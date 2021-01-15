package dev.mruniverse.pixelmotd.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.init.SpigotPixel;
import dev.mruniverse.pixelmotd.manager.WrappedStatus;
import dev.mruniverse.pixelmotd.utils.SpigotUtils;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"UnstableApiUsage", "CatchMayIgnoreException"})
public class spigotMotd {
    private final PacketAdapter packetAdapter = new PacketAdapter(SpigotPixel.getInstance(), ListenerPriority.HIGH, PacketType.Status.Server.SERVER_INFO) {
        public void onPacketSending(PacketEvent e) {
            if (e.getPacketType() != PacketType.Status.Server.SERVER_INFO)
                return;
            if(e.isCancelled()) return;
            WrappedStatus packet = new WrappedStatus(e.getPacket());
            WrappedServerPing ping = packet.getJsonResponse();

            //** load MotdType
            MotdType motdType;
            ShowType showType;

            //* load strings & integers
            String line1,line2,motd,motdName;
            int max,online;
            max = ping.getPlayersMaximum();
            online = ping.getPlayersOnline();

                if (SpigotControl.getControl(Files.EDITABLE).getBoolean("whitelist.toggle")) {
                    motdName = SpigotControl.getMotd(true);
                    motdType = MotdType.WHITELIST_MOTD;
                } else {
                    motdName = SpigotControl.getMotd(false);
                    motdType = MotdType.NORMAL_MOTD;
                }
                showType = ShowType.FIRST;

                //* Motd Version Setup
                if (ProtocolLibrary.getProtocolManager().getProtocolVersion(e.getPlayer()) >= 721) {
                    if (SpigotUtils.getHexMotdStatus(motdType, motdName)) {
                        showType = ShowType.SECOND;
                    }
                }
            try {
                //* Favicon Setup
                //* Custom Server Icon Setup
                if(SpigotUtils.getIconStatus(motdType,motdName,false)) {
                    File[] icons;
                    if(SpigotUtils.getIconStatus(motdType,motdName,true)) {
                        icons = SpigotUtils.getIcons(motdType,motdName).listFiles();
                    } else {
                        if(motdType.equals(MotdType.NORMAL_MOTD)) {
                            icons = SpigotPixel.getFiles().getFile(Icons.NORMAL).listFiles();
                        } else {
                            icons = SpigotPixel.getFiles().getFile(Icons.WHITELIST).listFiles();
                        }
                    }
                    if (icons != null && icons.length != 0) {
                        List<File> validIcons = new ArrayList<>();
                        for (File image : icons) {
                            if (com.google.common.io.Files.getFileExtension(image.getPath()).equals("png")) {
                                validIcons.add(image);
                            }
                        }
                        if (validIcons.size() != 0) {
                            WrappedServerPing.CompressedImage image = getImage(validIcons.get(new Random().nextInt(validIcons.size())));
                            if (image != null) ping.setFavicon(image);
                        }
                    }
                }
                //* Players Setup
                if(SpigotUtils.getPlayersStatus(motdType,motdName)) {
                    if(SpigotUtils.getPlayersMode(motdType,motdName).equals(ValueMode.ADD)) {
                        max = online + 1;
                    }
                    if(SpigotUtils.getPlayersMode(motdType,motdName).equals(ValueMode.CUSTOM)) {
                        max = SpigotUtils.getPlayersValue(motdType,motdName);
                    }
                    if(SpigotUtils.getPlayersMode(motdType,motdName).equals(ValueMode.HALF)) {
                        if(online >= 2) {
                            max = online / 2;
                        } else {
                            max = 0;
                        }
                    }
                    if(SpigotUtils.getPlayersMode(motdType,motdName).equals(ValueMode.HALF_ADD)) {
                        int add;
                        if(online >= 2) {
                            add = online / 2;
                        } else {
                            add = 0;
                        }
                        max = online + add;
                    }
                    if(SpigotUtils.getPlayersMode(motdType,motdName).equals(ValueMode.EQUAL)) {
                        max = online;
                    }
                }
                if(SpigotUtils.getOnlineStatus(motdType,motdName)) {
                    if(SpigotUtils.getOnlineMode(motdType,motdName).equals(ValueMode.ADD)) {
                        online = online + 1;
                    }
                    if(SpigotUtils.getOnlineMode(motdType,motdName).equals(ValueMode.CUSTOM)) {
                        online = SpigotUtils.getOnlineValue(motdType,motdName);
                    }
                    if(SpigotUtils.getOnlineMode(motdType,motdName).equals(ValueMode.HALF)) {
                        if(online >= 2) {
                            online = online / 2;
                        } else {
                            online = 0;
                        }
                    }
                    if(SpigotUtils.getOnlineMode(motdType,motdName).equals(ValueMode.HALF_ADD)) {
                        int add;
                        if(online >= 2) {
                            add = online / 2;
                        } else {
                            add = 0;
                        }
                        online = online + add;
                    }
                    if(SpigotUtils.getOnlineMode(motdType,motdName).equals(ValueMode.EQUAL)) {
                        online = max;
                    }
                }
                ping.setPlayersOnline(online);
                ping.setPlayersMaximum(max);
                //* Motd hover Setup
                if (SpigotUtils.getHoverStatus(motdType, motdName)) {
                    ping.setPlayers(SpigotUtils.getHover(motdType, motdName, online, max));
                }
                if (SpigotUtils.getProtocolStatus(motdType, motdName)) {
                    ping.setVersionName(SpigotPixel.getHex().applyColor(SpigotUtils.replaceProtocolVariables(SpigotUtils.getProtocolMessage(motdType, motdName), online, max, getName(e.getPlayer().getName(), e.getPlayer()))));
                    if (SpigotUtils.getProtocolVersion(motdType, motdName)) { ping.setVersionProtocol(-1); } else { ping.setVersionProtocol(ProtocolLibrary.getProtocolManager().getProtocolVersion(e.getPlayer()));}
                }

                //* Motd Setup
                line1 = SpigotUtils.getLine1(motdType, motdName, showType);
                line2 = SpigotUtils.getLine2(motdType, motdName, showType);
                motd = SpigotUtils.replaceVariables(line1, online, max) + "\n" + SpigotUtils.replaceVariables(line2, online, max);
                ping.setMotD(motd);

            } catch(Throwable ignored) {
                SpigotPixel.motdIssue(motdType.name(), motdName);
                if(SpigotControl.isDetailed()) {
                    SpigotPixel.sendConsole("&a[Pixel MOTD] [Detailed Error] Information: ");
                    //if(ignored.getCause().toString() != null) {
                    //    spigotPixelMOTD.sendConsole("&a[Pixel MOTD] Cause: " + ignored.getCause().toString());
                    //}
                    if(ignored.getMessage() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] Message: " + ignored.getMessage());
                    }
                    if(ignored.getLocalizedMessage() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + ignored.getLocalizedMessage());
                    }
                    if(ignored.getStackTrace() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] StackTrace: ");
                        for(StackTraceElement line : ignored.getStackTrace()) {
                            SpigotPixel.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                        }
                    }
                    if(Arrays.toString(ignored.getSuppressed()) != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(ignored.getSuppressed()));
                    }
                    SpigotPixel.sendConsole("&a[Pixel MOTD] Class: " + ignored.getClass().getName() +".class");
                    SpigotPixel.sendConsole("&a[Pixel MOTD] Plugin version:" + SpigotPixel.getInstance().getDescription().getVersion());
                    SpigotPixel.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
                }
            }
        }
    };
    private WrappedServerPing.CompressedImage getImage(File file) {
        try {
            return WrappedServerPing.CompressedImage.fromPng(ImageIO.read(file));
        } catch(IOException exception) {
            reportBadImage(file.getPath());
            return null;
        }
    }
    private void reportBadImage(String filePath) {
        SpigotPixel.sendConsole("Can't read image: &b" + filePath + "&f. Please check image size: 64x64 or check if the image isn't corrupted.");
    }
    private String getName(String userName, Player player) {
        if (userName.contains("UNKNOWN") &&
                userName.contains(player.getAddress() + ""))
            userName = SpigotControl.getControl(Files.SETTINGS).getString("settings.defaultUnknownUserName");
        return userName;
    }
    @SuppressWarnings("unused")
    public PacketAdapter getPacketAdapter() {
        return this.packetAdapter;
    }
}