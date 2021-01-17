package dev.mruniverse.pixelmotd.commands;

import dev.mruniverse.pixelmotd.enums.*;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.PixelSpigot;
import dev.mruniverse.pixelmotd.utils.Extras;
import dev.mruniverse.pixelmotd.utils.SpigotUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.mruniverse.pixelmotd.utils.Logger.error;
import static dev.mruniverse.pixelmotd.utils.Logger.sendMessage;

public class SpigotCMD implements CommandExecutor {

    private final String cmd;

    public SpigotCMD(PixelSpigot plugin, String command) {
        this.cmd = command;

        Objects.requireNonNull(plugin.getCommand(command)).setExecutor(this);
    }

    private String getUniqueId(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            return player.getUniqueId().toString();
        }
        return "??";
    }
    private boolean hasPermission(CommandSender sender, String permission) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.hasPermission(permission)) {
                SpigotUtils.sendColored(player, SpigotUtils.getPermissionMessage(permission));
            }
            return player.hasPermission(permission);
        }
        return true;
    }
    private String getStatus(String location) {
        if(location.equalsIgnoreCase("Global")) {
            if(SpigotControl.getWhitelistStatus()) {
                return SpigotControl.getControl(Files.COMMAND).getString("command.status.on");
            }
            return SpigotControl.getControl(Files.COMMAND).getString("command.status.off");
        }
        if(SpigotControl.getControl(Files.MODULES).get("modules.world-whitelist.worlds." + location +".whitelist-status") != null) {
            if(SpigotControl.getControl(Files.MODULES).getBoolean("modules.world-whitelist.worlds." + location +".whitelist-status")) {
                return SpigotControl.getControl(Files.COMMAND).getString("command.status.on");
            }
            return SpigotControl.getControl(Files.COMMAND).getString("command.status.off");
        }
        return SpigotControl.getControl(Files.COMMAND).getString("command.status.off");
    }
    private String getAuthor(String location) {
        if(SpigotControl.getControl(Files.MODULES).get("modules.world-whitelist.worlds." + location +".whitelist-author") != null) {
            return SpigotControl.getControl(Files.COMMAND).getString("modules.world-whitelist.worlds." + location +".whitelist-author");
        }
        return "Console";
    }
    @SuppressWarnings("all")
    private String getOnline(String playerName) {
        if(PixelSpigot.getInstance().getServer().getPlayer(playerName) != null) {
            try {
                if (Objects.requireNonNull(PixelSpigot.getInstance().getServer().getPlayer(playerName)).isOnline()) {
                    return SpigotControl.getControl(Files.COMMAND).getString("command.online-status.online").replace("%server%",Objects.requireNonNull(PixelSpigot.getInstance().getServer().getPlayer(playerName).getWorld().getName()));
                }
                return SpigotControl.getControl(Files.COMMAND).getString("command.online-status.offline");
            }catch(Throwable throwable) {
                return SpigotControl.getControl(Files.COMMAND).getString("command.online-status.offline");
            }
        }

        return SpigotControl.getControl(Files.COMMAND).getString("command.online-status.offline");
    }
    private void sendMain(CommandSender sender) {
        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.help")) {
            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
            if(lines.contains("%author%")) lines = lines.replace("%author%", PixelSpigot.getInstance().getDescription().getAuthors().toString());
            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
            SpigotUtils.sendColored(sender,lines);
        }
    }
    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (SpigotControl.isCommandEnabled()) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                    if (hasPermission(sender, "pixelmotd.command.help")) {
                        sendMain(sender);
                    }
                    return true;
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
                            return true;
                        }
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("global")) {
                        if (args.length == 2) {
                            for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", SpigotControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                SpigotUtils.sendColored(sender,lines);
                            }
                            for(String players : SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name")) {
                                String line= SpigotControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                SpigotUtils.sendColored(sender,line);
                            }
                            for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", SpigotControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                SpigotUtils.sendColored(sender,lines);
                            }
                            for(String uuids : SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid")) {
                                String line= SpigotControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                SpigotUtils.sendColored(sender,line);
                            }
                            for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", SpigotControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                SpigotUtils.sendColored(sender,lines);
                            }
                            return true;
                        }
                        if(args[2].equalsIgnoreCase("on")) {
                            SpigotControl.getControl(Files.EDITABLE).set("whitelist.toggle",true);
                            SpigotControl.getControl(Files.EDITABLE).set("whitelist.author","Console");
                            SpigotControl.save(SaveMode.EDITABLE);
                            SpigotControl.reloadFile(SaveMode.EDITABLE);
                            SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-enabled"));
                            return true;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            SpigotControl.getControl(Files.EDITABLE).set("whitelist.toggle",false);
                            SpigotControl.getControl(Files.EDITABLE).set("whitelist.author","Console");
                            SpigotControl.save(SaveMode.EDITABLE);
                            SpigotControl.reloadFile(SaveMode.EDITABLE);
                            SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-disabled"));
                            return true;
                        }
                        return true;
                    }
                    if (args.length == 2) {
                        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            SpigotUtils.sendColored(sender,lines);
                        }
                        for(String players : SpigotUtils.getPlayers(WhitelistMembers.NAMEs,args[1])) {
                            String line= SpigotControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            SpigotUtils.sendColored(sender,line);
                        }
                        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            SpigotUtils.sendColored(sender,lines);
                        }
                        for(String uuids : SpigotUtils.getPlayers(WhitelistMembers.UUIDs,args[1])) {
                            String line= SpigotControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            SpigotUtils.sendColored(sender,line);
                        }
                        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", getAuthor(args[1]));
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            SpigotUtils.sendColored(sender,lines);
                        }
                        return true;
                    }
                    if(args[2].equalsIgnoreCase("on")) {
                        SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Whitelist.STATUS,args[1]),true);
                        if(sender instanceof Player) {
                            SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Whitelist.AUTHOR, args[1]), sender.getName());
                        } else {
                            SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Whitelist.AUTHOR, args[1]), "Console");
                        }
                        SpigotControl.save(SaveMode.MODULES);
                        SpigotControl.reloadFile(SaveMode.MODULES);
                        SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-enabled"));
                        return true;
                    }
                    if(args[2].equalsIgnoreCase("off")) {
                        SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Whitelist.STATUS,args[1]),false);
                        if(sender instanceof Player) {
                            SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Whitelist.AUTHOR, args[1]), sender.getName());
                        } else {
                            SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Whitelist.AUTHOR, args[1]), "Console");
                        }
                        SpigotControl.save(SaveMode.MODULES);
                        SpigotControl.reloadFile(SaveMode.MODULES);
                        SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-disabled"));
                        return true;
                        //getServerPath
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("blacklist")) {
                    if (args.length == 1) {
                        if (hasPermission(sender, "pixelmotd.command.blacklist.toggle")) {
                            sendMain(sender);
                            return true;
                        }
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("global")) {
                        if (args.length == 2) {
                            boolean userMessage;
                            for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.top")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof Player) {
                                        SpigotUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    SpigotUtils.sendColored(sender, lines);
                                }
                            }
                            for(String players : SpigotControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name")) {
                                String line= SpigotControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                SpigotUtils.sendColored(sender,line);
                            }
                            for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.mid")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof Player) {
                                        SpigotUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    SpigotUtils.sendColored(sender, lines);
                                }
                            }
                            for(String uuids : SpigotControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid")) {
                                String line= SpigotControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                SpigotUtils.sendColored(sender,line);
                            }
                            for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.bot")) {
                                userMessage = false;
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                                if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                                if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) {
                                    lines = lines.replace("<isUser>","");
                                    userMessage = true;
                                }
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                if(userMessage) {
                                    if(sender instanceof Player) {
                                        SpigotUtils.sendColored(sender, lines);
                                    }
                                } else {
                                    SpigotUtils.sendColored(sender, lines);
                                }
                            }
                            return true;
                        }
                        if(args[2].equalsIgnoreCase("on")) {
                            SpigotControl.getControl(Files.EDITABLE).set("blacklist.toggle",true);
                            SpigotControl.getControl(Files.EDITABLE).set("blacklist.author","Console");
                            SpigotControl.save(SaveMode.EDITABLE);
                            SpigotControl.reloadFile(SaveMode.EDITABLE);
                            SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-enabled"));
                            return true;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            SpigotControl.getControl(Files.EDITABLE).set("blacklist.toggle",false);
                            SpigotControl.getControl(Files.EDITABLE).set("blacklist.author","Console");
                            SpigotControl.save(SaveMode.EDITABLE);
                            SpigotControl.reloadFile(SaveMode.EDITABLE);
                            SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-disabled"));
                            return true;
                        }
                    }
                    if (args.length == 2) {
                        boolean userMessage;
                        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.top")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof Player) {
                                    SpigotUtils.sendColored(sender, lines);
                                }
                            } else {
                                SpigotUtils.sendColored(sender, lines);
                            }
                        }
                        for(String players : SpigotUtils.getPlayers(BlacklistMembers.NAMEs,args[1])) {
                            String line= SpigotControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            SpigotUtils.sendColored(sender,line);
                        }
                        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.mid")) {
                            userMessage=false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage=true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof Player) {
                                    SpigotUtils.sendColored(sender, lines);
                                }
                            } else {
                                SpigotUtils.sendColored(sender, lines);
                            }
                        }
                        for(String uuids : SpigotUtils.getPlayers(BlacklistMembers.UUIDs,args[1])) {
                            String line= SpigotControl.getControl(Files.COMMAND).getString("command.blacklist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            SpigotUtils.sendColored(sender,line);
                        }
                        for(String lines : SpigotControl.getControl(Files.COMMAND).getStringList("command.blacklist.list.bot")) {
                            userMessage = false;
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", "??");
                            if(lines.contains("%version%")) lines = lines.replace("%version%", PixelSpigot.getInstance().getDescription().getVersion());
                            if(lines.contains("%blacklist%")) lines = lines.replace("%blacklist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) {
                                lines = lines.replace("<isUser>","");
                                userMessage = true;
                            }
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            if(userMessage) {
                                if(sender instanceof Player) {
                                    SpigotUtils.sendColored(sender, lines);
                                }
                            } else {
                                SpigotUtils.sendColored(sender, lines);
                            }
                        }
                        return true;
                    }
                    if(args.length == 3) {
                        if(args[2].equalsIgnoreCase("on")) {
                            SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Blacklist.STATUS,args[1]),true);
                            SpigotControl.save(SaveMode.MODULES);
                            SpigotControl.reloadFile(SaveMode.MODULES);
                            SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.status-enabled").replace("%type%","world").replace("%value%",args[1]));
                            return true;
                        }
                        if(args[2].equalsIgnoreCase("off")) {
                            SpigotControl.getControl(Files.MODULES).set(Extras.getWorldPath(Blacklist.STATUS,args[1]),false);
                            SpigotControl.save(SaveMode.MODULES);
                            SpigotControl.reloadFile(SaveMode.MODULES);
                            SpigotUtils.sendColored(sender, SpigotControl.getControl(Files.EDITABLE).getString("messages.status-disabled").replace("%type%","world").replace("%value%",args[1]));
                            return true;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (hasPermission(sender, "pixelmotd.command.whitelist.add")) {
                        if (args.length == 1) {
                            sendMain(sender);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("whitelist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return true;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return true;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (SpigotControl.getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                        SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                        List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.add(args[3]);
                                        SpigotControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        SpigotControl.save(SaveMode.EDITABLE);
                                        SpigotControl.reloadFile(SaveMode.EDITABLE);
                                        return true;
                                    }
                                    SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                if (SpigotControl.getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                    if (!SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[3])) {
                                        SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                        List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                        list.add(args[3]);
                                        SpigotControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                        SpigotControl.save(SaveMode.EDITABLE);
                                        SpigotControl.reloadFile(SaveMode.EDITABLE);
                                        return true;
                                    }
                                    SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                    return true;
                                }
                                SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = new ArrayList<>();
                                list.add(args[2]);
                                SpigotControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            if (args[3].contains("-")) {
                                if (!SpigotUtils.getPlayers(WhitelistMembers.UUIDs,args[2]).contains(args[3])) {
                                    SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = SpigotUtils.getPlayers(WhitelistMembers.UUIDs, args[2]);
                                    list.add(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Whitelist.PLAYERS_UUID, args[2]), list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                            }
                            if (!SpigotUtils.getPlayers(WhitelistMembers.NAMEs,args[2]).contains(args[3])) {
                                SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = SpigotUtils.getPlayers(WhitelistMembers.NAMEs,args[2]);
                                list.add(args[3]);
                                SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Whitelist.PLAYERS_NAME,args[2]), list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("blacklist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return true;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return true;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (SpigotControl.getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                        List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.add(args[3]);
                                        SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                        SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                        SpigotControl.save(SaveMode.EDITABLE);
                                        SpigotControl.reloadFile(SaveMode.EDITABLE);
                                        return true;
                                    }
                                    SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                if (SpigotControl.getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                    List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.add(args[3]);
                                    SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                    SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                List<String> list = new ArrayList<>();
                                list.add(args[3]);
                                SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            if (args[3].contains("-")) {
                                if (!SpigotUtils.getPlayers(BlacklistMembers.UUIDs,args[2]).contains(args[3])) {
                                    List<String> list = SpigotUtils.getPlayers(BlacklistMembers.UUIDs, args[2]);
                                    list.add(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Blacklist.PLAYERS_UUID, args[2]), list);
                                    SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.already-blacklisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                return true;
                            }
                            if (!SpigotUtils.getPlayers(BlacklistMembers.NAMEs,args[2]).contains(args[3])) {
                                List<String> list = SpigotUtils.getPlayers(BlacklistMembers.NAMEs,args[2]);
                                list.add(args[3]);
                                SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[3]));
                                SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Blacklist.PLAYERS_NAME,args[2]), list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.already-blacklisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return true;
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (hasPermission(sender, "pixelmotd.command.whitelist.remove")) {
                        if (args.length == 1) {
                            sendMain(sender);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("whitelist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return true;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return true;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (SpigotControl.getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                        SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3])));
                                        List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.remove(args[3]);
                                        SpigotControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        SpigotControl.save(SaveMode.EDITABLE);
                                        SpigotControl.reloadFile(SaveMode.EDITABLE);
                                        return true;
                                    }
                                    SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                }
                                if (SpigotControl.getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                    if (SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[3])) {
                                        SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[3])));
                                        List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                        list.remove(args[3]);
                                        SpigotControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                        SpigotControl.save(SaveMode.EDITABLE);
                                        SpigotControl.reloadFile(SaveMode.EDITABLE);
                                        return true;
                                    }
                                    SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                    return true;
                                }
                                SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                                return true;
                            }
                            if (args[3].contains("-")) {
                                if (SpigotUtils.getPlayers(WhitelistMembers.UUIDs,args[2]).contains(args[3])) {
                                    SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3])));
                                    List<String> list = SpigotUtils.getPlayers(WhitelistMembers.UUIDs, args[2]);
                                    list.remove(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Whitelist.PLAYERS_UUID, args[2]), list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                            }
                            if (SpigotUtils.getPlayers(WhitelistMembers.NAMEs,args[2]).contains(args[3])) {
                                SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[3])));
                                List<String> list = SpigotUtils.getPlayers(WhitelistMembers.NAMEs,args[2]);
                                list.remove(args[3]);
                                SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Whitelist.PLAYERS_NAME,args[2]), list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("blacklist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return true;
                            }
                            if (args.length == 3) {
                                sendMain(sender);
                                return true;
                            }
                            if(args[2].equalsIgnoreCase("Global")) {
                                if (args[3].contains("-")) {
                                    if (SpigotControl.getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                        List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.add(args[3]);
                                        SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                        SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                        SpigotControl.save(SaveMode.EDITABLE);
                                        SpigotControl.reloadFile(SaveMode.EDITABLE);
                                        return true;
                                    }
                                    SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    List<String> list = new ArrayList<>();
                                    list.add(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                if (SpigotControl.getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                    List<String> list = SpigotControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.add(args[3]);
                                    SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                    SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                List<String> list = new ArrayList<>();
                                list.add(args[3]);
                                SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                SpigotControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            if (args[3].contains("-")) {
                                if (SpigotUtils.getPlayers(BlacklistMembers.UUIDs,args[2]).contains(args[3])) {
                                    List<String> list = SpigotUtils.getPlayers(BlacklistMembers.UUIDs, args[2]);
                                    list.remove(args[3]);
                                    SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Blacklist.PLAYERS_UUID, args[2]), list);
                                    SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[3]));
                                    SpigotControl.save(SaveMode.EDITABLE);
                                    SpigotControl.reloadFile(SaveMode.EDITABLE);
                                    return true;
                                }
                                SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "UUID").replace("%player%", args[3])));
                                return true;
                            }
                            if (SpigotUtils.getPlayers(BlacklistMembers.NAMEs,args[2]).contains(args[3])) {
                                List<String> list = SpigotUtils.getPlayers(BlacklistMembers.NAMEs,args[2]);
                                list.remove(args[3]);
                                SpigotUtils.sendColored(sender, Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[3]));
                                SpigotControl.getControl(Files.EDITABLE).set(Extras.getWorldPath(Blacklist.PLAYERS_NAME,args[2]), list);
                                SpigotControl.save(SaveMode.EDITABLE);
                                SpigotControl.reloadFile(SaveMode.EDITABLE);
                                return true;
                            }
                            SpigotUtils.sendColored(sender, getMsg(Objects.requireNonNull(SpigotControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "Player").replace("%player%", args[3])));
                            return true;
                        }
                    }
                }
                if(args[0].equalsIgnoreCase("modules")) {
                    SpigotUtils.sendColored(sender,"&cCurrently working");
                    //modules setup
                }
                if(args[0].equalsIgnoreCase("externalModules")) {
                    SpigotUtils.sendColored(sender,"&cCurrently working");
                    //modules setup
                }
                //(all-settings-edit-modules-cmd-motds)
                if (args[0].equalsIgnoreCase("reload")) {
                    if (hasPermission(sender, "pixelmotd.command.reload")) {
                        if (args.length == 1) {
                            sendMain(sender);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("all")) {
                            long timeMS = System.currentTimeMillis();
                            SpigotControl.reloadFile(SaveMode.ALL);
                            String reload = SpigotControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "ALL");
                            SpigotUtils.sendColored(sender, reload);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("cmd")) {
                            long timeMS = System.currentTimeMillis();
                            SpigotControl.reloadFile(SaveMode.COMMAND);
                            String reload = SpigotControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "COMMANDS");
                            SpigotUtils.sendColored(sender, reload);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("edit")) {
                            long timeMS = System.currentTimeMillis();
                            SpigotControl.reloadFile(SaveMode.EDITABLE);
                            String reload = SpigotControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "EDIT");
                            SpigotUtils.sendColored(sender, reload);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("modules")) {
                            long timeMS = System.currentTimeMillis();
                            SpigotControl.reloadFile(SaveMode.MODULES);
                            String reload = SpigotControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MODULES");
                            SpigotUtils.sendColored(sender, reload);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("motds")) {
                            long timeMS = System.currentTimeMillis();
                            SpigotControl.reloadFile(SaveMode.MOTDS);
                            String reload = SpigotControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MOTDS");
                            SpigotUtils.sendColored(sender, reload);
                            return true;
                        }
                    }
                }
                return true;
            }
            return true;
        } catch (Throwable throwable) {
            sendMessage(sender, "&cPixelMOTD found a error using this command. Check console and report this error to the developer, please enable option &lshow-detailed-errors &cfor more info for the developer.");
            if (SpigotControl.isDetailed()) {
                error("Information:");

                if (throwable.getMessage() != null) {
                    error("Message: " + throwable.getMessage());
                }
                if (throwable.getLocalizedMessage() != null) {
                    error("LocalizedMessage: " + throwable.getLocalizedMessage());
                }
                if (throwable.getStackTrace() != null) {
                    error("StackTrace: ");

                    for (StackTraceElement line : throwable.getStackTrace()) {
                        error("(" + line.getLineNumber() + ") " + line.toString());
                    }

                }
                if (throwable.getSuppressed() != null) {
                    error("Suppressed: " + Arrays.toString(throwable.getSuppressed()));
                }

                if (throwable.getClass().getName() != null) {
                    error("Class: " + throwable.getClass().getName() + ".class");
                }

                error("Plugin version:" + PixelSpigot.getInstance().getDescription().getVersion());
                error("---------------");
            }
        }
        return true;
    }
    private String getMsg(String message) {
        if(message == null) return "";
        return message;
    }
}