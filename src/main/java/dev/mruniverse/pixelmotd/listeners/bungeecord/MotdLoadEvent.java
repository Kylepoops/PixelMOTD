package dev.mruniverse.pixelmotd.listeners.bungeecord;

import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.MotdType;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import net.md_5.bungee.api.plugin.Event;

import java.io.File;
import java.util.List;
@SuppressWarnings("unused")
public class MotdLoadEvent extends Event {
    private final String motdLine1;
    private final String motdLine2;
    private final String fullMotd;
    private final String customProtocol;
    private String path;
    private final String motdID;
    private final MotdType motdType;
    private final List<String> motdHover;
    private final boolean isHoverEnabled;
    private final boolean CustomHexMotdStatus;
    private final boolean customProtocolStatus;
    private final boolean protocolVersion;
    private Files motdFile;
    private final IconManager iconManager;
    private PlayerManager maxManager,onlineManager;
    public MotdLoadEvent(boolean customHex,MotdType type, String id, String line1, String line2, String motd, String protocolMessage, File icon,int online,int max) {
        motdFile = Files.NORMAL_MOTD;
        motdType = type;
        fullMotd = motd;
        motdID = id;
        motdLine1 = line1;
        motdLine2 = line2;
        customProtocol = protocolMessage;
        if(type.equals(MotdType.NORMAL_MOTD)) {
            path = "normal." + id + ".";
        }
        if(type.equals(MotdType.WHITELIST_MOTD)) {
            motdFile = Files.WHITELIST_MOTD;
            path = "whitelist." + id + ".";
        }
        if(type.equals(MotdType.TIMER_MOTD)) {
            motdFile = Files.TIMER_MOTD;
            path = "timers." + id + ".";
        }
        maxManager = new PlayerManager(motdType,id,max);
        onlineManager = new PlayerManager(motdType,id,online);
        protocolVersion = BungeeControl.getControl(motdFile).getBoolean(path + "otherSettings.customProtocol.changeProtocolVersion");
        motdHover = BungeeControl.getControl(motdFile).getStringList(path + "otherSettings.customHover.hover");
        isHoverEnabled = BungeeControl.getControl(motdFile).getBoolean(path + "otherSettings.customHover.toggle");
        CustomHexMotdStatus = customHex;
        customProtocolStatus = BungeeControl.getControl(motdFile).getBoolean(path + "otherSettings.customProtocol.toggle");
        iconManager = new IconManager(BungeeControl.getControl(motdFile).getBoolean(path + "otherSettings.customIcon.toggle"),
                BungeeControl.getControl(motdFile).getBoolean(path + "otherSettings.customIcon.customFile"),icon);
    }
    public PlayerManager getMax() {
        return maxManager;
    }
    public PlayerManager getOnline() {
        return onlineManager;
    }
    public IconManager getIcon() {
        return iconManager;
    }
    public String getFullMotd() {
        return fullMotd;
    }
    public Files getMotdFile() {
        return motdFile;
    }
    public String getLine1() {
        return motdLine1;
    }
    public String getLine2() {
        return motdLine2;
    }
    public String getProtocolMessage() {
        return customProtocol;
    }
    public String getMotdName() {
        return motdID;
    }
    public String getMotdID() {
        return motdID;
    }
    public MotdType getMotdType() {
        return motdType;
    }
    public List<String> getHover() {
        return motdHover;
    }
    public boolean getHoverStatus() {
        return isHoverEnabled;
    }
    public boolean isCustomHexMotd() {
        return CustomHexMotdStatus;
    }
    public boolean getCustomProtocolStatus() {
        return customProtocolStatus;
    }
    public boolean getProtocolVersionStatus() {
        return protocolVersion;
    }

}