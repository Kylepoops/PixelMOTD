package dev.mruniverse.pixelmotd.commands;

import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.PixelBungee;
import dev.mruniverse.pixelmotd.utils.Extras;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.mruniverse.pixelmotd.utils.bungeeLogger.error;
import static dev.mruniverse.pixelmotd.utils.bungeeLogger.sendMessage;

public class BungeeCMD extends Command {
    private final PixelBungee plugin;

    private final String cmd;

    public BungeeCMD(PixelBungee plugin, String command) {
        super(command);
        this.plugin = plugin;
        this.cmd = command;
    }

    private String getUniqueId(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            return player.getUniqueId().toString();
        }
        return "??";
    }
    private boolean hasPermission(CommandSender sender, String permission) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            if (!player.hasPermission(permission)) {
                sendMessage(player, plugin.getBungeeUtils().getPermissionMessage(permission));
            }
            return player.hasPermission(permission);
        }
        return true;
    }

    private String getStatus(String location) {
        if(location.equalsIgnoreCase("Global")) {
            if(plugin.getBungeeControl().getWhitelistStatus()) {
                return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.status.on");
            }
            return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.status.off");
        }
        if(plugin.getBungeeControl().getControl(Files.MODULES).get("modules.world-whitelist.worlds." + location +".whitelist-status") != null) {
            if(plugin.getBungeeControl().getControl(Files.MODULES).getBoolean("modules.world-whitelist.worlds." + location +".whitelist-status")) {
                return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.status.on");
            }
            return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.status.off");
        }
        return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.status.off");
    }
    private String getOnline(String playerName) {
        if(PixelBungee.getInstance().getProxy().getPlayer(playerName) != null) {
            try {
                if (Objects.requireNonNull(PixelBungee.getInstance().getProxy().getPlayer(playerName)).isConnected()) {
                    return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.online-status.online").replace("%server%",Objects.requireNonNull(PixelBungee.getInstance().getProxy().getPlayer(playerName).getServer().getInfo().getName()));
                }
                return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.online-status.offline");
            }catch(Throwable throwable) {
                return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.online-status.offline");
            }
        }

        return plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.online-status.offline");
    }
    private String getAuthor(String location) {
        if(plugin.getBungeeControl().getControl(Files.MODULES).get("modules.server-whitelist.servers." + location +".whitelist-author") != null) {
            return plugin.getBungeeControl().getControl(Files.COMMAND).getString("modules.server-whitelist.servers." + location +".whitelist-author");
        }
        return "Console";
    }
    private void sendMain(CommandSender sender) {
        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.help")) {
            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
            if(lines.contains("%author%")) lines = lines.replace("%author%", PixelBungee.getInstance().getDescription().getAuthor());
            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
            sendMessage(sender, lines);
        }
    }
    @SuppressWarnings({"ConstantConditions"})
    public void execute(CommandSender sender, String[] args) {
        try {
            if (plugin.getBungeeControl().isCommandEnabled()) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                    if (hasPermission(sender, "pixelmotd.command.help")) {
                        sendMain(sender);
                    }
                    return;
                }
                //motdLists.add("&6/%cmd% &e- &fMain Command");
                //if (bungeeMode) {
                //    motdLists.add("&6/%cmd% whitelist (global-serverName) [on-off]");
                //} else {
                //    motdLists.add("&6/%cmd% whitelist (global-worldName) [on-off]");
                //}
                //motdLists.add("&6/%cmd% add (whitelist-blacklist) (playerName-playerUUID) &e- &fadd a player to your list.");
                //motdLists.add("&6/%cmd% remove (whitelist-blacklist) (playerName-playerUUID) &e- &fremove a player from your list.");
                //motdLists.add("&6/%cmd% reload (all-settings-edit-modules-cmd-motds)");
                //motdLists.add("&6/%cmd% modules toggle (moduleName)");
                //motdLists.add("&6/%cmd% modules info (moduleName)");
                //motdLists.add("&6/%cmd% modules list");
                //motdLists.add("&6/%cmd% externalModules toggle (moduleName)");
                //motdLists.add("&6/%cmd% externalModules info (moduleName)");
                //motdLists.add("&6/%cmd% externalModules list");
                if (args[0].equalsIgnoreCase("whitelist")) {
                    if (args.length == 1) {
                        if (hasPermission(sender, "pixelmotd.command.whitelist.toggle")) {
                            sendMain(sender);
                            return;
                        }
                        return;
                    }
                    if (args[1].equalsIgnoreCase("global")) {
                        if (args.length == 2) {
                            boolean userMessage;
                            for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", plugin.getBungeeControl().getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        sendMessage(sender, lines);
                                    }
                                } else {
                                    sendMessage(sender, lines);
                                }
                            }
                            for(String players : plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name")) {
                                String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                sendMessage(sender,line);
                            }
                            for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", plugin.getBungeeControl().getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        sendMessage(sender, lines);
                                    }
                                } else {
                                    sendMessage(sender, lines);
                                }
                            }
                            for(String uuids : plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-uuid")) {
                                String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                sendMessage(sender,line);
                            }
                            for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", plugin.getBungeeControl().getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        sendMessage(sender, lines);
                                    }
                                } else {
                                    sendMessage(sender, lines);
                                }
                            }
                            return;
                        }
                        if(args[2].equalsIgnoreCase("on")) {
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.toggle",true);
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.author","Console");
                            plugin.getBungeeControl().save(SaveMode.EDITABLE);
                            plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-enabled"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.toggle",false);
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.author","Console");
                            plugin.getBungeeControl().save(SaveMode.EDITABLE);
                            plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-disabled"));
                            return;
                        }
                    }
                    if (args.length == 2) {
                        boolean userMessage;
                        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    sendMessage(sender, lines);
                                }
                            } else {
                                sendMessage(sender, lines);
                            }
                        }
                        for(String players : plugin.getBungeeUtils().getPlayers(WhitelistMembers.NAMEs,args[1])) {
                            String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            sendMessage(sender,line);
                        }
                        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage=true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    sendMessage(sender, lines);
                                }
                            } else {
                                sendMessage(sender, lines);
                            }
                        }
                        for(String uuids : plugin.getBungeeUtils().getPlayers(WhitelistMembers.UUIDs,args[1])) {
                            String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            sendMessage(sender,line);
                        }
                        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                            userMessage = false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    sendMessage(sender, lines);
                                }
                            } else {
                                sendMessage(sender, lines);
                            }
                        }
                        return;
                    }
                    if(args.length == 3) {
                        if(args[2].equalsIgnoreCase("on")) {
                            plugin.getBungeeControl().getControl(Files.MODULES).set(Extras.getServerPath(Whitelist.STATUS,args[1]),true);
                            plugin.getBungeeControl().save(SaveMode.MODULES);
                            plugin.getBungeeControl().reloadFile(SaveMode.MODULES);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.status-enabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","whitelist"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            plugin.getBungeeControl().getControl(Files.MODULES).set(Extras.getServerPath(Whitelist.STATUS,args[1]),false);
                            plugin.getBungeeControl().save(SaveMode.MODULES);
                            plugin.getBungeeControl().reloadFile(SaveMode.MODULES);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.status-disabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","whitelist"));
                            return;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("blacklist")) {
                    if (args.length == 1) {
                        if (hasPermission(sender, "pixelmotd.command.blacklist.toggle")) {
                            sendMain(sender);
                            return;
                        }
                        return;
                    }
                    if (args[1].equalsIgnoreCase("global")) {
                        if (args.length == 2) {
                            boolean userMessage;
                            for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.blacklist.list.top")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        sendMessage(sender, lines);
                                    }
                                } else {
                                    sendMessage(sender, lines);
                                }
                            }
                            for(String players : plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-name")) {
                                String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.blacklist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                sendMessage(sender,line);
                            }
                            for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.blacklist.list.mid")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        sendMessage(sender, lines);
                                    }
                                } else {
                                    sendMessage(sender, lines);
                                }
                            }
                            for(String uuids : plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-uuid")) {
                                String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.blacklist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                sendMessage(sender,line);
                            }
                            for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.blacklist.list.bot")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        sendMessage(sender, lines);
                                    }
                                } else {
                                    sendMessage(sender, lines);
                                }
                            }
                            return;
                        }
                        if(args[2].equalsIgnoreCase("on")) {
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.toggle",true);
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.author","Console");
                            plugin.getBungeeControl().save(SaveMode.EDITABLE);
                            plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-enabled"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.toggle",false);
                            plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.author","Console");
                            plugin.getBungeeControl().save(SaveMode.EDITABLE);
                            plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-disabled"));
                            return;
                        }
                    }
                    if (args.length == 2) {
                        boolean userMessage;
                        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.blacklist.list.top")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    sendMessage(sender, lines);
                                }
                            } else {
                                sendMessage(sender, lines);
                            }
                        }
                        for(String players : plugin.getBungeeUtils().getPlayers(BlacklistMembers.NAMEs,args[1])) {
                            String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.blacklist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            sendMessage(sender,line);
                        }
                        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.blacklist.list.mid")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage=true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    sendMessage(sender, lines);
                                }
                            } else {
                                sendMessage(sender, lines);
                            }
                        }
                        for(String uuids : plugin.getBungeeUtils().getPlayers(BlacklistMembers.UUIDs,args[1])) {
                            String line= plugin.getBungeeControl().getControl(Files.COMMAND).getString("command.blacklist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            sendMessage(sender,line);
                        }
                        for(String lines : plugin.getBungeeControl().getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                            userMessage = false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelBungee.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    sendMessage(sender, lines);
                                }
                            } else {
                                sendMessage(sender, lines);
                            }
                        }
                        return;
                    }
                    if(args.length == 3) {
                        if(args[2].equalsIgnoreCase("on")) {
                            plugin.getBungeeControl().getControl(Files.MODULES).set(Extras.getServerPath(Blacklist.STATUS,args[1]),true);
                            plugin.getBungeeControl().save(SaveMode.MODULES);
                            plugin.getBungeeControl().reloadFile(SaveMode.MODULES);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.status-enabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","blacklist"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            plugin.getBungeeControl().getControl(Files.MODULES).set(Extras.getServerPath(Blacklist.STATUS,args[1]),false);
                            plugin.getBungeeControl().save(SaveMode.MODULES);
                            plugin.getBungeeControl().reloadFile(SaveMode.MODULES);
                            sendMessage(sender, plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.status-disabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","blacklist"));
                            return;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (hasPermission(sender, "pixelmotd.command.whitelist.add")) {
                        if (args.length == 1) {
                            sendMain(sender);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("whitelist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                        sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                        List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.add(args[3]);
                                        plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                        plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                    if (!plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[3])) {
                                        sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                        List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                        list.add(args[3]);
                                        plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                        plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                        plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                    return;
                                }
                                sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = new ArrayList<>();
                                list.add(args[2]);
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (!plugin.getBungeeUtils().getPlayers(WhitelistMembers.UUIDs,args[2]).contains(args[3])) {
                                    sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = plugin.getBungeeUtils().getPlayers(WhitelistMembers.UUIDs, args[2]);
                                    list.add(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_UUID, args[2]), list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                            }
                            if (!plugin.getBungeeUtils().getPlayers(WhitelistMembers.NAMEs,args[2]).contains(args[3])) {
                                sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = plugin.getBungeeUtils().getPlayers(WhitelistMembers.NAMEs,args[2]);
                                list.add(args[3]);
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_NAME,args[2]), list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return;
                        }
                        if (args[1].equalsIgnoreCase("blacklist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                        List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.add(args[3]);
                                        plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                        sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                        plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                        plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                    List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.add(args[3]);
                                    sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                List<String> list = new ArrayList<>();
                                list.add(args[3]);
                                sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (!plugin.getBungeeUtils().getPlayers(BlacklistMembers.UUIDs,args[2]).contains(args[3])) {
                                    List<String> list = plugin.getBungeeUtils().getPlayers(BlacklistMembers.UUIDs, args[2]);
                                    list.add(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_UUID, args[2]), list);
                                    sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.already-blacklisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                return;
                            }
                            if (!plugin.getBungeeUtils().getPlayers(BlacklistMembers.NAMEs,args[2]).contains(args[3])) {
                                List<String> list = plugin.getBungeeUtils().getPlayers(BlacklistMembers.NAMEs,args[2]);
                                list.add(args[3]);
                                sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_NAME,args[2]), list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.already-blacklisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return;
                        }
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (hasPermission(sender, "pixelmotd.command.whitelist.remove")) {
                        if (args.length == 1) {
                            sendMain(sender);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("whitelist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                        sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3])));
                                        List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.remove(args[3]);
                                        plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                        plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                }
                                if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                    if (plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[3])) {
                                        sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[3])));
                                        List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                        list.remove(args[3]);
                                        plugin.getBungeeControl().getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                        plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                        plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                    return;
                                }
                                sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (plugin.getBungeeUtils().getPlayers(WhitelistMembers.UUIDs,args[2]).contains(args[3])) {
                                    sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = plugin.getBungeeUtils().getPlayers(WhitelistMembers.UUIDs, args[2]);
                                    list.remove(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_UUID, args[2]), list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                            }
                            if (plugin.getBungeeUtils().getPlayers(WhitelistMembers.NAMEs,args[2]).contains(args[3])) {
                                sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = plugin.getBungeeUtils().getPlayers(WhitelistMembers.NAMEs,args[2]);
                                list.remove(args[3]);
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_NAME,args[2]), list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return;
                        }
                        if (args[1].equalsIgnoreCase("blacklist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                        List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.add(args[3]);
                                        plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                        sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                        plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                        plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                if (plugin.getBungeeControl().getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                    List<String> list = plugin.getBungeeControl().getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.add(args[3]);
                                    sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                List<String> list = new ArrayList<>();
                                list.add(args[3]);
                                sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (plugin.getBungeeUtils().getPlayers(BlacklistMembers.UUIDs,args[2]).contains(args[3])) {
                                    List<String> list = plugin.getBungeeUtils().getPlayers(BlacklistMembers.UUIDs, args[2]);
                                    list.remove(args[3]);
                                    plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_UUID, args[2]), list);
                                    sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                    plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                return;
                            }
                            if (plugin.getBungeeUtils().getPlayers(BlacklistMembers.NAMEs,args[2]).contains(args[3])) {
                                List<String> list = plugin.getBungeeUtils().getPlayers(BlacklistMembers.NAMEs,args[2]);
                                list.remove(args[3]);
                                sendMessage(sender, Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                plugin.getBungeeControl().getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_NAME,args[2]), list);
                                plugin.getBungeeControl().save(SaveMode.EDITABLE);
                                plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            sendMessage(sender, getMsg(Objects.requireNonNull(plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return;
                        }
                    }
                    return;
                }
                if(args[0].equalsIgnoreCase("modules")) {
                    sendMessage(sender,"&cCurrently working");
                    //modules setup
                }
                if(args[0].equalsIgnoreCase("externalModules")) {
                    sendMessage(sender,"&cCurrently working");
                    //modules setup
                }
                //(all-settings-edit-modules-cmd-motds)
                if (args[0].equalsIgnoreCase("reload")) {
                    if (hasPermission(sender, "pixelmotd.command.reload")) {
                        if (args.length == 1) {
                            sendMain(sender);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("all")) {
                            long timeMS = System.currentTimeMillis();
                            plugin.getBungeeControl().reloadFile(SaveMode.ALL);
                            String reload = plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "ALL");
                            sendMessage(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("cmd")) {
                            long timeMS = System.currentTimeMillis();
                            plugin.getBungeeControl().reloadFile(SaveMode.COMMAND);
                            String reload = plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "COMMANDS");
                            sendMessage(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("edit")) {
                            long timeMS = System.currentTimeMillis();
                            plugin.getBungeeControl().reloadFile(SaveMode.EDITABLE);
                            String reload = plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "EDIT");
                            sendMessage(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("modules")) {
                            long timeMS = System.currentTimeMillis();
                            plugin.getBungeeControl().reloadFile(SaveMode.MODULES);
                            String reload = plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MODULES");
                            sendMessage(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("motds")) {
                            long timeMS = System.currentTimeMillis();
                            plugin.getBungeeControl().reloadFile(SaveMode.MOTDS);
                            String reload = plugin.getBungeeControl().getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MOTDS");
                            sendMessage(sender, reload);
                        }
                    }
                }
            }
        } catch(Throwable throwable) {
            sendMessage(sender,"&cPixelMOTD found a error using this command. Check console and report this error to the developer, please enable option &lshow-detailed-errors &cfor more info for the developer.");
            if(plugin.getBungeeControl().isDetailed()) {
                error("Information:");
                if(throwable.getMessage() != null) {
                    error("Message: " + throwable.getMessage());
                }
                if(throwable.getLocalizedMessage() != null) {
                    error("LocalizedMessage: " + throwable.getLocalizedMessage());
                }
                if(throwable.getStackTrace() != null) {
                    error("StackTrace: ");
                    for(StackTraceElement line : throwable.getStackTrace()) {
                        error("(" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if(throwable.getSuppressed() != null) {
                    error("Suppressed: " + Arrays.toString(throwable.getSuppressed()));
                }
                if(throwable.getClass().getName() != null) {
                    error("Class: " + throwable.getClass().getName() + ".class");
                }
                error("Plugin version:" + PixelBungee.getInstance().getDescription().getVersion());
                error("---------------");
            }
        }
    }
    private String getMsg(String message) {
        if(message == null) return "";
        return message;
    }
}