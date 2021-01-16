package dev.mruniverse.pixelmotd.listeners;

import dev.mruniverse.pixelmotd.PixelBungee;
import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.listeners.bungeecord.MotdLoadEvent;
import dev.mruniverse.pixelmotd.utils.Extras;
import dev.mruniverse.pixelmotd.utils.PixelConverter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

import static dev.mruniverse.pixelmotd.utils.Logger.info;

public class BungeeEvents implements Listener {
    private final PixelBungee plugin;

    public BungeeEvents(PixelBungee plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }


    @EventHandler
    public void onLoginEvent(LoginEvent event) {
        if (event.isCancelled()) return;

        Configuration editableConfig = plugin.getBungeeControl().getControl(Files.EDITABLE);

        if (editableConfig.getString("whitelist.check-mode").equalsIgnoreCase("LoginEvent")) {
            if (editableConfig.getBoolean("whitelist.toggle")) {
                if (!editableConfig.getStringList("whitelist.players-name").contains(event.getConnection().getName()) &&
                        !editableConfig.getStringList("whitelist.players-uuid").contains(event.getConnection().getUniqueId().toString())) {

                    String kickReason = PixelConverter.StringListToString(editableConfig.getStringList("whitelist.kick-message"));
                    event.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickReason.replace("%whitelist_author%", plugin.getBungeeControl().getWhitelistAuthor()).replace("%type%", "Server"))));
                }
                return;
            }
        }
        if(plugin.getBungeeControl().getControl(Files.EDITABLE).getBoolean("blacklist.toggle")) {
            if(plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-name").contains(event.getConnection().getName()) || plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-uuid").contains(event.getConnection().getUniqueId().toString())) {
                String kickReason = PixelConverter.StringListToString(plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.kick-message"));
                event.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickReason.replace("%nick%", event.getConnection().getName()).replace("%type%","Server"))));
                return;
            }
        }
        if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.block-users.enabled")) {
            if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.block-users.ignoreCase")) {
                String name = event.getConnection().getName().toLowerCase();
                List<String> blackList = new ArrayList<>();
                for(String nameToLow : plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-users.blockedUsers")) {
                    blackList.add(nameToLow.toLowerCase());
                }
                if(blackList.contains(name)) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-users.kickMessage"));
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_name%",name))));
                    return;
                }
            } else {
                if(plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-users.blockedUsers").contains(event.getConnection().getName())) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-users.kickMessage"));
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_name%",event.getConnection().getName()))));
                    return;
                }
            }
        }
        if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.block-words-in-name.enabled")) {
            boolean magicalEdition = false;
            String blockedWord = "";
            if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.block-words-in-name.ignoreCase")) {
                String name = event.getConnection().getName().toLowerCase();
                for(String nameToLow : plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.blockedWords")) {
                    if(name.contains(nameToLow.toLowerCase())) {
                        magicalEdition = true;
                        blockedWord = nameToLow.toLowerCase();
                    }
                }
                if(magicalEdition) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.kickMessage"));
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_word%",blockedWord))));
                }

            } else {
                for(String name : plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.blockedWords")) {
                    if(event.getConnection().getName().contains(name)) {
                        magicalEdition = true;
                    }
                }
                if(magicalEdition) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.kickMessage"));
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_word%",blockedWord))));
                }
            }
        }
    }
    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
        if(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("whitelist.check-mode").equalsIgnoreCase("LoginEvent")) {
            if(plugin.getBungeeControl().getControl(Files.EDITABLE).getBoolean("whitelist.toggle")) {
                if(!plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(event.getPlayer().getName()) && !plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-uuid").contains(event.getPlayer().getUniqueId().toString())) {
                    String kickReason = PixelConverter.StringListToString(plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.kick-message"));
                    event.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickReason.replace("%whitelist_author%", plugin.getBungeeControl().getWhitelistAuthor()).replace("%type%","Server"))));
                }
            }
        }
    }

    @EventHandler
    public void onMotdLoad(MotdLoadEvent event) {
        info("&aMotdLoad event: ");
        info("&aLine1: " + event.getLine1());
        info("&aLine2: " + event.getLine2());
        info("&aOnline: " + event.getOnline().getResult());
        info("&aMax: " + event.getMax().getResult());
        info("&aFull Motd: ");
        info(event.getFullMotd());
        info("&aMotd Name: " + event.getMotdName());
        info("&aMotd Protocol: " + event.getProtocolMessage());
        info("&aMotd Hover: " + event.getHover());
        info("&aMotd Type: " + event.getMotdType().toString());
        info("&aIs Custom Hex Motd: " + event.isCustomHexMotd());
    }
    
    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        if(event.isCancelled()) return;
        String name = event.getTarget().getName();
        ProxiedPlayer player = event.getPlayer();
        if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.servers-whitelist.toggle")) {
            if(plugin.getBungeeControl().getControl(Files.MODULES).contains(Extras.getServerPath(Whitelist.STATUS,name))) {
                if(!plugin.getBungeeUtils().getPlayers(WhitelistMembers.NAMEs,name).contains(player.getName()) || !plugin.getBungeeUtils().getPlayers(WhitelistMembers.UUIDs,name).contains(player.getUniqueId().toString())) {
                    for (String message : plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.servers-whitelist.kickMessage")) {
                        message = message.replace("%whitelist_author%", plugin.getBungeeControl().getControl(Files.MODULES).getString(Extras.getServerPath(Whitelist.AUTHOR,name)))
                            .replace("%whitelist_reason%", plugin.getBungeeControl().getControl(Files.MODULES).getString(Extras.getServerPath(Whitelist.REASON,name))).replace("%type%","server").replace("%value%",name);
                        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
                    }
                    event.setCancelled(true);
                }
            }
        }
        if(event.isCancelled()) return;
        if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.servers-blacklist.toggle")) {
            if(plugin.getBungeeControl().getControl(Files.MODULES).contains(Extras.getServerPath(Blacklist.STATUS,name))) {
                if(!plugin.getBungeeUtils().getPlayers(BlacklistMembers.NAMEs,name).contains(player.getName()) || !plugin.getBungeeUtils().getPlayers(BlacklistMembers.UUIDs,name).contains(player.getUniqueId().toString())) {
                    for (String message : plugin.getBungeeControl().getControl(Files.MODULES).getStringList("modules.servers-blacklist.kickMessage")) {
                        message = message.replace("%blacklist_author%", "??")
                                .replace("%blacklist_reason%", plugin.getBungeeControl().getControl(Files.MODULES).getString(Extras.getServerPath(Blacklist.REASON,name))).replace("%type%","server").replace("%value%",name);
                        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
