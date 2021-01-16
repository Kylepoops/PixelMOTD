package dev.mruniverse.pixelmotd.utils;

import dev.mruniverse.pixelmotd.PixelBungee;
import dev.mruniverse.pixelmotd.PixelSpigot;
import dev.mruniverse.pixelmotd.bstats.BukkitMetrics;
import dev.mruniverse.pixelmotd.bstats.BungeeMetrics;
import dev.mruniverse.pixelmotd.commands.BungeeCMD;
import dev.mruniverse.pixelmotd.commands.SpigotCMD;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.listeners.BungeeEvents;
import dev.mruniverse.pixelmotd.listeners.BungeeMotd;
import dev.mruniverse.pixelmotd.listeners.SpigotEvents;

import java.util.List;

import static dev.mruniverse.pixelmotd.utils.Logger.debug;
import static dev.mruniverse.pixelmotd.utils.Logger.info;

public class LoaderUtils {
    public static boolean isBungee;
    private final boolean control;

    /**
     * Specify if it's Bungee or Spigot
     * when initialize this class constructor.
     *
     * @param isBungee spigot if false.
     */
    public LoaderUtils (boolean isBungee) {
        LoaderUtils.isBungee = isBungee;

        if (isBungee) {
            control = BungeeControl.getControl(Files.SETTINGS).getBoolean("settings.update-check");
            return;
        }
        control = SpigotControl.getControl(Files.SETTINGS).getBoolean("settings.update-check");
    }

    public void pluginUpdater() {
        if (control) {
            PixelUpdater updater = new PixelUpdater(isBungee, 37177);
            String updaterResult = updater.getUpdateResult();
            String versionResult = updater.getVersionResult();

            switch (updaterResult.toUpperCase()) {
                case "UPDATED":
                    info("&aYou're using latest version of PixelMOTD, You're Awesome!");
                    switch (versionResult.toUpperCase()) {
                        case "RED_PROBLEM":
                            info("&aPixelMOTD can't connect to WiFi to check plugin version.");
                            break;
                        case "PRE_ALPHA_VERSION":
                            info("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                            break;
                        case "ALPHA_VERSION":
                            info("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                            break;
                        case "RELEASE":
                            info("&aYou are Running a &bRelease Version&a, this is a stable version, awesome!");
                            break;
                        case "PRE_RELEASE":
                            info("&aYou are Running a &bPreRelease Version&a, this is a stable version but is not the final version or don't have finished all things of the final version, but is a stable version,awesome!");
                            break;
                        default:
                            info("DEBUG... 1");
                            break;
                    }
                    break;
                case "NEW_VERSION":
                    info("&aA new update is available: &bhttps://www.spigotmc.org/resources/37177/");
                    break;
                case "BETA_VERSION":
                    info("&aYou are Running a Pre-Release version, please report bugs ;)");
                    break;
                case "RED_PROBLEM":
                    info("&aPixelMOTD can't connect to WiFi to check plugin version.");
                    break;
                case "ALPHA_VERSION":
                    info("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                    break;
                case "PRE_ALPHA_VERSION":
                    info("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                    break;
                default:
                    info("DEBUG... 2");
                    break;
            }
        }
    }

    /**
     * Load it on the onEnable method.
     */
    public void loadMetrics() {
        if (!isBungee) {
            BukkitMetrics bukkitMetrics = new BukkitMetrics(PixelSpigot.getInstance(), 8509);
            debug(String.format("Spigot metrics has been enabled &7(%s)", bukkitMetrics.isEnabled()));
            return;
        }

        BungeeMetrics bungeeMetrics = new BungeeMetrics(PixelBungee.getInstance(), 8509);
        debug(String.format("Proxy metrics has been enabled &7(%s)", bungeeMetrics.isEnabled()));
    }

    /**
     * Register it on the onEnable method.
     */
    public void registerListeners() {
        if (!isBungee) {
            new SpigotEvents(PixelSpigot.getInstance());
            debug("Spigot listener has been loaded.");
            return;
        }

        new BungeeEvents(PixelBungee.getInstance());
        new BungeeMotd(PixelBungee.getInstance());
        debug("Proxy listeners has been loaded.");
    }

    public void registerCommands() {
        if (!isBungee) {
            PixelSpigot plugin = PixelSpigot.getInstance();
            new SpigotCMD(plugin, "pixelmotd");
            new SpigotCMD(plugin, "pmotd");
            debug("Spigot commands has been registered.");
            return;
        }

        PixelBungee plugin = PixelBungee.getInstance();
        List<String> cmdList = BungeeControl.getControl(Files.COMMAND).getStringList("command.list");

        for (String command : cmdList) {
            plugin.getProxy().getPluginManager().registerCommand(plugin, new BungeeCMD(command));
        }

        debug("Proxy commands has been registered.");
    }
}
