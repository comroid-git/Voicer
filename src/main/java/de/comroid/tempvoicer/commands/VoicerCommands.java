package de.comroid.tempvoicer.commands;

import java.util.concurrent.CompletableFuture;

import de.comroid.tempvoicer.SessionManager;
import de.comroid.tempvoicer.voicer.Session;
import de.kaleidox.javacord.util.commands.Command;
import de.kaleidox.javacord.util.commands.CommandGroup;

import org.javacord.api.entity.channel.ServerVoiceChannelUpdater;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.user.User;

@CommandGroup(name = "Main TempVoicer Commands", description = "Main commands for using TempVoicer", ordinal = 0)
public enum VoicerCommands {
    INSTANCE;

    @Command
    public void lock(User user) {
        SessionManager.INSTANCE.getSession(user)
                .map(Session::getChannel)
                .map(svc -> svc.createUpdater().addPermissionOverwrite(
                        svc.getServer().getEveryoneRole(),
                        Permissions.fromBitmask(0, PermissionType.CONNECT.getValue())).update())
                .ifPresent(CompletableFuture::join);
    }

    @Command
    public void unlock(User user) {
        SessionManager.INSTANCE.getSession(user)
                .map(Session::getChannel)
                .map(svc -> svc.createUpdater().addPermissionOverwrite(
                        svc.getServer().getEveryoneRole(),
                        Permissions.fromBitmask(PermissionType.CONNECT.getValue(), 0)).update())
                .ifPresent(CompletableFuture::join);
    }

    @Command(requiredArguments = 1)
    public void name(User user, String[] args) {
        SessionManager.INSTANCE.getSession(user)
                .map(Session::getChannel)
                .map(svc -> svc.updateName(args[0]))
                .ifPresent(CompletableFuture::join);
    }

    @Command(requiredArguments = 1)
    public void limit(User user, String[] args) {
        if (!args[0].matches("[0-9]+"))
            throw new IllegalArgumentException("No valid number was entered!");
        SessionManager.INSTANCE.getSession(user)
                .map(Session::getChannel)
                .map(svc -> svc.updateUserLimit(Integer.parseInt(args[0])))
                .ifPresent(CompletableFuture::join);
    }

    @Command(requiredUserMentions = 1)
    public void permit(User user, Message message) {
        SessionManager.INSTANCE.getSession(user)
                .map(Session::getChannel)
                .map(svc -> {
                    ServerVoiceChannelUpdater channelUpdater = svc.createUpdater()
                            .addPermissionOverwrite(
                                    svc.getServer().getEveryoneRole(),
                                    Permissions.fromBitmask(0, PermissionType.CONNECT.getValue()));
                    message.getMentionedUsers().forEach(usr -> channelUpdater.addPermissionOverwrite(
                            message.getMentionedUsers().get(0),
                            Permissions.fromBitmask(PermissionType.CONNECT.getValue(), 0)));
                    return channelUpdater.update();
                })
                .ifPresent(CompletableFuture::join);
    }

    @Command(requiredUserMentions = 1)
    public void reject(User user, Message message) {
        SessionManager.INSTANCE.getSession(user)
                .map(Session::getChannel)
                .map(svc -> {
                    ServerVoiceChannelUpdater channelUpdater = svc.createUpdater();
                    for (User rem : message.getMentionedUsers()) channelUpdater.removePermissionOverwrite(rem);
                    return channelUpdater.update();
                })
                .ifPresent(CompletableFuture::join);
    }
}
