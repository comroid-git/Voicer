package de.comroid.tempvoicer.voicer;

import java.util.concurrent.CompletableFuture;

import de.comroid.Voicer;

import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class Session {
    private final long voiceId;
    private final long owner;

    public Session(Server server, User owner) {
        long categoryId = Voicer.PROP.getProperty("voicer.category.id").getValue(server).asLong();

        ServerVoiceChannelBuilder voiceChannelBuilder = server.createVoiceChannelBuilder()
                .setName(owner.getDisplayName(server));
        if (categoryId != -1) Voicer.API.getChannelById(categoryId)
                .flatMap(Channel::asChannelCategory)
                .ifPresent(voiceChannelBuilder::setCategory);
        this.voiceId = voiceChannelBuilder.create()
                .thenApply(DiscordEntity::getId)
                .join();

        this.owner = owner.getId();
    }

    public boolean close(long by) {
        if (by != owner) return false;

        Voicer.API.getChannelById(voiceId)
                .flatMap(Channel::asServerVoiceChannel)
                .map(ServerChannel::delete)
                .ifPresent(CompletableFuture::join);
        return true;
    }

    public ServerVoiceChannel getChannel() {
        return Voicer.API.getChannelById(voiceId)
                .flatMap(Channel::asServerVoiceChannel)
                .orElseThrow(() -> new AssertionError("Channel could not be found!"));
    }
}
