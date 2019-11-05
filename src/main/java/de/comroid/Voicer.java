package de.comroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.comroid.tempvoicer.SessionManager;
import de.comroid.tempvoicer.commands.AdminCommands;
import de.comroid.tempvoicer.commands.BasicCommands;
import de.comroid.tempvoicer.commands.VoicerCommands;
import de.comroid.util.files.FileProvider;
import de.comroid.util.files.OSValidator;
import de.kaleidox.botstats.BotListSettings;
import de.kaleidox.botstats.javacord.JavacordStatsClient;
import de.kaleidox.botstats.model.StatsClient;
import de.kaleidox.javacord.util.commands.CommandHandler;
import de.kaleidox.javacord.util.server.properties.ServerPropertiesManager;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.util.logging.ExceptionLogger;

public final class Voicer {
    public static final DiscordApi API;
    public static final CommandHandler CMD;
    public static final StatsClient STATS;
    public static final ServerPropertiesManager PROP;

    static {
        try {
            File file = FileProvider.getFile("login/token.cred");
            System.out.println("Looking for token file at " + file.getAbsolutePath());
            API = new DiscordApiBuilder()
                    .setToken(new BufferedReader(new FileReader(file)).readLine())
                    .login()
                    .exceptionally(ExceptionLogger.get())
                    .join();

            API.updateStatus(UserStatus.DO_NOT_DISTURB);
            API.updateActivity("Booting up...");

            STATS = new JavacordStatsClient(BotListSettings.builder()
                    .postStatsTester(OSValidator::isUnix)
                    .tokenFile(FileProvider.getFile("login/botLists.properties"))
                    .build(), API);
            API.getThreadPool().getScheduler()
                    .scheduleAtFixedRate(() -> {
                        try {
                            STATS.updateTokensFromFile();
                        } catch (IOException e) {
                            throw new RuntimeException("Updating Stat Tokens", e);
                        }
                    }, 1, 1, TimeUnit.DAYS);

            CMD = new CommandHandler(API);
            CMD.prefixes = new String[]{"voice!"};
            CMD.useDefaultHelp(null);
            CMD.registerCommands(BasicCommands.INSTANCE);
            CMD.registerCommands(VoicerCommands.INSTANCE);
            CMD.registerCommands(AdminCommands.INSTANCE);

            PROP = new ServerPropertiesManager(FileProvider.getFile("data/properties.json"));
            PROP.usePropertyCommand(null, CMD);
            PROP.register("bot.prefix", CMD.prefixes[0])
                    .setDisplayName("Custom Command Prefix")
                    .setDescription("A custom prefix to call bot commands with");
            PROP.register("voicer.lobby.id", -1L)
                    .setDisplayName("ID of the lobby-channel")
                    .setDescription("The ID of the channel to be used as the lobby channel");
            PROP.register("voicer.category.id", -1L)
                    .setDisplayName("ID of the category to create the channels in")
                    .setDescription("The ID of the category in which the channels should be created");

            CMD.useCustomPrefixes(PROP.getProperty("bot.prefix"), false);

            API.getThreadPool()
                    .getScheduler()
                    .scheduleAtFixedRate(() -> {
                        try {
                            PROP.storeData();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, 5, 5, TimeUnit.MINUTES);
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(SessionManager.INSTANCE::close));

            API.updateStatus(UserStatus.ONLINE);
            API.updateActivity(ActivityType.LISTENING, "voice!help");
        } catch (Exception e) {
            throw new RuntimeException("Error in initializer", e);
        }
    }

    public static void main(String[] args) {
        API.addListener(SessionManager.INSTANCE);
    }
}
