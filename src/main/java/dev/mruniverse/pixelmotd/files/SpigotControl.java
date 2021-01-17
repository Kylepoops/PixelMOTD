package dev.mruniverse.pixelmotd.files;

import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.MotdType;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.PixelSpigot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static dev.mruniverse.pixelmotd.utils.spigotLogger.info;

public class SpigotControl {
    private final PixelSpigot plugin;
    public SpigotControl(PixelSpigot main) {
        plugin = main;
    }

    private FileConfiguration rEditable, rModules, rSettings, rWhitelist, rNormal,rTimer,rCommand;
    public void reloadFiles() {
        plugin.getFiles().loadFiles();
        rEditable = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.EDITABLE));
        rCommand = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.COMMAND));
        rModules = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.MODULES));
        rSettings = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.SETTINGS));
        rWhitelist = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.WHITELIST_MOTD));
        rNormal = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.NORMAL_MOTD));
        rTimer = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.TIMER_MOTD));
    }
    public boolean isCommandEnabled() {
        return true;
    }
    public FileConfiguration getControl(Files fileToControl) {
        if(fileToControl.equals(Files.WHITELIST_MOTD)) {
            if(rWhitelist == null) reloadFiles();
            return rWhitelist;
        }
        if(fileToControl.equals(Files.COMMAND)) {
            if(rCommand == null) reloadFiles();
            return rCommand;
        }
        if(fileToControl.equals(Files.NORMAL_MOTD)) {
            if(rNormal == null) reloadFiles();
            return rNormal;
        }
        if(fileToControl.equals(Files.TIMER_MOTD)) {
            if(rTimer == null) reloadFiles();
            return rTimer;
        }
        if(fileToControl.equals(Files.EDITABLE)) {
            if(rEditable == null) reloadFiles();
            return rEditable;
        }
        if(fileToControl.equals(Files.MODULES)) {
            if(rModules == null) reloadFiles();
            return rModules;
        }
        if(fileToControl.equals(Files.SETTINGS)) {
            if(rSettings == null) reloadFiles();
            return rSettings;
        }
        info("The plugin can't load or save configuration files! (Spigot Control Issue - Caused by: One plugin is using bad the <getControl() from FileManager.class>)");
        return rSettings;
    }
    public boolean getWhitelistStatus() {
        return getControl(Files.EDITABLE).getBoolean("whitelist.toggle");
    }
    public MotdType getMotdType(boolean whitelistStatus) {
        if(whitelistStatus) {
            return MotdType.WHITELIST_MOTD;
        }
        return MotdType.NORMAL_MOTD;
    }
    public String getMotd(boolean isWhitelistMotd) {
        List<String> motdToGet = new ArrayList<>();
        if(isWhitelistMotd) {
            motdToGet.addAll(Objects.requireNonNull(getControl(Files.WHITELIST_MOTD).getConfigurationSection("whitelist")).getKeys(false));
            return motdToGet.get(new Random().nextInt(motdToGet.size()));
        }
        motdToGet.addAll(Objects.requireNonNull(getControl(Files.NORMAL_MOTD).getConfigurationSection("normal")).getKeys(false));
        return motdToGet.get(new Random().nextInt(motdToGet.size()));

    }
    public String getWorlds(String msg) throws ParseException {
        if(msg.contains("%online_")) {
            for (World world : PixelSpigot.getInstance().getServer().getWorlds()) {
                msg = msg.replace("%online_" + world.getName() + "%", world.getPlayers().size() + "");
            }
        }
        return replaceEventInfo(msg);
    }

    public Date getEventDate(String eventName) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(getControl(Files.SETTINGS).getString("events." + eventName + ".TimeZone")));
        return format.parse(getControl(Files.SETTINGS).getString("events." + eventName + ".eventDate"));
    }

    public String replaceEventInfo(String motdLineOrHoverLine) throws ParseException {
        if(motdLineOrHoverLine.contains("%event_")) {
            Date CurrentDate;
            CurrentDate = new Date();
            for(String event : Objects.requireNonNull(getControl(Files.SETTINGS).getConfigurationSection("events")).getKeys(false)) {
                String TimeLeft = "<Invalid format-Type>";
                long difference = getEventDate(event).getTime() - CurrentDate.getTime();
                if(difference >= 0L) {
                    if(Objects.requireNonNull(getControl(Files.SETTINGS).getString("events." + event + ".format-Type")).equalsIgnoreCase("FIRST")) {
                        TimeLeft = convertToFinalResult(difference,"FIRST");
                    } else if(Objects.requireNonNull(getControl(Files.SETTINGS).getString("events." + event + ".format-Type")).equalsIgnoreCase("SECOND")) {
                        TimeLeft = convertToFinalResult(difference, "SECOND");
                    } else if(Objects.requireNonNull(getControl(Files.SETTINGS).getString("events." + event + ".format-Type")).equalsIgnoreCase("THIRD")) {
                        TimeLeft = convertToFinalResult(difference,"THIRD");
                    }
                } else {
                    TimeLeft = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getControl(Files.SETTINGS).getString("events." + event + ".endMessage")));
                }
                motdLineOrHoverLine = motdLineOrHoverLine.replace("%event_" + event +  "_name%", Objects.requireNonNull(getControl(Files.SETTINGS).getString("events." + event + ".eventName")))
                        .replace("%event_" + event + "_TimeZone%", Objects.requireNonNull(getControl(Files.SETTINGS).getString("events." + event + ".TimeZone")))
                        .replace("%event_" + event + "_TimeLeft%",TimeLeft);
            }
        }
        return motdLineOrHoverLine;
    }

    public String convertToFinalResult(long time,String formatType) {
        StringJoiner joiner = new StringJoiner(" ");
        if (formatType.equalsIgnoreCase("SECOND")) {
            long seconds = time / 1000;
            int unitValue = Math.toIntExact(seconds / TimeUnit.DAYS.toSeconds(7));
            if (unitValue > 0) {
                seconds %= TimeUnit.DAYS.toSeconds(7);
                joiner.add(unitValue + ":");
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.DAYS.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.DAYS.toSeconds(1);
                joiner.add(unitValue + ":");
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.HOURS.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.HOURS.toSeconds(1);
                joiner.add(unitValue + ":");
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.MINUTES.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.MINUTES.toSeconds(1);
                joiner.add(unitValue + ":");
            }
            if (seconds > 0 || joiner.length() == 0) {
                joiner.add(seconds + "");
            }

        } else if(formatType.equalsIgnoreCase("FIRST")) {
            long seconds = time / 1000;
            String unit;
            int unitValue = Math.toIntExact(seconds / TimeUnit.DAYS.toSeconds(7));
            if (unitValue > 0) {
                seconds %= TimeUnit.DAYS.toSeconds(7);
                if (unitValue == 1) {
                    unit = getControl(Files.SETTINGS).getString("timings.week");
                } else {
                    unit = getControl(Files.SETTINGS).getString("timings.weeks");
                }
                joiner.add(unitValue + " " + unit);
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.DAYS.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.DAYS.toSeconds(1);
                if (unitValue == 1) {
                    unit = getControl(Files.SETTINGS).getString("timings.day");
                } else {
                    unit = getControl(Files.SETTINGS).getString("timings.days");
                }
                joiner.add(unitValue + " " + unit);
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.HOURS.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.HOURS.toSeconds(1);
                if (unitValue == 1) {
                    unit = getControl(Files.SETTINGS).getString("timings.hour");
                } else {
                    unit = getControl(Files.SETTINGS).getString("timings.hours");
                }

                joiner.add(unitValue + " " + unit);
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.MINUTES.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.MINUTES.toSeconds(1);
                if (unitValue == 1) {
                    unit = getControl(Files.SETTINGS).getString("timings.minute");
                } else {
                    unit = getControl(Files.SETTINGS).getString("timings.minutes");
                }

                joiner.add(unitValue + " " + unit);
            }
            if (seconds > 0 || joiner.length() == 0) {
                if (seconds == 1) {
                    unit = getControl(Files.SETTINGS).getString("timings.second");
                } else {
                    unit = getControl(Files.SETTINGS).getString("timings.seconds");
                }

                joiner.add(seconds + " " + unit);
            }
        } else if(formatType.equalsIgnoreCase("THIRD")) {
            long seconds = time / 1000;
            int unitValue = Math.toIntExact(seconds / TimeUnit.DAYS.toSeconds(7));
            if (unitValue > 0) {
                seconds %= TimeUnit.DAYS.toSeconds(7);
                joiner.add(unitValue + "w,");
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.DAYS.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.DAYS.toSeconds(1);
                joiner.add(unitValue + "d,");
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.HOURS.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.HOURS.toSeconds(1);
                joiner.add(unitValue + "h,");
            }
            unitValue = Math.toIntExact(seconds / TimeUnit.MINUTES.toSeconds(1));
            if (unitValue > 0) {
                seconds %= TimeUnit.MINUTES.toSeconds(1);
                joiner.add(unitValue + "m,");
            }
            if (seconds > 0 || joiner.length() == 0) {
                joiner.add(seconds + "s.");
            }
        }
        if(formatType.equalsIgnoreCase("SECOND")) {
            return joiner.toString().replace(" ","");
        } else {
            return joiner.toString();
        }
    }
    public boolean pendingPath(MotdType motdType,String motdName) {
        String initial = "timers.";
        Files fileS = Files.TIMER_MOTD;
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            initial = "normal.";
            fileS = Files.NORMAL_MOTD;
        }
        if(motdType.equals(MotdType.WHITELIST_MOTD)) {
            initial = "whitelist.";
            fileS = Files.WHITELIST_MOTD;
        }
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            if(getControl(Files.NORMAL_MOTD).get(initial + motdName + ".enabled") == null) return true;
        }
        if(getControl(fileS).get(initial + motdName + ".line1") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".line2") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customHover.toggle") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customHover.hover") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customIcon.toggle") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customIcon.customFile") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customProtocol.toggle") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customProtocol.changeProtocolVersion") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customProtocol.protocol") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customHexMotd.toggle") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customHexMotd.line1") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customHexMotd.line2") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customOnlinePlayers.toggle") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customOnlinePlayers.mode") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customOnlinePlayers.values") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customMaxPlayers.toggle") == null) return true;
        if(getControl(fileS).get(initial + motdName + ".otherSettings.customMaxPlayers.mode") == null) return true;
        return getControl(fileS).get(initial + motdName + ".otherSettings.customMaxPlayers.values") == null;
    }
    public void loadMotdPath(MotdType motdType,String motdName) {
        if(pendingPath(motdType,motdName)) {
            List<Object> stringList = new ArrayList<>();
            if (motdType.equals(MotdType.WHITELIST_MOTD)) {
                stringList.add("     &c&lPIXEL MOTD");
                stringList.add("&7SpigotMC Plugin v%plugin_version%");
                stringList.add("");
                stringList.add("&c&lInformation:");
                stringList.add("  &7Whitelist by: &f%whitelist_author%");
                stringList.add("  &7Spigot ID: &f37177");
                stringList.add("  &7Discord: &fMrUniverse#2556");
                stringList.add("  &7Online: &f%online%");
                stringList.add("  &frigox.club/discord/dev");
                stringList.add("");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".line1", "&8» &aPixelMOTD v%plugin_version% &7| &aSpigotMC");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".line2", "&f&oThis server is in whitelist. (1.8-1.15 Motd)");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customHover.toggle", true);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customHover.hover", stringList);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customIcon.toggle", true);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customIcon.customFile", false);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customProtocol.toggle", true);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customProtocol.changeProtocolVersion", false);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customProtocol.protocol", "PixelMotd Security");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customHexMotd.toggle", true);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customHexMotd.line1", "&8» &cPixelMOTD v%plugin_version% &7| &cSpigotMC");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customHexMotd.line2", "&f&oWhitelist Mode (1.16+ Motd)");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customOnlinePlayers.toggle", false);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customOnlinePlayers.mode", "HALF-ADD");
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customMaxPlayers.toggle", true);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customMaxPlayers.mode", "HALF");
                stringList = new ArrayList<>();
                stringList.add(2021);
                stringList.add(2022);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customOnlinePlayers.values", stringList);
                plugin.getFiles().addConfig(Files.WHITELIST_MOTD, "whitelist." + motdName + ".otherSettings.customMaxPlayers.values", stringList);
                return;
            }
            if (motdType.equals(MotdType.NORMAL_MOTD)) {
                stringList.add("     &9&lPIXEL MOTD");
                stringList.add("&7SpigotMC Plugin v%plugin_version%");
                stringList.add("");
                stringList.add("&b&lInformation:");
                stringList.add("  &7Version: &f%plugin_version%");
                stringList.add("  &7Spigot ID: &f37177");
                stringList.add("  &7Discord: &fMrUniverse#2556");
                stringList.add("  &7Online: &f%online%&7/&f%max%");
                stringList.add("  &frigox.club/discord/dev");
                stringList.add("");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".line1", "&b&lPixelMOTD v%plugin_version%");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".line2", "&f&oThis motd only appear for 1.8 - 1.15");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customHover.toggle", true);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customHover.hover", stringList);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customIcon.toggle", true);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customIcon.customFile", false);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customProtocol.toggle", true);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customProtocol.changeProtocolVersion", false);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customProtocol.protocol", "PixelMotd System");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customHexMotd.toggle", true);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customHexMotd.line1", "&b&lPixelMOTD v%plugin_version%");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customHexMotd.line2", "&f&oThis motd only appear for 1.16+");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customOnlinePlayers.toggle", false);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customOnlinePlayers.mode", "HALF-ADD");
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customMaxPlayers.toggle", true);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customMaxPlayers.mode", "HALF-ADD");
                stringList = new ArrayList<>();
                stringList.add(2021);
                stringList.add(2022);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customOnlinePlayers.values", stringList);
                plugin.getFiles().addConfig(Files.NORMAL_MOTD, "normal." + motdName + ".otherSettings.customMaxPlayers.values", stringList);
                return;
            }
            stringList.add("     &9&lPIXEL MOTD");
            stringList.add("&7This is a timer motd");
            stringList.add("&7When you enable 1 motd");
            stringList.add("&7And you have 1 event with");
            stringList.add("&7the same name it will be");
            stringList.add("&7Sync. And when an event");
            stringList.add("&7End, it will execute commands");
            stringList.add("&7By the console automatically!");
            stringList.add("&frigox.club/discord/dev");
            stringList.add("");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".enabled", false);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".line1", "&6&l%event_timeLeft%");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".line2", "&f&oThis is a timer motd");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customHover.toggle", true);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customHover.hover", stringList);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customIcon.toggle", true);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customIcon.customFile", false);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customProtocol.toggle", true);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customProtocol.changeProtocolVersion", false);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customProtocol.protocol", "PixelMotd System");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customHexMotd.toggle", true);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customHexMotd.line1", "&6&l%event_timeLeft%");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customHexMotd.line2", "&f&oThis motd only appear for 1.16+");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customOnlinePlayers.toggle", false);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customOnlinePlayers.mode", "HALF-ADD");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customMaxPlayers.toggle", true);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customMaxPlayers.mode", "HALF-ADD");
            stringList = new ArrayList<>();
            stringList.add("/pmotd whitelist off");
            stringList.add("/alert Maintenance off automatically!");
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".timerSettings.commandsToExecute", stringList);
            stringList = new ArrayList<>();
            stringList.add(2021);
            stringList.add(2022);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customOnlinePlayers.values", stringList);
            plugin.getFiles().addConfig(Files.TIMER_MOTD, "timers." + motdName + ".otherSettings.customMaxPlayers.values", stringList);
        }
    }
    public void loadMotdPaths(MotdType motdType) {
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            for (String motdName : Objects.requireNonNull(getControl(Files.NORMAL_MOTD).getConfigurationSection("normal")).getKeys(false)) {
                loadMotdPath(motdType,motdName);
            }
            return;
        }
        if(motdType.equals(MotdType.WHITELIST_MOTD)) {
            for (String motdName : Objects.requireNonNull(getControl(Files.WHITELIST_MOTD).getConfigurationSection("whitelist")).getKeys(false)) {
                loadMotdPath(motdType,motdName);
            }
            return;
        }
        for (String motdName : Objects.requireNonNull(getControl(Files.TIMER_MOTD).getConfigurationSection("timers")).getKeys(false)) {
            loadMotdPath(motdType,motdName);
        }
    }
    public boolean callMotds(MotdType motdType) {
        try {
            if (motdType.equals(MotdType.NORMAL_MOTD)) {
                return getControl(Files.NORMAL_MOTD).get("normal") == null;
            }
            if (motdType.equals(MotdType.WHITELIST_MOTD)) {
                return getControl(Files.WHITELIST_MOTD).get("whitelist") == null;
            }
            return getControl(Files.TIMER_MOTD).get("timers") == null;
        } catch(Throwable throwable) {
            return true;
        }
    }
    public boolean isDetailed() {
        return getControl(Files.SETTINGS).getBoolean("settings.show-detailed-errors");
    }
    public void reloadFile(SaveMode Mode) {
        plugin.getFiles().loadFiles();
        if(Mode.equals(SaveMode.EDITABLE) || Mode.equals(SaveMode.ALL)) {
            rEditable = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.EDITABLE));
        }
        if(Mode.equals(SaveMode.COMMAND) || Mode.equals(SaveMode.ALL)) {
            rCommand = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.COMMAND));
        }
        if(Mode.equals(SaveMode.MODULES) || Mode.equals(SaveMode.ALL)) {
            rModules = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.MODULES));
        }
        if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
            rSettings = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.SETTINGS));
        }
        if(Mode.equals(SaveMode.MOTDS) || Mode.equals(SaveMode.ALL)) {
            rWhitelist = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.WHITELIST_MOTD));
            rNormal = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.NORMAL_MOTD));
            rTimer = YamlConfiguration.loadConfiguration(plugin.getFiles().getFile(Files.TIMER_MOTD));
        }
    }
    public void save(SaveMode Mode) {
        try {
            if(Mode.equals(SaveMode.MODULES) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.MODULES).save(plugin.getFiles().getFile(Files.MODULES));
            }
            if(Mode.equals(SaveMode.TIMER_MOTD) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.TIMER_MOTD).save(plugin.getFiles().getFile(Files.TIMER_MOTD));
            }
            if(Mode.equals(SaveMode.COMMAND) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.COMMAND).save(plugin.getFiles().getFile(Files.COMMAND));
            }
            if(Mode.equals(SaveMode.EDITABLE) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.EDITABLE).save(plugin.getFiles().getFile(Files.EDITABLE));
            }
            if(Mode.equals(SaveMode.NORMAL_MOTD) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.NORMAL_MOTD).save(plugin.getFiles().getFile(Files.NORMAL_MOTD));
            }
            if(Mode.equals(SaveMode.WHITELIST_MOTD) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.WHITELIST_MOTD).save(plugin.getFiles().getFile(Files.WHITELIST_MOTD));
            }
            if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.SETTINGS).save(plugin.getFiles().getFile(Files.SETTINGS));
            }
            if(Mode.equals(SaveMode.MOTDS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.NORMAL_MOTD).save(plugin.getFiles().getFile(Files.NORMAL_MOTD));
                getControl(Files.WHITELIST_MOTD).save(plugin.getFiles().getFile(Files.WHITELIST_MOTD));
                getControl(Files.TIMER_MOTD).save(plugin.getFiles().getFile(Files.TIMER_MOTD));
            }
        } catch(IOException exception) {
            info("The plugin can't load or save configuration files! (Spigot Control Issue - Caused by: One plugin is using bad the <getControl() from FileManager.class>)");
        }
    }
    public String getWhitelistAuthor() {
        if(!Objects.requireNonNull(getControl(Files.EDITABLE).getString("whitelist.author")).equalsIgnoreCase("CONSOLE")) {
            return getControl(Files.EDITABLE).getString("whitelist.author");
        } else {
            if(getControl(Files.EDITABLE).getBoolean("whitelist.customConsoleName.toggle")) {
                return getControl(Files.EDITABLE).getString("whitelist.customConsoleName.name");
            }
            return "Console";
        }
    }
}