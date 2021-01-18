package dev.mruniverse.pixelmotd.files;

import dev.mruniverse.pixelmotd.PixelBungee;
import dev.mruniverse.pixelmotd.PixelSpigot;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.utils.LoaderUtils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static dev.mruniverse.pixelmotd.utils.Logger.*;

public class Control {
    private Configuration pEditable,
            pTimer,
            pModules,
            pSettings,
            pWhitelist,
            pNormal,
            pCommand;

    private FileConfiguration rEditable,
            rModules,
            rSettings,
            rWhitelist,
            rNormal,
            rTimer,
            rCommand;

    // DONE
    private File newFile(String file) {
        if (!LoaderUtils.isBungee) {
            return new File(PixelSpigot.getInstance().getDataFolder(), file + ".yml");
        }
        return new File(PixelBungee.getInstance().getDataFolder(), file + ".yml");
    }

    private File getFile(Files fileToGet) {
        switch (fileToGet) {
            case NORMAL_MOTD:
                return newFile("normal-motd");
            case COMMAND:
                return newFile("command");
            case WHITELIST_MOTD:
                return newFile("whitelist-motd");
            case EDITABLE:
                return newFile("edit");
            case TIMER_MOTD:
                return newFile("timer-motd");
            case MODULES:
                return newFile("modules");
            default:
                return newFile("settings");
        }
    }

    /**
     * Gets file manager instance for Bungee or Spigot
     * @return file manager.
     */
    private FileManager getFileManager() {
        if (!LoaderUtils.isBungee) {
            return PixelSpigot.getInstance().getFiles();
        }
        return PixelBungee.getInstance().getFiles();
    }

    public FileConfiguration getSpigotControl(Files fileToControl) {
        switch (fileToControl) {
            case WHITELIST_MOTD:
                if (rWhitelist == null) reloadFiles();
                return rWhitelist;
            case COMMAND:
                if (rCommand == null) reloadFiles();
                return rCommand;
            case NORMAL_MOTD:
                if (rNormal == null) reloadFiles();
                return rNormal;
            case TIMER_MOTD:
                if (rTimer == null) reloadFiles();
                return rTimer;
            case EDITABLE:
                if (rEditable == null) reloadFiles();
                return rEditable;
            case MODULES:
                if (rModules == null) reloadFiles();
                return rModules;
            case SETTINGS:
                if (rSettings == null) reloadFiles();
                return rSettings;
            default:
                warn("Plugin can't load/save configuration files.");
                return rSettings;
        }
    }

    public Configuration getBungeeControl(Files fileToControl) {
        switch (fileToControl) {
            case SETTINGS:
                if (pSettings == null) reloadFiles();
                return pSettings;
            case MODULES:
                if (pModules == null) reloadFiles();
                return pModules;
            case EDITABLE:
                if (pEditable == null) reloadFiles();
                return pEditable;
            case COMMAND:
                if (pCommand == null) reloadFiles();
                return pCommand;
            case NORMAL_MOTD:
                if (pNormal == null) reloadFiles();
                return pNormal;
            case TIMER_MOTD:
                if (pTimer == null) reloadFiles();
                return pTimer;
            case WHITELIST_MOTD:
                if (pWhitelist == null) reloadFiles();
                return pWhitelist;
            default:
                warn("Plugin can't load/save configuration files.");
                return pSettings;
        }
    }

    public void reloadFiles() {
        try {
            if (!LoaderUtils.isBungee) {
                getFileManager().loadFiles();

                rEditable  = bukkitFile(Files.EDITABLE);
                rCommand   = bukkitFile(Files.COMMAND);
                rModules   = bukkitFile(Files.MODULES);
                rSettings  = bukkitFile(Files.SETTINGS);
                rWhitelist = bukkitFile(Files.WHITELIST_MOTD);
                rNormal    = bukkitFile(Files.NORMAL_MOTD);
                rTimer     = bukkitFile(Files.TIMER_MOTD);
                return;
            }

            pCommand   = proxyFile(Files.COMMAND);
            pTimer     = proxyFile(Files.TIMER_MOTD);
            pEditable  = proxyFile(Files.EDITABLE);
            pModules   = proxyFile(Files.MODULES);
            pSettings  = proxyFile(Files.SETTINGS);
            pWhitelist = proxyFile(Files.WHITELIST_MOTD);
            pNormal    = proxyFile(Files.NORMAL_MOTD);

        } catch (IOException exp) {
            // TODO Remove Pixel MOTD Prefix
            info("The plugin can't load or save configuration files! (Bungee | Spigot Control Issue - Caused by: IO Exception)");
            if(isDetailed()) {
                error("&a[Pixel MOTD] [Detailed Error] Information: ");
                //if(exp.getCause().toString() != null) {
                //    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Cause: " + exp.getCause().toString());
                //}
                if(exp.getMessage() != null) {
                    error("&a[Pixel MOTD] Message: " + exp.getMessage());
                }
                if(exp.getLocalizedMessage() != null) {
                    error("&a[Pixel MOTD] LocalizedMessage: " + exp.getLocalizedMessage());
                }
                if(exp.getStackTrace() != null) {
                    error("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : exp.getStackTrace()) {
                        error("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }

                exp.getSuppressed();
                error("&a[Pixel MOTD] Suppressed: " + Arrays.toString(exp.getSuppressed()));
                error("&a[Pixel MOTD] Class: " + exp.getClass().getName() +".class");
                error("&a[Pixel MOTD] Plugin version:" + PixelBungee.getInstance().getDescription().getVersion());
                error("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
        }
    }

    private Configuration proxyFile (Files type) throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(type));
    }

    private FileConfiguration bukkitFile (Files type) {
        return org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(getFileManager().getFile(type));
    }

    public boolean isDetailed() {
        if (!LoaderUtils.isBungee) {
            return getSpigotControl(Files.SETTINGS).getBoolean("settings.show-detailed-errors");
        }
        return getBungeeControl(Files.SETTINGS).getBoolean("settings.show-detailed-errors");
    }


}
