package dev.mruniverse.pixelmotd.files;

import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.MotdType;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.init.bungeePixelMOTD;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class bungeeControl {
    private static Configuration pEditable,pTimer, pModules ,pSettings, pWhitelist, pNormal,pCommand;

    private static File getFile(Files fileToGet) {
        if(fileToGet.equals(Files.NORMAL_MOTD)) {
            return new File(bungeePixelMOTD.getInstance().getDataFolder(), "normal-motd.yml");
        }
        if(fileToGet.equals(Files.COMMAND)) {
            return new File(bungeePixelMOTD.getInstance().getDataFolder(), "command.yml");
        }
        if(fileToGet.equals(Files.WHITELIST_MOTD)) {
            return new File(bungeePixelMOTD.getInstance().getDataFolder(), "whitelist-motd.yml");
        }
        if(fileToGet.equals(Files.EDITABLE)) {
            return new File(bungeePixelMOTD.getInstance().getDataFolder(), "edit.yml");
        }
        if(fileToGet.equals(Files.TIMER_MOTD)) {
            return new File(bungeePixelMOTD.getInstance().getDataFolder(), "timer-motd.yml");
        }
        if(fileToGet.equals(Files.MODULES)) {
            return new File(bungeePixelMOTD.getInstance().getDataFolder(), "modules.yml");
        }
        return new File(bungeePixelMOTD.getInstance().getDataFolder(), "settings.yml");
    }
    public static boolean callMotds(MotdType motdType) {
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
    public static String getServers(String msg) throws ParseException {
        if(msg.contains("%online_")) {
            for (ServerInfo svs : bungeePixelMOTD.getInstance().getProxy().getServers().values()) {
                msg = msg.replace("%online_" + svs.getName() + "%", svs.getPlayers().size() + "");
            }
        }
        return replaceEventInfo(msg);
    }

    public static Date getEventDate(String eventName) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(getControl(Files.SETTINGS).getString("events." + eventName + ".TimeZone")));
        return format.parse(getControl(Files.SETTINGS).getString("events." + eventName + ".eventDate"));
    }

    public static String replaceEventInfo(String motdLineOrHoverLine) throws ParseException {
        if(motdLineOrHoverLine.contains("%event_")) {
            Date CurrentDate;
            CurrentDate = new Date();
            for(String event : getControl(Files.SETTINGS).getSection("events").getKeys()) {
                String TimeLeft = "<Invalid format-Type>";
                long difference = getEventDate(event).getTime() - CurrentDate.getTime();
                if(difference >= 0L) {
                    if(getControl(Files.SETTINGS).getString("events." + event + ".format-Type").equalsIgnoreCase("FIRST")) {
                        TimeLeft = convertToFinalResult(difference,"FIRST");
                    } else if(getControl(Files.SETTINGS).getString("events." + event + ".format-Type").equalsIgnoreCase("SECOND")) {
                        TimeLeft = convertToFinalResult(difference, "SECOND");
                    } else if(getControl(Files.SETTINGS).getString("events." + event + ".format-Type").equalsIgnoreCase("THIRD")) {
                        TimeLeft = convertToFinalResult(difference,"THIRD");
                    }
                } else {
                    TimeLeft = ChatColor.translateAlternateColorCodes('&',getControl(Files.SETTINGS).getString("events." + event + ".endMessage"));
                }
                motdLineOrHoverLine = motdLineOrHoverLine.replace("%event_" + event +  "_name%",getControl(Files.SETTINGS).getString("events." + event + ".eventName"))
                        .replace("%event_" + event + "_TimeZone%",getControl(Files.SETTINGS).getString("events." + event + ".TimeZone"))
                        .replace("%event_" + event + "_TimeLeft%",TimeLeft);
            }
        }
        return motdLineOrHoverLine;
    }
    public static String convertToFinalResult(long time,String formatType) {
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
    public static boolean getWhitelistStatus() {
        return getControl(Files.EDITABLE).getBoolean("whitelist.toggle");
    }
    public static String getMotd(boolean isWhitelistMotd) {
        List<String> motdToGet = new ArrayList<>();
        if(isWhitelistMotd) {
            motdToGet.addAll(getControl(Files.WHITELIST_MOTD).getSection("whitelist").getKeys());
            return motdToGet.get(new Random().nextInt(motdToGet.size()));
        }
        motdToGet.addAll(getControl(Files.NORMAL_MOTD).getSection("normal").getKeys());
        return motdToGet.get(new Random().nextInt(motdToGet.size()));

    }
    public static boolean isCommandEnabled() {
        return true;
    }
    public static void reloadFile(SaveMode saveMode) {
        try {
            if(saveMode.equals(SaveMode.COMMAND) || saveMode.equals(SaveMode.ALL)) {
                pCommand = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.COMMAND));
            }
            if(saveMode.equals(SaveMode.TIMER_MOTD) || saveMode.equals(SaveMode.ALL)) {
                pTimer = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.TIMER_MOTD));
            }
            if(saveMode.equals(SaveMode.EDITABLE) || saveMode.equals(SaveMode.ALL)) {
                pEditable = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.EDITABLE));
            }
            if(saveMode.equals(SaveMode.MODULES) || saveMode.equals(SaveMode.ALL)) {
                pModules = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.MODULES));
            }
            if(saveMode.equals(SaveMode.SETTINGS) || saveMode.equals(SaveMode.ALL)) {
                pSettings = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.SETTINGS));
            }
            if(saveMode.equals(SaveMode.WHITELIST_MOTD) || saveMode.equals(SaveMode.ALL)) {
                pWhitelist = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.WHITELIST_MOTD));
            }
            if(saveMode.equals(SaveMode.NORMAL_MOTD) || saveMode.equals(SaveMode.ALL)) {
                pNormal = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.NORMAL_MOTD));
            }
        } catch (IOException exp) {
            bungeePixelMOTD.getFiles().reportControlError();
            if(bungeeControl.isDetailed()) {
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] [Detailed Error] Information: ");
                //if(exp.getCause().toString() != null) {
                //    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Cause: " + exp.getCause().toString());
                //}
                if(exp.getMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Message: " + exp.getMessage());
                }
                if(exp.getLocalizedMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + exp.getLocalizedMessage());
                }
                if(exp.getStackTrace() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : exp.getStackTrace()) {
                        bungeePixelMOTD.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if(Arrays.toString(exp.getSuppressed()) != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(exp.getSuppressed()));
                }
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Class: " + exp.getClass().getName() +".class");
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Plugin version:" + bungeePixelMOTD.getInstance().getDescription().getVersion());
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
        }
    }
    public static void reloadFiles() {
        try {
            pCommand = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.COMMAND));
            pTimer = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.TIMER_MOTD));
            pEditable = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.EDITABLE));
            pModules = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.MODULES));
            pSettings = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.SETTINGS));
            pWhitelist = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.WHITELIST_MOTD));
            pNormal = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(Files.NORMAL_MOTD));
        } catch (IOException exp) {
            bungeePixelMOTD.getFiles().reportControlError();
            if(bungeeControl.isDetailed()) {
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] [Detailed Error] Information: ");
                //if(exp.getCause().toString() != null) {
                //    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Cause: " + exp.getCause().toString());
                //}
                if(exp.getMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Message: " + exp.getMessage());
                }
                if(exp.getLocalizedMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + exp.getLocalizedMessage());
                }
                if(exp.getStackTrace() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : exp.getStackTrace()) {
                        bungeePixelMOTD.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if(Arrays.toString(exp.getSuppressed()) != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(exp.getSuppressed()));
                }
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Class: " + exp.getClass().getName() +".class");
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Plugin version:" + bungeePixelMOTD.getInstance().getDescription().getVersion());
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
        }
    }
    public static boolean isDetailed() {
        return getControl(Files.SETTINGS).getBoolean("settings.show-detailed-errors");
    }
    public static Configuration getControl(Files fileToControl) {
        if(fileToControl.equals(Files.SETTINGS)) {
            if(pSettings == null) reloadFiles();
            return pSettings;
        }
        if(fileToControl.equals(Files.MODULES)) {
            if(pModules == null) reloadFiles();
            return pModules;
        }
        if(fileToControl.equals(Files.EDITABLE)) {
            if(pEditable == null) reloadFiles();
            return pEditable;
        }
        if(fileToControl.equals(Files.COMMAND)) {
            if(pCommand == null) reloadFiles();
            return pCommand;
        }
        if(fileToControl.equals(Files.NORMAL_MOTD)) {
            if(pNormal == null) reloadFiles();
            return pNormal;
        }
        if(fileToControl.equals(Files.TIMER_MOTD)) {
            if(pTimer == null) reloadFiles();
            return pTimer;
        }
        if(fileToControl.equals(Files.WHITELIST_MOTD)) {
            if(pWhitelist == null) reloadFiles();
            return pWhitelist;
        }
        bungeePixelMOTD.getFiles().reportBungeeGetControlError();
        return pSettings;
    }
    public static void save(SaveMode Mode) {
        try {
            if(Mode.equals(SaveMode.MODULES) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.MODULES), getFile(Files.MODULES));
            }
            if(Mode.equals(SaveMode.TIMER_MOTD) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.TIMER_MOTD), getFile(Files.TIMER_MOTD));
            }
            if(Mode.equals(SaveMode.EDITABLE) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.EDITABLE), getFile(Files.EDITABLE));
            }
            if(Mode.equals(SaveMode.NORMAL_MOTD) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.NORMAL_MOTD), getFile(Files.NORMAL_MOTD));
            }
            if(Mode.equals(SaveMode.COMMAND) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.COMMAND), getFile(Files.COMMAND));
            }
            if(Mode.equals(SaveMode.WHITELIST_MOTD) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.WHITELIST_MOTD), getFile(Files.WHITELIST_MOTD));
            }
            if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(getControl(Files.SETTINGS), getFile(Files.SETTINGS));
            }
        } catch(IOException exception) {
            bungeePixelMOTD.getFiles().reportControlError();
            if(isDetailed()) {
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] [Detailed Error] Information: ");
                if (exception.getCause().toString() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Cause: " + exception.getCause().toString());
                }
                if (exception.getMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Message: " + exception.getMessage());
                }
                if (exception.getLocalizedMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + exception.getLocalizedMessage());
                }
                if(exception.getStackTrace() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : exception.getStackTrace()) {
                        bungeePixelMOTD.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if (Arrays.toString(exception.getSuppressed()) != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(exception.getSuppressed()));
                }
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Class: " + exception.getClass().getName() + ".class");
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Plugin version:" + bungeePixelMOTD.getInstance().getDescription().getVersion());
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
        }
    }
    public static String getWhitelistAuthor() {
        if(!getControl(Files.EDITABLE).getString("whitelist.author").equalsIgnoreCase("CONSOLE")) {
            return getControl(Files.EDITABLE).getString("whitelist.author");
        } else {
            if(getControl(Files.EDITABLE).getBoolean("whitelist.customConsoleName.toggle")) {
                return getControl(Files.EDITABLE).getString("whitelist.customConsoleName.name");
            }
            return "Console";
        }
    }
}
