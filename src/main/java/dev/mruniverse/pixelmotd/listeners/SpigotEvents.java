package dev.mruniverse.pixelmotd.listeners;

import dev.mruniverse.pixelmotd.enums.BlacklistMembers;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.WhitelistMembers;
import dev.mruniverse.pixelmotd.PixelSpigot;
import dev.mruniverse.pixelmotd.utils.PixelConverter;
import dev.mruniverse.pixelmotd.utils.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class SpigotEvents implements Listener {
    private final PixelSpigot plugin;

    public SpigotEvents(PixelSpigot plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void playerLoginEvent(PlayerLoginEvent event) {
        //database - Setup
        //bungeePixelMOTD.getInstance().getDataManager().setAddress(event.getConnection().getVirtualHost().getAddress(), event.getConnection().getName());
        //whitelist - blacklist and modules - Setup
        if (plugin.getSpigotControl().getControl(Files.EDITABLE).getBoolean("whitelist.toggle")) {
            if (!plugin.getSpigotControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(event.getPlayer().getName()) && !plugin.getSpigotControl().getControl(Files.EDITABLE).getStringList("whitelist.players-uuid").contains(event.getPlayer().getUniqueId().toString())) {
                String kickReason = PixelConverter.StringListToString(plugin.getSpigotControl().getControl(Files.EDITABLE).getStringList("whitelist.kick-message"));
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,ChatColor.translateAlternateColorCodes('&', kickReason.replace("%whitelist_author%", plugin.getSpigotControl().getWhitelistAuthor()).replace("%type%", "Server")));
            }
            return;
        }
        if(plugin.getSpigotControl().getControl(Files.EDITABLE).getBoolean("blacklist.toggle")) {
            if(plugin.getSpigotControl().getControl(Files.EDITABLE).getStringList("blacklist.players-name").contains(event.getPlayer().getName()) || plugin.getSpigotControl().getControl(Files.EDITABLE).getStringList("blacklist.players-uuid").contains(event.getPlayer().getUniqueId().toString())) {
                String kickReason = PixelConverter.StringListToString(plugin.getSpigotControl().getControl(Files.EDITABLE).getStringList("blacklist.kick-message"));
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,ChatColor.translateAlternateColorCodes('&', kickReason.replace("%nick%", event.getPlayer().getName()).replace("%type%","Server")));
                return;
            }
        }
        if(plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.block-users.enabled")) {
            if(plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.block-users.ignoreCase")) {
                String name = event.getPlayer().getName().toLowerCase();
                List<String> blackList = new ArrayList<>();
                for(String nameToLow : plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-users.blockedUsers")) {
                    blackList.add(nameToLow.toLowerCase());
                }
                if(blackList.contains(name)) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-users.kickMessage"));
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_name%",name)));
                    return;
                }
            } else {
                if(plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-users.blockedUsers").contains(event.getPlayer().getName())) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-users.kickMessage"));
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_name%",event.getPlayer().getName())));
                    return;
                }
            }
        }
        if(plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.block-words-in-name.enabled")) {
            boolean magicalEdition = false;
            String blockedWord = "";
            if(plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.block-words-in-name.ignoreCase")) {
                String name = event.getPlayer().getName().toLowerCase();
                for(String nameToLow : plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.blockedWords")) {
                    if(name.contains(nameToLow.toLowerCase())) {
                        magicalEdition = true;
                        blockedWord = nameToLow.toLowerCase();
                    }
                }
                if(magicalEdition) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.kickMessage"));
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_word%",blockedWord)));
                }

            } else {
                for(String name : plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.blockedWords")) {
                    if(event.getPlayer().getName().contains(name)) {
                        magicalEdition = true;
                    }
                }
                if(magicalEdition) {
                    String kickMsg = PixelConverter.StringListToString(plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.block-words-in-name.kickMessage"));
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,ChatColor.translateAlternateColorCodes('&', kickMsg.replace("%blocked_word%",blockedWord)));
                }
            }
        }
    }
    @EventHandler
    public void teleport(PlayerTeleportEvent event) {
        if(event.isCancelled()) return;
        World actualWorld = event.getPlayer().getWorld();
        World nextWorld = Objects.requireNonNull(event.getTo()).getWorld();
        if(nextWorld == null) nextWorld = actualWorld;
        if (plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.worlds-whitelist.toggle")) {
            if (actualWorld != nextWorld &&
                    plugin.getSpigotControl().getControl(Files.MODULES).contains("modules.worlds-whitelist.worlds." + nextWorld.getName() + ".whitelist-status") &&
                    plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.worlds-whitelist.worlds." + nextWorld.getName() + ".whitelist-status")) {
                if(!plugin.getSpigotUtils().getPlayers(WhitelistMembers.NAMEs,nextWorld.getName()).contains(event.getPlayer().getName()) || !plugin.getSpigotUtils().getPlayers(WhitelistMembers.UUIDs,nextWorld.getName()).contains(event.getPlayer().getUniqueId().toString())) {
                    for (String message : plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.worlds-whitelist.kickMessage")) {
                        message = message.replace("%whitelist_author%", Objects.requireNonNull(plugin.getSpigotControl().getControl(Files.MODULES).getString("modules.worlds-whitelist.worlds." + nextWorld.getName() + ".whitelist-author"))).replace("%whitelist_reason%", Objects.requireNonNull(plugin.getSpigotControl().getControl(Files.MODULES).getString("modules.worlds-whitelist.worlds." + nextWorld.getName() + ".whitelist-reason")))
                                .replace("%type%","world").replace("%value%",nextWorld.getName());
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    event.setCancelled(true);
                }
            }

        }
        if (plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.worlds-blacklist.toggle")) {
            if (!event.isCancelled()) {
                if (actualWorld != nextWorld &&
                        plugin.getSpigotControl().getControl(Files.MODULES).contains("modules.worlds-blacklist.worlds." + nextWorld.getName() + ".blacklist-status") &&
                        plugin.getSpigotControl().getControl(Files.MODULES).getBoolean("modules.worlds-blacklist.worlds." + nextWorld.getName() + ".blacklist-status")) {
                    if (plugin.getSpigotUtils().getPlayers(BlacklistMembers.NAMEs, nextWorld.getName()).contains(event.getPlayer().getName()) || plugin.getSpigotUtils().getPlayers(BlacklistMembers.UUIDs, nextWorld.getName()).contains(event.getPlayer().getUniqueId().toString())) {
                        for (String message : plugin.getSpigotControl().getControl(Files.MODULES).getStringList("modules.worlds-whitelist.kickMessage")) {
                            message = message.replace("%blacklist_author%", "??").replace("%blacklist_reason%", Objects.requireNonNull(plugin.getSpigotControl().getControl(Files.MODULES).getString("modules.worlds-blacklist.worlds." + nextWorld.getName() + ".blacklist-reason")))
                                    .replace("%type%","world").replace("%value%",nextWorld.getName());
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}