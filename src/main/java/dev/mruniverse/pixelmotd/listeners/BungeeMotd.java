package dev.mruniverse.pixelmotd.listeners;

import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.init.BungeePixel;
import dev.mruniverse.pixelmotd.listeners.bungeecord.MotdLoadEvent;
import dev.mruniverse.pixelmotd.utils.BungeeUtils;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static dev.mruniverse.pixelmotd.enums.ShowType.FIRST;

@SuppressWarnings("UnstableApiUsage")
public class BungeeMotd implements Listener {
    private final BungeePixel plugin;

    public BungeeMotd(BungeePixel plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPing(ProxyPingEvent e) {

        //*verify for cancelled motd to prevent errors
        if(e instanceof Cancellable && ((Cancellable) e).isCancelled()) return;
        if(e.getResponse() == null) return;
        //** load server ping.
        ServerPing.Protocol protocol;
        ServerPing.Players MotdPlayers;
        ServerPing.PlayerInfo[] motdHover;
        Favicon icon = null;

        //** load MotdType
        MotdType ShowMode;
        ShowType showType;
        boolean mHover;
        File iconFile;
        //* load strings & integers
        String line1,line2,motd,ShowMotd;
        int max,online;
        ServerPing response = e.getResponse();
        max = response.getPlayers().getMax();
        online = response.getPlayers().getOnline();
        PendingConnection connection = e.getConnection();


        //* generate the motd name & get the server whitelist status
        if(BungeeControl.getControl(Files.EDITABLE).getBoolean("whitelist.toggle")) {
            ShowMotd = BungeeControl.getMotd(true);
            ShowMode = MotdType.WHITELIST_MOTD;
        } else {
            ShowMotd = BungeeControl.getMotd(false);
            ShowMode = MotdType.NORMAL_MOTD;
        }
        showType = ShowType.FIRST;
        //* Motd Version Setup
        if(connection != null) {
            if (e.getConnection().getVersion() >= 735) {
                if (BungeeUtils.getHexMotdStatus(ShowMode, ShowMotd)) {
                    showType = ShowType.SECOND;
                }
            }
        }

        //* Database information recollect
        //e.getConnection().getVirtualHost().getAddress();

        //* Motd Hover Setup
        motdHover = BungeeUtils.getHover(ShowMode,ShowMotd,online,max);
        mHover = BungeeUtils.getHoverStatus(ShowMode,ShowMotd);
        iconFile = null;
        //* Custom Server Icon Setup
        if(BungeeUtils.getIconStatus(ShowMode,ShowMotd,false)) {
            File[] icons;
            if(BungeeUtils.getIconStatus(ShowMode,ShowMotd,true)) {
                icons = BungeeUtils.getIcons(ShowMode,ShowMotd).listFiles();
            } else {
                if(ShowMode.equals(MotdType.NORMAL_MOTD)) {
                    icons = BungeePixel.getFiles().getFile(Icons.NORMAL).listFiles();
                } else {
                    icons = BungeePixel.getFiles().getFile(Icons.WHITELIST).listFiles();
                }
            }
            List<File> validIcons = new ArrayList<>();
            if (icons != null && icons.length != 0) {
                for (File image : icons) {
                    if (com.google.common.io.Files.getFileExtension(image.getPath()).equals("png")) {
                        validIcons.add(image);
                    }
                }
                if(validIcons.size() != 0) {
                    iconFile = validIcons.get(new Random().nextInt(validIcons.size()));
                    BufferedImage image = getImage(iconFile);
                    if(image != null) {
                        icon = Favicon.create(image);
                    } else {
                        icon = response.getFaviconObject();
                    }
                } else {
                    icon = response.getFaviconObject();
                }
            } else {
                icon = response.getFaviconObject();
            }
        }

        //* player setup
        if(BungeeUtils.getPlayersStatus(ShowMode,ShowMotd)) {
            if(BungeeUtils.getPlayersMode(ShowMode,ShowMotd).equals(ValueMode.ADD)) {
                max = online + 1;
            }
            if(BungeeUtils.getPlayersMode(ShowMode,ShowMotd).equals(ValueMode.CUSTOM)) {
                max = BungeeUtils.getPlayersValue(ShowMode,ShowMotd);
            }
            if(BungeeUtils.getPlayersMode(ShowMode,ShowMotd).equals(ValueMode.HALF)) {
                if(online >= 2) {
                    max = online / 2;
                } else {
                    max = 0;
                }
            }
            if(BungeeUtils.getPlayersMode(ShowMode,ShowMotd).equals(ValueMode.HALF_ADD)) {
                int add;
                if(online >= 2) {
                    add = online / 2;
                } else {
                    add = 0;
                }
                max = online + add;
            }
            if(BungeeUtils.getPlayersMode(ShowMode,ShowMotd).equals(ValueMode.EQUAL)) {
                max = online;
            }
        }

        //*custom Protocol Setup
        if(BungeeUtils.getProtocolStatus(ShowMode,ShowMotd)) {
            ServerPing.Protocol Received = response.getVersion();
            Received.setName(BungeeUtils.applyColor(BungeeUtils.replaceVariables(BungeeUtils.getProtocolMessage(ShowMode,ShowMotd),online,max).replace("%server_icon%", BungeeUtils.getServerIcon())));
            if(BungeeUtils.getProtocolVersion(ShowMode,ShowMotd)) {
                Received.setProtocol(-1);
            }
            protocol = Received;
        } else {
            protocol = response.getVersion();
        }

        //* motd Lines Setup

        line1 = BungeeUtils.getLine1(ShowMode,ShowMotd,showType);
        line2 = BungeeUtils.getLine2(ShowMode,ShowMotd,showType);
        if(mHover) {
            MotdPlayers = new ServerPing.Players(max, online, motdHover);
        } else {
            MotdPlayers = new ServerPing.Players(max, online, response.getPlayers().getSample());
        }

        //* motd Lines to show - Setup
        String motdL;
        motdL = BungeeUtils.applyColor(BungeeUtils.replaceVariables(line1,online,max),showType) + "\n" + BungeeUtils.applyColor(BungeeUtils.replaceVariables(line2,online,max),showType);
        if(connection != null) {
            InetSocketAddress virtualHost = connection.getVirtualHost();
            if (virtualHost != null) {
                motd = replacePlayer(virtualHost.getAddress(), motdL);
            } else {
                motd = replacePlayer(null, motdL);
            }
        } else {
            motd = replacePlayer(null, motdL);
        }
        ServerPing result;
        if(showType.equals(FIRST)) {
            MotdLoadEvent event = new MotdLoadEvent(false,ShowMode,ShowMotd,line1,line2,motdL,protocol.getName(),iconFile,online,max);
            plugin.getProxy().getPluginManager().callEvent(event);
            result = new ServerPing(protocol, MotdPlayers, new TextComponent(motd), icon);
        } else {
            MotdLoadEvent event = new MotdLoadEvent(true,ShowMode,ShowMotd,line1,line2,motdL,protocol.getName(),iconFile,online,max);
            plugin.getProxy().getPluginManager().callEvent(event);
            result = new ServerPing(protocol, MotdPlayers, new TextComponent(TextComponent.fromLegacyText(motd)), icon);
        }
        e.setResponse(result);

    }
    private String replacePlayer(InetAddress address,String message) {
        if(message.contains("%player%")) {
            message = message.replace("%player%","Unknown");
        }
        if(message.contains("%address%")) {
            message = message.replace("%address%",address.getCanonicalHostName());
        }
        return message;
    }
    @SuppressWarnings("ConstantConditions")
    private BufferedImage getImage(File file) {
        try {
            return ImageIO.read(file);
        } catch(IOException exception) {
            reportBadImage(file.getPath());
            if(BungeeControl.isDetailed()) {
                BungeePixel.sendConsole("&a[Pixel MOTD] [Detailed Error] Information: ");
                if(exception.getCause().toString() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] Cause: " + exception.getCause().toString());
                }
                if(exception.getMessage() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] Message: " + exception.getMessage());
                }
                if(exception.getLocalizedMessage() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + exception.getLocalizedMessage());
                }
                if(exception.getStackTrace() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : exception.getStackTrace()) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if(Arrays.toString(exception.getSuppressed()) != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(exception.getSuppressed()));
                }
                BungeePixel.sendConsole("&a[Pixel MOTD] Class: " + exception.getClass().getName() +".class");
                BungeePixel.sendConsole("&a[Pixel MOTD] Plugin version:" + BungeePixel.getInstance().getDescription().getVersion());
                BungeePixel.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
            return null;
        }
    }
    private void reportBadImage(String filePath) {
        BungeePixel.sendConsole("Can't read image: &b" + filePath + "&f. Please check image size: 64x64 or check if the image isn't corrupted.");
    }
}
