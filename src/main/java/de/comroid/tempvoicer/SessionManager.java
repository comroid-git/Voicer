package de.comroid.tempvoicer;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import de.comroid.Voicer;
import de.comroid.tempvoicer.voicer.Session;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberJoinEvent;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberLeaveEvent;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberJoinListener;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberLeaveListener;

public enum SessionManager implements Closeable, ServerVoiceChannelMemberJoinListener, ServerVoiceChannelMemberLeaveListener {
    INSTANCE;

    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void close() {
        sessions.values()
                .stream()
                .distinct()
                .map(Session::getChannel)
                .forEach(ServerVoiceChannel::delete);
    }

    @Override
    public void onServerVoiceChannelMemberJoin(ServerVoiceChannelMemberJoinEvent event) {
        Server server = event.getServer();
        User user = event.getUser();

        if (Voicer.PROP.getProperty("voicer.lobby.id")
                .getValue(server)
                .asLong() == event.getChannel().getId()) {
            // joined into lobby

            Session session = new Session(server, user);
            server.moveUser(user, session.getChannel());
            sessions.put(user.getId(), session);
        }
    }

    @Override
    public void onServerVoiceChannelMemberLeave(ServerVoiceChannelMemberLeaveEvent event) {
        if (!isManaged(event.getChannel())) return;

        if (event.getChannel().getConnectedUsers().size() == 0) event.getChannel().delete();
    }

    public Optional<Session> getSession(User user) {
        return Optional.ofNullable(sessions.get(user.getId()));
    }

    private boolean isManaged(ServerVoiceChannel channel) {
        return sessions.values()
                .stream()
                .distinct()
                .anyMatch(session -> session.getChannel().equals(channel));
    }
}
