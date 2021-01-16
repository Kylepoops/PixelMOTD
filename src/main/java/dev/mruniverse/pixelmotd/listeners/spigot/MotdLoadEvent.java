package dev.mruniverse.pixelmotd.listeners.spigot;


import dev.mruniverse.pixelmotd.enums.MotdType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.List;
@SuppressWarnings("unused")
public class MotdLoadEvent extends Event {
    private final String motdLine1;
    private final String motdLine2;
    private final String fullMotd;
    private final String customProtocol;
    private final String motdID;
    private final MotdType motdType;
    private final List<String> motdHover;
    private final boolean isHoverEnabled;
    private final boolean CustomHexMotdStatus;
    private final boolean customProtocolStatus;
    private final boolean protocolVersion;
    private final IconManager iconManager;
    private PlayerManager maxManager,onlineManager;
    private static final HandlerList handlerList = new HandlerList();
    public MotdLoadEvent(MotdType type,
                         String id,
                         String line1,
                         String line2,
                         String motd,
                         String protocolMessage,
                         List<String> hover,
                         boolean hoverStatus,
                         boolean hexStatus,
                         boolean protocolStatus,
                         boolean protocolV,
                         boolean iconStatus,
                         boolean iconCustomFile,
                         File icon) {
        motdType = type;
        fullMotd = motd;
        motdID = id;
        motdLine1 = line1;
        motdLine2 = line2;
        customProtocol = protocolMessage;
        protocolVersion = protocolV;
        motdHover = hover;
        isHoverEnabled = hoverStatus;
        CustomHexMotdStatus = hexStatus;
        customProtocolStatus = protocolStatus;
        iconManager = new IconManager(iconStatus,iconCustomFile,icon);
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public IconManager getIcon() {
        return iconManager;
    }
    public String getFullMotd() {
        return fullMotd;
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