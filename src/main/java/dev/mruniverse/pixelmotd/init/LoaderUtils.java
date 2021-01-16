package dev.mruniverse.pixelmotd.init;

import dev.mruniverse.pixelmotd.bstats.BukkitMetrics;
import dev.mruniverse.pixelmotd.bstats.BungeeMetrics;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.listeners.BungeeEvents;
import dev.mruniverse.pixelmotd.listeners.BungeeMotd;
import dev.mruniverse.pixelmotd.listeners.SpigotEvents;
import dev.mruniverse.pixelmotd.utils.PixelUpdater;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class LoaderUtils {
    private final boolean isBungee;
    private boolean control;

    /**
     * Specify if it's Bungee or Spigot
     * when initialize this class constructor.
     *
     * @param isBungee spigot if false.
     */
    public LoaderUtils (boolean isBungee) {
        this.isBungee = isBungee;

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
                    sendConsole("&aYou're using latest version of PixelMOTD, You're Awesome!");
                    switch (versionResult.toUpperCase()) {
                        case "RED_PROBLEM":
                            sendConsole("&aPixelMOTD can't connect to WiFi to check plugin version.");
                            break;
                        case "PRE_ALPHA_VERSION":
                            sendConsole("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                            break;
                        case "ALPHA_VERSION":
                            sendConsole("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                            break;
                        case "RELEASE":
                            sendConsole("&aYou are Running a &bRelease Version&a, this is a stable version, awesome!");
                            break;
                        case "PRE_RELEASE":
                            sendConsole("&aYou are Running a &bPreRelease Version&a, this is a stable version but is not the final version or don't have finished all things of the final version, but is a stable version,awesome!");
                            break;
                        default:
                            sendConsole("DEBUG... 1");
                            break;
                    }
                    break;
                case "NEW_VERSION":
                    sendConsole("&aA new update is available: &bhttps://www.spigotmc.org/resources/37177/");
                    break;
                case "BETA_VERSION":
                    sendConsole("&aYou are Running a Pre-Release version, please report bugs ;)");
                    break;
                case "RED_PROBLEM":
                    sendConsole("&aPixelMOTD can't connect to WiFi to check plugin version.");
                    break;
                case "ALPHA_VERSION":
                    sendConsole("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                    break;
                case "PRE_ALPHA_VERSION":
                    sendConsole("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                    break;
                default:
                    sendConsole("DEBUG... 2");
                    break;
            }
        }
    }

    /**
     * Load it on the onEnable method.
     */
    public void loadMetrics() {
        if (!isBungee) {
            BukkitMetrics bukkitMetrics = new BukkitMetrics(SpigotPixel.getInstance(), 8509);
            debug(String.format("Spigot metrics has been enabled &7(%s)", bukkitMetrics.isEnabled()));
            return;
        }

        BungeeMetrics bungeeMetrics = new BungeeMetrics(BungeePixel.getInstance(), 8509);
        debug(String.format("Proxy metrics has been enabled &7(%s)", bungeeMetrics.isEnabled()));
    }

    public void registerListeners() {
        if (!isBungee) {
            new SpigotEvents(SpigotPixel.getInstance());
            debug("Spigot listener has been loaded.");
            return;
        }

        new BungeeEvents(BungeePixel.getInstance());
        new BungeeMotd(BungeePixel.getInstance());
        debug("Proxy listeners has been loaded.");
    }

    /**
     * Sends a message depending on isBungee
     * boolean value.
     *
     * @param message string to send on.
     */
    private void sendConsole(String message) {
        if (!isBungee) {
            Bukkit.getServer().getConsoleSender().sendMessage(color("&b[Pixel MOTD] &f" + message));
            return;
        }

        CommandSender bungeeConsole = BungeePixel.getInstance().getProxy().getConsole();
        bungeeConsole.sendMessage(new TextComponent(color("&b[Pixel MOTD] &f" + message)));
    }

    private void debug(String message) {
        if (!isBungee) {
            Bukkit.getServer().getConsoleSender().sendMessage(color("&9[DEBUG &7| Pixel MOTD] &f" + message));
            return;
        }

        CommandSender bungeeConsole = BungeePixel.getInstance().getProxy().getConsole();
        bungeeConsole.sendMessage(new TextComponent(color("&9[DEBUG &7| Pixel MOTD] &f" + message)));
    }

    /**
     * Colorize a string provided to method
     *
     * @param message Message to transform.
     * @return transformed message with colors.
     */
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }
}
