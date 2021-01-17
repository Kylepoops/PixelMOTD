package dev.mruniverse.pixelmotd.utils;

import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.PixelBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;

import java.io.File;
import java.text.ParseException;
import java.util.*;

import static dev.mruniverse.pixelmotd.utils.bungeeLogger.error;

public class BungeeUtils {
    private final PixelBungee plugin;

    public BungeeUtils(PixelBungee plugin) {
        this.plugin = plugin;

    }

    public List<String> getPlayers(WhitelistMembers mode, String serverName) {
        if(mode.equals(WhitelistMembers.NAMEs)) {
            if(plugin.getBungeeControl().getControl(Files.MODULES).get(Extras.getServerPath(Whitelist.PLAYERS_NAME,serverName)) != null)
                if(plugin.getBungeeControl().getControl(Files.MODULES).get("modules.server-whitelist.servers." + serverName + " .players-name") != null) {
                    return plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.server-whitelist.servers." + serverName + " .players-name");
                }
            return new ArrayList<>();
        }
        if(plugin.getBungeeControl().getControl(Files.MODULES).get("modules.server-whitelist.servers." + serverName + " .players-uuid") != null) {
            return plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.server-whitelist.servers." + serverName + " .players-uuid");
        }
        return new ArrayList<>();
    }
    public List<String> getPlayers(BlacklistMembers mode, String serverName) {
        if(mode.equals(BlacklistMembers.NAMEs)) {
            if(plugin.getBungeeControl().getControl(Files.MODULES).get(Extras.getServerPath(Blacklist.PLAYERS_NAME,serverName)) != null)
                if(plugin.getBungeeControl().getControl(Files.MODULES).get("modules.server-blacklist.servers." + serverName + " .players-name") != null) {
                    return plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.server-blacklist.servers." + serverName + " .players-name");
                }
            return new ArrayList<>();
        }
        if(plugin.getBungeeControl().getControl(Files.MODULES).get("modules.server-blacklist.servers." + serverName + " .players-uuid") != null) {
            return plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.server-blacklist.servers." + serverName + " .players-uuid");
        }
        return new ArrayList<>();
    }
    public ServerPing.PlayerInfo[] getHover(MotdType motdType, String motdName,int online,int max) {
        int ids = 0;
        ServerPing.PlayerInfo[] hoverToShow = new ServerPing.PlayerInfo[0];
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            if(plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customHover.toggle")) {
                for(String line : plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getStringList("normal." + motdName + ".otherSettings.customHover.hover")) {
                    try {
                        hoverToShow = addHoverLine(hoverToShow, new ServerPing.PlayerInfo(applyColor(plugin.getBungeeControl().getServers(line.replace("&","§").replace("%plugin_version%", PixelBungee.getInstance().getDescription().getVersion()).replace("%online%", online + "").replace("%max%", max + "").replace("%whitelist_author%", plugin.getBungeeControl().getWhitelistAuthor()))), String.valueOf(ids)));
                    } catch (ParseException e) {
                        reportHoverError();
                        if(plugin.getBungeeControl().isDetailed()) {
                            error("Information: ");
                            if(e.getMessage() != null) {
                                error("Message: " + e.getMessage());
                            }
                            if(e.getLocalizedMessage() != null) {
                                error("LocalizedMessage: " + e.getLocalizedMessage());
                            }
                            if(e.getStackTrace() != null) {
                                error("StackTrace: ");
                                for(StackTraceElement str : e.getStackTrace()) {
                                    error("(" + str.getLineNumber() + ") " + str.toString());
                                }
                            }
                            error("ErrorOffset: " + e.getErrorOffset());
                            error("Class: " + e.getClass().getName() +".class");
                            error("Plugin version:" + PixelBungee.getInstance().getDescription().getVersion());
                            error("---------------");
                        }
                    }
                    ids++;
                }
                return hoverToShow;
            }
            hoverToShow = addHoverLine(hoverToShow, new ServerPing.PlayerInfo("", ""));
            return hoverToShow;
        }
        if(plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customHover.toggle")) {
            for(String line : plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getStringList("whitelist." + motdName + ".otherSettings.customHover.hover")) {
                try {
                    hoverToShow = addHoverLine(hoverToShow, new ServerPing.PlayerInfo(applyColor(plugin.getBungeeControl().getServers(line.replace("&","§").replace("%plugin_version%", PixelBungee.getInstance().getDescription().getVersion()).replace("%online%", online + "").replace("%max%", max + "").replace("%whitelist_author%", plugin.getBungeeControl().getWhitelistAuthor()))), String.valueOf(ids)));
                } catch (ParseException e) {
                    reportHoverError();
                    if(plugin.getBungeeControl().isDetailed()) {
                        error("Information: ");
                        if(e.getMessage() != null) {
                            error("Message: " + e.getMessage());
                        }
                        if(e.getLocalizedMessage() != null) {
                            error("LocalizedMessage: " + e.getLocalizedMessage());
                        }
                        if(e.getStackTrace() != null) {
                            error("StackTrace: ");
                            for(StackTraceElement str : e.getStackTrace()) {
                                error("(" + str.getLineNumber() + ") " + str.toString());
                            }
                        }
                        if(Arrays.toString(e.getSuppressed()) != null) {
                            error("Suppressed: " + Arrays.toString(e.getSuppressed()));
                        }
                        error("ErrorOffset: " + e.getErrorOffset());
                        error("Class: " + e.getClass().getName() +".class");
                        error("Plugin version:" + PixelBungee.getInstance().getDescription().getVersion());
                        error("---------------");
                    }
                }
                ids++;
            }
            return hoverToShow;
        }
        hoverToShow = addHoverLine(hoverToShow, new ServerPing.PlayerInfo("", ""));
        return hoverToShow;
    }
    private void reportHoverError() {
        error("Can't generate motd Hover, please verify if your hover is correctly created!");
    }
    private void reportProtocolError() {
        error("Can't generate motd Protocol, please verify if your protocol is correctly created!");
    }
    public File getIcons(MotdType motdType,String motdName) {
        File iconFolder = plugin.getFiles().getFile(Icons.FOLDER);
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            iconFolder = new File(plugin.getFiles().getFile(Icons.FOLDER), "Normal-" + motdName);
        }
        if(motdType.equals(MotdType.WHITELIST_MOTD)) {
            iconFolder = new File(plugin.getFiles().getFile(Icons.FOLDER), "Whitelist-" + motdName);
        }
        if(!iconFolder.exists()) plugin.getFiles().loadFolder(iconFolder,"&fIcon Folder: &b" + motdName);
        return iconFolder;
    }
    public boolean getPlayersStatus(MotdType motdType,String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customMaxPlayers.toggle");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customMaxPlayers.toggle");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customMaxPlayers.toggle");
    }
    public boolean getProtocolStatus(MotdType motdType,String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customProtocol.toggle");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customProtocol.toggle");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customProtocol.toggle");
    }
    public ValueMode getPlayersMode(MotdType motdType, String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            if(plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("CUSTOM-VALUES")) {
                return ValueMode.CUSTOM;
            }
            if(plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("ADD")) {
                return ValueMode.ADD;
            }
            if(plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("HALF-ADD")) {
                return ValueMode.HALF_ADD;
            }
            if(plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("HALF")) {
                return ValueMode.HALF;
            }
            return ValueMode.EQUAL;
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            if(plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("CUSTOM-VALUES")) {
                return ValueMode.CUSTOM;
            }
            if(plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("ADD")) {
                return ValueMode.ADD;
            }
            if(plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("HALF-ADD")) {
                return ValueMode.HALF_ADD;
            }
            if(plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("HALF")) {
                return ValueMode.HALF;
            }
            return ValueMode.EQUAL;
        }
        if(plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("CUSTOM-VALUES")) {
            return ValueMode.CUSTOM;
        }
        if(plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("ADD")) {
            return ValueMode.ADD;
        }
        if(plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("HALF-ADD")) {
            return ValueMode.HALF_ADD;
        }
        if(plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customMaxPlayers.mode").equalsIgnoreCase("HALF")) {
            return ValueMode.HALF;
        }
        return ValueMode.EQUAL;
    }
    public String getServerIcon() { return "                                                                   "; }
    public String getLine1(MotdType motdType,String motdName, ShowType showType) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            if(showType.equals(ShowType.FIRST)) {
                return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".line1");
            }
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customHexMotd.line1");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            if(showType.equals(ShowType.FIRST)) {
                return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".line1");
            }
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customHexMotd.line1");
        }
        if(showType.equals(ShowType.FIRST)) {
            return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".line1");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customHexMotd.line1");
    }
    public String getLine2(MotdType motdType, String motdName, ShowType showType) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            if(showType.equals(ShowType.FIRST)) {
                return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".line2");
            }
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customHexMotd.line2");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            if(showType.equals(ShowType.FIRST)) {
                return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".line2");
            }
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customHexMotd.line2");
        }
        if(showType.equals(ShowType.FIRST)) {
            return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".line2");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customHexMotd.line2");
    }
    //getHoverStatus
    public boolean getHoverStatus(MotdType motdType,String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customHover.toggle");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customHover.toggle");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customHover.toggle");
    }
    public boolean getProtocolVersion(MotdType motdType,String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customProtocol.changeProtocolVersion");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customProtocol.changeProtocolVersion");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customProtocol.changeProtocolVersion");
    }
    public String getProtocolMessage(MotdType motdType,String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getString("normal." + motdName + ".otherSettings.customProtocol.protocol");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getString("whitelist." + motdName + ".otherSettings.customProtocol.protocol");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getString("timers." + motdName + ".otherSettings.customProtocol.protocol");
    }
    public String replaceVariables(String msg,int online,int max) {
        try {
            msg = plugin.getBungeeControl().getServers(msg).replace("%online%",online + "")
                    .replace("%max%",max + "")
                    .replace("%plugin_author%","MrUniverse44")
                    .replace("%whitelist_author%", plugin.getBungeeControl().getWhitelistAuthor())
                    .replace("%plugin_version%", PixelBungee.getInstance().getDescription().getVersion());
        } catch (ParseException e) {
            reportProtocolError();
            if(plugin.getBungeeControl().isDetailed()) {
                error("Information: ");
                if(e.getMessage() != null) {
                    error("Message: " + e.getMessage());
                }
                if(e.getLocalizedMessage() != null) {
                    error("LocalizedMessage: " + e.getLocalizedMessage());
                }
                if(e.getStackTrace() != null) {
                    error("StackTrace: ");
                    for(StackTraceElement line : e.getStackTrace()) {
                        error("(" + line.getLineNumber() + ") " + line.toString());
                    }
                }

                error("ErrorOffset: " + e.getErrorOffset());
                error("Class: " + e.getClass().getName() +".class");
                error("Plugin version:" + PixelBungee.getInstance().getDescription().getVersion());
                error("---------------");
            }
        }
        return msg;
    }
    public List<Integer> getPlayersList(MotdType motdType,String motdName) {
        List<Integer> values = new ArrayList<>();
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            values = plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getIntList("normal." + motdName + ".otherSettings.customMaxPlayers.values");
        }
        if(motdType.equals(MotdType.WHITELIST_MOTD)) {
            values = plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getIntList("whitelist." + motdName + ".otherSettings.customMaxPlayers.values");
        }
        if(motdType.equals(MotdType.TIMER_MOTD)) {
            values = plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getIntList("timers." + motdName + ".otherSettings.customMaxPlayers.values");
        }
        return values;
    }
    public int getPlayersValue(MotdType motdType,String motdName) {
        List<Integer> values = new ArrayList<>();
        if(motdType.equals(MotdType.NORMAL_MOTD)) {
            values = plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getIntList("normal." + motdName + ".otherSettings.customMaxPlayers.values");
        }
        if(motdType.equals(MotdType.WHITELIST_MOTD)) {
            values = plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getIntList("whitelist." + motdName + ".otherSettings.customMaxPlayers.values");
        }
        if(motdType.equals(MotdType.TIMER_MOTD)) {
            values = plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getIntList("timers." + motdName + ".otherSettings.customMaxPlayers.values");
        }
        return values.get(new Random().nextInt(values.size()));
    }
    private ServerPing.PlayerInfo[] addHoverLine(ServerPing.PlayerInfo[] player, ServerPing.PlayerInfo info) {
        ServerPing.PlayerInfo[] hoverText = new ServerPing.PlayerInfo[player.length + 1];
        for(int id = 0; id < player.length; id++) {
            hoverText[id] = player[id];
        }
        hoverText[player.length] = info;
        return hoverText;
    }
    public boolean getHexMotdStatus(MotdType motdType,String motdName) {
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customHexMotd.toggle");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customHexMotd.toggle");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customHexMotd.toggle");
    }
    public String getPlayer() {
        return plugin.getBungeeControl().getControl(Files.SETTINGS).getString("settings.defaultUnknownUserName");
    }
    public String getPermissionMessage(String permission) {
        try {
            if (plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.no-perms").contains("<permission>")) {
                return Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.no-perms")).replace("<permission>", permission);
            }
        } catch (Throwable throwable) {
            reportMistake();
        }
        return plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.no-perms");
    }

    private void reportMistake() {
        error("The plugin found an issue, fixing internal issue.");
    }
    public boolean getIconStatus(MotdType motdType,String motdName,boolean customFile) {
        if(!customFile) {
            if (motdType.equals(MotdType.NORMAL_MOTD)) {
                return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customIcon.toggle");
            }
            if (motdType.equals(MotdType.WHITELIST_MOTD)) {
                return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customIcon.toggle");
            }
            return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customIcon.toggle");
        }
        if (motdType.equals(MotdType.NORMAL_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.NORMAL_MOTD).getBoolean("normal." + motdName + ".otherSettings.customIcon.customFile");
        }
        if (motdType.equals(MotdType.WHITELIST_MOTD)) {
            return plugin.getBungeeControl().getControl(Files.WHITELIST_MOTD).getBoolean("whitelist." + motdName + ".otherSettings.customIcon.customFile");
        }
        return plugin.getBungeeControl().getControl(Files.TIMER_MOTD).getBoolean("timers." + motdName + ".otherSettings.customIcon.customFile");
    }
    public String applyColor(String message) {
        if(plugin.getHex().getStatus()) {
            return nowCentered(plugin.getHex().applyColor(message));
        }
        return nowCentered(ChatColor.translateAlternateColorCodes('&',message));
    }
    public String applyColor(String message,ShowType showType) {
        if(showType.equals(ShowType.SECOND)) {
            return nowCentered(plugin.getHex().applyColor(ChatColor.translateAlternateColorCodes('&',message)));
        }

        return nowCentered(ChatColor.translateAlternateColorCodes('&',message));
    }
    private String nowCentered(String msg) {
        if(msg.contains("<centerText>")) {
            msg = msg.replace("<centerText>","");
            msg = CenterMotd.centerMotd(msg);
        }
        return msg;
    }
}