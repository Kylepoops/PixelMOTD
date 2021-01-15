package dev.mruniverse.pixelmotd.commands;

import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.init.BungeePixel;
import dev.mruniverse.pixelmotd.utils.Extras;
import dev.mruniverse.pixelmotd.utils.bungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BungeeCMD extends Command {
    private final String cmd;


    public BungeeCMD(String command) {
        super(command);
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
                bungeeUtils.sendColored(player, bungeeUtils.getPermissionMessage(permission));
            }
            return player.hasPermission(permission);
        }
        return true;
    }

    private String getStatus(String location) {
        if(location.equalsIgnoreCase("Global")) {
            if(BungeeControl.getWhitelistStatus()) {
                return BungeeControl.getControl(Files.COMMAND).getString("command.status.on");
            }
            return BungeeControl.getControl(Files.COMMAND).getString("command.status.off");
        }
        if(BungeeControl.getControl(Files.MODULES).get("modules.world-whitelist.worlds." + location +".whitelist-status") != null) {
            if(BungeeControl.getControl(Files.MODULES).getBoolean("modules.world-whitelist.worlds." + location +".whitelist-status")) {
                return BungeeControl.getControl(Files.COMMAND).getString("command.status.on");
            }
            return BungeeControl.getControl(Files.COMMAND).getString("command.status.off");
        }
        return BungeeControl.getControl(Files.COMMAND).getString("command.status.off");
    }
    private String getOnline(String playerName) {
        if(BungeePixel.getInstance().getProxy().getPlayer(playerName) != null) {
            try {
                if (Objects.requireNonNull(BungeePixel.getInstance().getProxy().getPlayer(playerName)).isConnected()) {
                    return BungeeControl.getControl(Files.COMMAND).getString("command.online-status.online").replace("%server%",Objects.requireNonNull(BungeePixel.getInstance().getProxy().getPlayer(playerName).getServer().getInfo().getName()));
                }
                return BungeeControl.getControl(Files.COMMAND).getString("command.online-status.offline");
            }catch(Throwable throwable) {
                return BungeeControl.getControl(Files.COMMAND).getString("command.online-status.offline");
            }
        }

        return BungeeControl.getControl(Files.COMMAND).getString("command.online-status.offline");
    }
    private String getAuthor(String location) {
        if(BungeeControl.getControl(Files.MODULES).get("modules.server-whitelist.servers." + location +".whitelist-author") != null) {
            return BungeeControl.getControl(Files.COMMAND).getString("modules.server-whitelist.servers." + location +".whitelist-author");
        }
        return "Console";
    }
    private void sendMain(CommandSender sender) {
        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.help")) {
            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
            if(lines.contains("%author%")) lines = lines.replace("%author%", BungeePixel.getInstance().getDescription().getAuthor());
            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
            bungeeUtils.sendColored(sender,lines);
        }
    }
    @SuppressWarnings({"ConstantConditions"})
    public void execute(CommandSender sender, String[] args) {
        try {
            if (BungeeControl.isCommandEnabled()) {
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
                            for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", BungeeControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        bungeeUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            }
                            for(String players : BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name")) {
                                String line= BungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                bungeeUtils.sendColored(sender,line);
                            }
                            for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", BungeeControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        bungeeUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            }
                            for(String uuids : BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid")) {
                                String line= BungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                bungeeUtils.sendColored(sender,line);
                            }
                            for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", BungeeControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        bungeeUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            }
                            return;
                        }
                        if(args[2].equalsIgnoreCase("on")) {
                            BungeeControl.getControl(Files.EDITABLE).set("whitelist.toggle",true);
                            BungeeControl.getControl(Files.EDITABLE).set("whitelist.author","Console");
                            BungeeControl.save(SaveMode.EDITABLE);
                            BungeeControl.reloadFile(SaveMode.EDITABLE);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-enabled"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            BungeeControl.getControl(Files.EDITABLE).set("whitelist.toggle",false);
                            BungeeControl.getControl(Files.EDITABLE).set("whitelist.author","Console");
                            BungeeControl.save(SaveMode.EDITABLE);
                            BungeeControl.reloadFile(SaveMode.EDITABLE);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-disabled"));
                            return;
                        }
                    }
                    if (args.length == 2) {
                        boolean userMessage;
                        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            } else {
                                bungeeUtils.sendColored(sender, lines);
                            }
                        }
                        for(String players : bungeeUtils.getPlayers(WhitelistMembers.NAMEs,args[1])) {
                            String line= BungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            bungeeUtils.sendColored(sender,line);
                        }
                        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage=true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            } else {
                                bungeeUtils.sendColored(sender, lines);
                            }
                        }
                        for(String uuids : bungeeUtils.getPlayers(WhitelistMembers.UUIDs,args[1])) {
                            String line= BungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            bungeeUtils.sendColored(sender,line);
                        }
                        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                            userMessage = false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            } else {
                                bungeeUtils.sendColored(sender, lines);
                            }
                        }
                        return;
                    }
                    if(args.length == 3) {
                        if(args[2].equalsIgnoreCase("on")) {
                            BungeeControl.getControl(Files.MODULES).set(Extras.getServerPath(Whitelist.STATUS,args[1]),true);
                            BungeeControl.save(SaveMode.MODULES);
                            BungeeControl.reloadFile(SaveMode.MODULES);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.status-enabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","whitelist"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            BungeeControl.getControl(Files.MODULES).set(Extras.getServerPath(Whitelist.STATUS,args[1]),false);
                            BungeeControl.save(SaveMode.MODULES);
                            BungeeControl.reloadFile(SaveMode.MODULES);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.status-disabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","whitelist"));
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
                            for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.top")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        bungeeUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            }
                            for(String players : BungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name")) {
                                String line= BungeeControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                bungeeUtils.sendColored(sender,line);
                            }
                            for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.mid")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        bungeeUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            }
                            for(String uuids : BungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid")) {
                                String line= BungeeControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                bungeeUtils.sendColored(sender,line);
                            }
                            for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.bot")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof ProxiedPlayer) {
                                        bungeeUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            }
                            return;
                        }
                        if(args[2].equalsIgnoreCase("on")) {
                            BungeeControl.getControl(Files.EDITABLE).set("blacklist.toggle",true);
                            BungeeControl.getControl(Files.EDITABLE).set("blacklist.author","Console");
                            BungeeControl.save(SaveMode.EDITABLE);
                            BungeeControl.reloadFile(SaveMode.EDITABLE);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-enabled"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            BungeeControl.getControl(Files.EDITABLE).set("blacklist.toggle",false);
                            BungeeControl.getControl(Files.EDITABLE).set("blacklist.author","Console");
                            BungeeControl.save(SaveMode.EDITABLE);
                            BungeeControl.reloadFile(SaveMode.EDITABLE);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-disabled"));
                            return;
                        }
                    }
                    if (args.length == 2) {
                        boolean userMessage;
                        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.top")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            } else {
                                bungeeUtils.sendColored(sender, lines);
                            }
                        }
                        for(String players : bungeeUtils.getPlayers(BlacklistMembers.NAMEs,args[1])) {
                            String line= BungeeControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            bungeeUtils.sendColored(sender,line);
                        }
                        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.mid")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage=true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            } else {
                                bungeeUtils.sendColored(sender, lines);
                            }
                        }
                        for(String uuids : bungeeUtils.getPlayers(BlacklistMembers.UUIDs,args[1])) {
                            String line= BungeeControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            bungeeUtils.sendColored(sender,line);
                        }
                        for(String lines : BungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                            userMessage = false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", BungeePixel.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof ProxiedPlayer) {
                                    bungeeUtils.sendColored(sender, lines);
                                }
                            } else {
                                bungeeUtils.sendColored(sender, lines);
                            }
                        }
                        return;
                    }
                    if(args.length == 3) {
                        if(args[2].equalsIgnoreCase("on")) {
                            BungeeControl.getControl(Files.MODULES).set(Extras.getServerPath(Blacklist.STATUS,args[1]),true);
                            BungeeControl.save(SaveMode.MODULES);
                            BungeeControl.reloadFile(SaveMode.MODULES);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.status-enabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","blacklist"));
                            return;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            BungeeControl.getControl(Files.MODULES).set(Extras.getServerPath(Blacklist.STATUS,args[1]),false);
                            BungeeControl.save(SaveMode.MODULES);
                            BungeeControl.reloadFile(SaveMode.MODULES);
                            bungeeUtils.sendColored(sender, BungeeControl.getControl(Files.EDITABLE).getString("messages.status-disabled").replace("%type%","server").replace("%value%",args[1]).replace("%list%","blacklist"));
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
                                    if (BungeeControl.getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                        bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                        List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.add(args[3]);
                                        BungeeControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        BungeeControl.save(SaveMode.EDITABLE);
                                        BungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                if (BungeeControl.getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                    if (!BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[3])) {
                                        bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                        List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                        list.add(args[3]);
                                        BungeeControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                        BungeeControl.save(SaveMode.EDITABLE);
                                        BungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                    return;
                                }
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = new ArrayList<>();
                                list.add(args[2]);
                                BungeeControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (!bungeeUtils.getPlayers(WhitelistMembers.UUIDs,args[2]).contains(args[3])) {
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = bungeeUtils.getPlayers(WhitelistMembers.UUIDs, args[2]);
                                    list.add(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_UUID, args[2]), list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                            }
                            if (!bungeeUtils.getPlayers(WhitelistMembers.NAMEs,args[2]).contains(args[3])) {
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = bungeeUtils.getPlayers(WhitelistMembers.NAMEs,args[2]);
                                list.add(args[3]);
                                BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_NAME,args[2]), list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
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
                                    if (BungeeControl.getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                        List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.add(args[3]);
                                        BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                        bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                        BungeeControl.save(SaveMode.EDITABLE);
                                        BungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                if (BungeeControl.getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                    List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.add(args[3]);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                    BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                List<String> list = new ArrayList<>();
                                list.add(args[3]);
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (!bungeeUtils.getPlayers(BlacklistMembers.UUIDs,args[2]).contains(args[3])) {
                                    List<String> list = bungeeUtils.getPlayers(BlacklistMembers.UUIDs, args[2]);
                                    list.add(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_UUID, args[2]), list);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.already-blacklisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                return;
                            }
                            if (!bungeeUtils.getPlayers(BlacklistMembers.NAMEs,args[2]).contains(args[3])) {
                                List<String> list = bungeeUtils.getPlayers(BlacklistMembers.NAMEs,args[2]);
                                list.add(args[3]);
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_NAME,args[2]), list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.already-blacklisted")).replace("%type%", "Player").replace("%player%", args[3])));
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
                                    if (BungeeControl.getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                        bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3])));
                                        List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.remove(args[3]);
                                        BungeeControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        BungeeControl.save(SaveMode.EDITABLE);
                                        BungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                }
                                if (BungeeControl.getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                    if (BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[3])) {
                                        bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[3])));
                                        List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                        list.remove(args[3]);
                                        BungeeControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                        BungeeControl.save(SaveMode.EDITABLE);
                                        BungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                    return;
                                }
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (bungeeUtils.getPlayers(WhitelistMembers.UUIDs,args[2]).contains(args[3])) {
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = bungeeUtils.getPlayers(WhitelistMembers.UUIDs, args[2]);
                                    list.remove(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_UUID, args[2]), list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                            }
                            if (bungeeUtils.getPlayers(WhitelistMembers.NAMEs,args[2]).contains(args[3])) {
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = bungeeUtils.getPlayers(WhitelistMembers.NAMEs,args[2]);
                                list.remove(args[3]);
                                BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Whitelist.PLAYERS_NAME,args[2]), list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
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
                                    if (BungeeControl.getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                        List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.add(args[3]);
                                        BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                        bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                        BungeeControl.save(SaveMode.EDITABLE);
                                        BungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                if (BungeeControl.getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                    List<String> list = BungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.add(args[3]);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                    BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                List<String> list = new ArrayList<>();
                                list.add(args[3]);
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                BungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (args[3].contains("-")) {
                                if (bungeeUtils.getPlayers(BlacklistMembers.UUIDs,args[2]).contains(args[3])) {
                                    List<String> list = bungeeUtils.getPlayers(BlacklistMembers.UUIDs, args[2]);
                                    list.remove(args[3]);
                                    BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_UUID, args[2]), list);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    BungeeControl.save(SaveMode.EDITABLE);
                                    BungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                return;
                            }
                            if (bungeeUtils.getPlayers(BlacklistMembers.NAMEs,args[2]).contains(args[3])) {
                                List<String> list = bungeeUtils.getPlayers(BlacklistMembers.NAMEs,args[2]);
                                list.remove(args[3]);
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                BungeeControl.getControl(Files.EDITABLE).set(Extras.getServerPath(Blacklist.PLAYERS_NAME,args[2]), list);
                                BungeeControl.save(SaveMode.EDITABLE);
                                BungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(BungeeControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return;
                        }
                    }
                    return;
                }
                if(args[0].equalsIgnoreCase("modules")) {
                    bungeeUtils.sendColored(sender,"&cCurrently working");
                    //modules setup
                }
                if(args[0].equalsIgnoreCase("externalModules")) {
                    bungeeUtils.sendColored(sender,"&cCurrently working");
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
                            BungeeControl.reloadFile(SaveMode.ALL);
                            String reload = BungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "ALL");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("cmd")) {
                            long timeMS = System.currentTimeMillis();
                            BungeeControl.reloadFile(SaveMode.COMMAND);
                            String reload = BungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "COMMANDS");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("edit")) {
                            long timeMS = System.currentTimeMillis();
                            BungeeControl.reloadFile(SaveMode.EDITABLE);
                            String reload = BungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "EDIT");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("modules")) {
                            long timeMS = System.currentTimeMillis();
                            BungeeControl.reloadFile(SaveMode.MODULES);
                            String reload = BungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MODULES");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("motds")) {
                            long timeMS = System.currentTimeMillis();
                            BungeeControl.reloadFile(SaveMode.MOTDS);
                            String reload = BungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MOTDS");
                            bungeeUtils.sendColored(sender, reload);
                        }
                    }
                }
            }
        } catch(Throwable throwable) {
            bungeeUtils.sendColored(sender,"&cPixelMOTD found a error using this command. Check console and report this error to the developer, please enable option &lshow-detailed-errors &cfor more info for the developer.");
            if(BungeeControl.isDetailed()) {
                BungeePixel.sendConsole("&a[Pixel MOTD] [Detailed Error] Information:");
                if(throwable.getMessage() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] Message: " + throwable.getMessage());
                }
                if(throwable.getLocalizedMessage() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + throwable.getLocalizedMessage());
                }
                if(throwable.getStackTrace() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : throwable.getStackTrace()) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if(throwable.getSuppressed() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(throwable.getSuppressed()));
                }
                if(throwable.getClass().getName() != null) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] Class: " + throwable.getClass().getName() + ".class");
                }
                BungeePixel.sendConsole("&a[Pixel MOTD] Plugin version:" + BungeePixel.getInstance().getDescription().getVersion());
                BungeePixel.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
        }
    }
    private String getMsg(String message) {
        if(message == null) return "";
        return message;
    }
}
