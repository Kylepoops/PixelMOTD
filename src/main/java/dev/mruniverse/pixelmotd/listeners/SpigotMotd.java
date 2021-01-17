package dev.mruniverse.pixelmotd.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.PixelSpigot;
import dev.mruniverse.pixelmotd.listeners.spigot.MotdLoadEvent;
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

import static dev.mruniverse.pixelmotd.utils.spigotLogger.error;
import static dev.mruniverse.pixelmotd.utils.spigotLogger.warn;

@SuppressWarnings({"UnstableApiUsage", "CatchMayIgnoreException"})
public class SpigotMotd {
    private final PixelSpigot main;
    public SpigotMotd(PixelSpigot plugin) {
        main = plugin;
    }
    private final PacketAdapter packetAdapter = new PacketAdapter(PixelSpigot.getInstance(), ListenerPriority.HIGH, PacketType.Status.Server.SERVER_INFO) {
        public void onPacketSending(PacketEvent e) {
            if (e.getPacketType() != PacketType.Status.Server.SERVER_INFO)
                return;
            if(e.isCancelled()) return;
            WrappedStatus packet = new WrappedStatus(e.getPacket());
            WrappedServerPing ping = packet.getJsonResponse();

            //** load MotdType
            MotdType motdType;
            ShowType showType;
            File iconFile = null;
            //* load strings & integers
            String line1,line2,motd,motdName;
            int max,online;
            max = ping.getPlayersMaximum();
            online = ping.getPlayersOnline();

<<<<<<< HEAD
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
=======
                if (main.getSpigotControl().getControl(Files.EDITABLE).getBoolean("whitelist.toggle")) {
                    motdName = main.getSpigotControl().getMotd(true);
                    motdType = MotdType.WHITELIST_MOTD;
                } else {
                    motdName = main.getSpigotControl().getMotd(false);
                    motdType = MotdType.NORMAL_MOTD;
                }
                showType = ShowType.FIRST;

                //* Motd Version Setup
                if (ProtocolLibrary.getProtocolManager().getProtocolVersion(e.getPlayer()) >= 721) {
                    if (main.getSpigotUtils().getHexMotdStatus(motdType, motdName)) {
                        showType = ShowType.SECOND;
                    }
>>>>>>> 8c769325e60fc856c61791189dce1d62afd1eaa2
                }
            }
            try {
                //* Favicon Setup
                //* Custom Server Icon Setup
                if(main.getSpigotUtils().getIconStatus(motdType,motdName,false)) {
                    File[] icons;
                    if(main.getSpigotUtils().getIconStatus(motdType,motdName,true)) {
                        icons = main.getSpigotUtils().getIcons(motdType,motdName).listFiles();
                    } else {
                        if(motdType.equals(MotdType.NORMAL_MOTD)) {
                            icons = main.getFiles().getFile(Icons.NORMAL).listFiles();
                        } else {
                            icons = main.getFiles().getFile(Icons.WHITELIST).listFiles();
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
                            iconFile = validIcons.get(new Random().nextInt(validIcons.size()));
                            WrappedServerPing.CompressedImage image = getImage(iconFile);
                            if (image != null) ping.setFavicon(image);
                        }
                    }
                }
                //* Players Setup
                if(main.getSpigotUtils().getPlayersStatus(motdType,motdName)) {
                    if(main.getSpigotUtils().getPlayersMode(motdType,motdName).equals(ValueMode.ADD)) {
                        max = online + 1;
                    }
                    if(main.getSpigotUtils().getPlayersMode(motdType,motdName).equals(ValueMode.CUSTOM)) {
                        max = main.getSpigotUtils().getPlayersValue(motdType,motdName);
                    }
                    if(main.getSpigotUtils().getPlayersMode(motdType,motdName).equals(ValueMode.HALF)) {
                        if(online >= 2) {
                            max = online / 2;
                        } else {
                            max = 0;
                        }
                    }
                    if(main.getSpigotUtils().getPlayersMode(motdType,motdName).equals(ValueMode.HALF_ADD)) {
                        int add;
                        if(online >= 2) {
                            add = online / 2;
                        } else {
                            add = 0;
                        }
                        max = online + add;
                    }
                    if(main.getSpigotUtils().getPlayersMode(motdType,motdName).equals(ValueMode.EQUAL)) {
                        max = online;
                    }
                }
                if(main.getSpigotUtils().getOnlineStatus(motdType,motdName)) {
                    if(main.getSpigotUtils().getOnlineMode(motdType,motdName).equals(ValueMode.ADD)) {
                        online = online + 1;
                    }
                    if(main.getSpigotUtils().getOnlineMode(motdType,motdName).equals(ValueMode.CUSTOM)) {
                        online = main.getSpigotUtils().getOnlineValue(motdType,motdName);
                    }
                    if(main.getSpigotUtils().getOnlineMode(motdType,motdName).equals(ValueMode.HALF)) {
                        if(online >= 2) {
                            online = online / 2;
                        } else {
                            online = 0;
                        }
                    }
                    if(main.getSpigotUtils().getOnlineMode(motdType,motdName).equals(ValueMode.HALF_ADD)) {
                        int add;
                        if(online >= 2) {
                            add = online / 2;
                        } else {
                            add = 0;
                        }
                        online = online + add;
                    }
                    if(main.getSpigotUtils().getOnlineMode(motdType,motdName).equals(ValueMode.EQUAL)) {
                        online = max;
                    }
                }
                ping.setPlayersOnline(online);
                ping.setPlayersMaximum(max);
                //* Motd hover Setup
                if (main.getSpigotUtils().getHoverStatus(motdType, motdName)) {
                    ping.setPlayers(main.getSpigotUtils().getHover(motdType, motdName, online, max));
                }
                if (main.getSpigotUtils().getProtocolStatus(motdType, motdName)) {
                    ping.setVersionName(PixelSpigot.getHex().applyColor(main.getSpigotUtils().replaceProtocolVariables(main.getSpigotUtils().getProtocolMessage(motdType, motdName), online, max, getName(e.getPlayer().getName(), e.getPlayer()))));
                    if (main.getSpigotUtils().getProtocolVersion(motdType, motdName)) { ping.setVersionProtocol(-1); } else { ping.setVersionProtocol(ProtocolLibrary.getProtocolManager().getProtocolVersion(e.getPlayer()));}
                }

                //* Motd Setup
                line1 = main.getSpigotUtils().getLine1(motdType, motdName, showType);
                line2 = main.getSpigotUtils().getLine2(motdType, motdName, showType);
                motd = main.getSpigotUtils().replaceVariables(line1, online, max) + "\n" + main.getSpigotUtils().replaceVariables(line2, online, max);
                MotdLoadEvent event;
                if(showType.equals(ShowType.FIRST)) {
                    event = new MotdLoadEvent(false,motdType,motdName,line1,line2,motd,ping.getVersionName(),iconFile);
                } else {
                    event = new MotdLoadEvent(true,motdType,motdName,line1,line2,motd,ping.getVersionName(),iconFile);
                }
                plugin.getServer().getPluginManager().callEvent(event);
                ping.setMotD(motd);

            } catch(Throwable ignored) {
                warn(String.format("Can't generate a correct motd, Latest issue was generated by the next motd: (%s-%s)", motdType.name(), motdName));
                if (main.getSpigotControl().isDetailed()) {
                    error("Information: ");

                    if (ignored.getMessage() != null) {
                        error("Message: " + ignored.getMessage());
                    }

                    if (ignored.getLocalizedMessage() != null) {
                        error("LocalizedMessage: " + ignored.getLocalizedMessage());
                    }
                    if(ignored.getStackTrace() != null) {
                        error("StackTrace: ");
                        for(StackTraceElement line : ignored.getStackTrace()) {
                            error("(" + line.getLineNumber() + ") " + line.toString());
                        }
                    }

                    error("Suppressed: " + Arrays.toString(ignored.getSuppressed()));
                    error("Class: " + ignored.getClass().getName() +".class");
                    error("Plugin version:" + PixelSpigot.getInstance().getDescription().getVersion());
                    error("---------------");
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
        warn("Can't read image: &b" + filePath + "&f. Please check image size: 64x64 or check if the image isn't corrupted.");
    }
    private String getName(String userName, Player player) {
        if (userName.contains("UNKNOWN") &&
                userName.contains(player.getAddress() + ""))
            userName = main.getSpigotControl().getControl(Files.SETTINGS).getString("settings.defaultUnknownUserName");
        return userName;
    }
    @SuppressWarnings("unused")
    public PacketAdapter getPacketAdapter() {
        return this.packetAdapter;
    }
}