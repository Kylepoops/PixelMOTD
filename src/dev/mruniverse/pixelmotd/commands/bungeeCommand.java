package dev.mruniverse.pixelmotd.commands;

import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.enums.WhitelistMembers;
import dev.mruniverse.pixelmotd.files.bungeeControl;
import dev.mruniverse.pixelmotd.init.bungeePixelMOTD;
import dev.mruniverse.pixelmotd.utils.bungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class bungeeCommand extends Command {
    private final String cmd;


    public bungeeCommand(String command) {
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
    private List<String> getPlayers(WhitelistMembers mode, String areaName) {
        return new ArrayList<>();
    }

    private String getStatus(String location) {
        if(location.equalsIgnoreCase("Global")) {
            if(bungeeControl.getWhitelistStatus()) {
                return bungeeControl.getControl(Files.COMMAND).getString("command.status.on");
            }
            return bungeeControl.getControl(Files.COMMAND).getString("command.status.off");
        }
        if(bungeeControl.getControl(Files.MODULES).get("modules.world-whitelist.worlds." + location +".whitelist-status") != null) {
            if(bungeeControl.getControl(Files.MODULES).getBoolean("modules.world-whitelist.worlds." + location +".whitelist-status")) {
                return bungeeControl.getControl(Files.COMMAND).getString("command.status.on");
            }
            return bungeeControl.getControl(Files.COMMAND).getString("command.status.off");
        }
        return bungeeControl.getControl(Files.COMMAND).getString("command.status.off");
    }
    @SuppressWarnings("all")
    private String getOnline(String playerName) {
        if(bungeePixelMOTD.getInstance().getProxy().getPlayer(playerName) != null) {
            try {
                if (Objects.requireNonNull(bungeePixelMOTD.getInstance().getProxy().getPlayer(playerName)).isConnected()) {
                    return bungeeControl.getControl(Files.COMMAND).getString("command.online-status.online").replace("%server%",Objects.requireNonNull(bungeePixelMOTD.getInstance().getProxy().getPlayer(playerName).getServer().getInfo().getName()));
                }
                return bungeeControl.getControl(Files.COMMAND).getString("command.online-status.offline");
            }catch(Throwable throwable) {
                return bungeeControl.getControl(Files.COMMAND).getString("command.online-status.offline");
            }
        }

        return bungeeControl.getControl(Files.COMMAND).getString("command.online-status.offline");
    }
    private void sendMain(CommandSender sender) {
        for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.help")) {
            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
            if(lines.contains("%author%")) lines = lines.replace("%author%", bungeePixelMOTD.getInstance().getDescription().getAuthor());
            if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
            bungeeUtils.sendColored(sender,lines);
        }
    }
    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    public void execute(CommandSender sender, String[] args) {
        try {
            if (bungeeControl.isCommandEnabled()) {
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
                            for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", bungeeControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                bungeeUtils.sendColored(sender,lines);
                            }
                            for(String players : bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name")) {
                                String line= bungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                                if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                                if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                                if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                                bungeeUtils.sendColored(sender,line);
                            }
                            for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", bungeeControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                bungeeUtils.sendColored(sender,lines);
                            }
                            for(String uuids : bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid")) {
                                String line= bungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                                if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                                if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                                if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                                if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                                bungeeUtils.sendColored(sender,line);
                            }
                            for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                                if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                                if(lines.contains("%author%")) lines = lines.replace("%author%", bungeeControl.getWhitelistAuthor());
                                if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
                                if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                                if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                                if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                                if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                                bungeeUtils.sendColored(sender,lines);
                            }
                            return;
                        }
                        return;
                    }
                    if (args.length == 2) {
                        for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.top")) {
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", bungeeControl.getWhitelistAuthor());
                            if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", args[1]);
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus(args[1]));
                            if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            bungeeUtils.sendColored(sender,lines);
                        }
                        for(String players : bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name")) {
                            String line= bungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersNameFormat");
                            if(line == null) line = "&e&l* &8[&7%online_status%&8] &7%player_name%";
                            if(line.contains("%online_status%")) line = line.replace("%online_status%",getOnline(players));
                            if(line.contains("%player_name%")) line = line.replace("%player_name%",players);
                            bungeeUtils.sendColored(sender,line);
                        }
                        for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.mid")) {
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", bungeeControl.getWhitelistAuthor());
                            if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                            if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            bungeeUtils.sendColored(sender,lines);
                        }
                        for(String uuids : bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid")) {
                            String line= bungeeControl.getControl(Files.COMMAND).getString("command.whitelist.list.playersUuidFormat");
                            if(line == null) line = "&e&l* &8[&7UUID&8] &7%player_uuid%";
                            if(line.contains("%player_uuid%")) line = line.replace("%online_status%","??");
                            if(line.contains("%player_name%")) line = line.replace("%player_name%","??");
                            if(line.contains("%player_uuid%")) line = line.replace("%player_uuid%",uuids);
                            bungeeUtils.sendColored(sender,line);
                        }
                        for(String lines : bungeeControl.getControl(Files.COMMAND).getStringList("command.whitelist.list.bot")) {
                            if(lines.contains("%cmd%")) lines = lines.replace("%cmd%", cmd);
                            if(lines.contains("%author%")) lines = lines.replace("%author%", bungeeControl.getWhitelistAuthor());
                            if(lines.contains("%version%")) lines = lines.replace("%version%", bungeePixelMOTD.getInstance().getDescription().getVersion());
                            if(lines.contains("%whitelist%")) lines = lines.replace("%whitelist%", "Global");
                            if(lines.contains("%status%")) lines = lines.replace("%status%", getStatus("Global"));
                            if(lines.contains("<isUser>")) lines = lines.replace("<isUser>","");
                            if(lines.contains("%your_uuid%")) lines = lines.replace("%your_uuid%",getUniqueId(sender));
                            bungeeUtils.sendColored(sender,lines);
                        }
                        return;
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
                            if (args[2].contains("-")) {
                                if (bungeeControl.getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[2])));
                                    List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                    bungeeControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                    bungeeControl.save(SaveMode.EDITABLE);
                                    bungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "UUID").replace("%player%", args[2])));
                                List<String> list = new ArrayList<>();
                                list.add(args[2]);
                                bungeeControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                bungeeControl.save(SaveMode.EDITABLE);
                                bungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (bungeeControl.getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                if(!bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[2])) {
                                    bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[2])));
                                    List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                    list.add(args[2]);
                                    bungeeControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                    bungeeControl.save(SaveMode.EDITABLE);
                                    bungeeControl.reloadFile(SaveMode.EDITABLE);
                                }
                                bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.already-whitelisted")).replace("%type%", "Player").replace("%player%", args[2])));
                                return;
                            }
                            bungeeUtils.sendColored(sender, getMsg(Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-add")).replace("%type%", "Player").replace("%player%", args[2])));
                            List<String> list = new ArrayList<>();
                            list.add(args[2]);
                            bungeeControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                            bungeeControl.save(SaveMode.EDITABLE);
                            bungeeControl.reloadFile(SaveMode.EDITABLE);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("blacklist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return;
                            }
                            if (args[2].contains("-")) {
                                if (bungeeControl.getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                    List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                    list.add(args[2]);
                                    bungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[2]));
                                    bungeeControl.save(SaveMode.EDITABLE);
                                    bungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "UUID").replace("%player%", args[2]));
                                List<String> list = new ArrayList<>();
                                list.add(args[2]);
                                bungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid", list);
                                bungeeControl.save(SaveMode.EDITABLE);
                                bungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            if (bungeeControl.getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                list.add(args[2]);
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[2]));
                                bungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                bungeeControl.save(SaveMode.EDITABLE);
                                bungeeControl.reloadFile(SaveMode.EDITABLE);
                                return;
                            }
                            List<String> list = new ArrayList<>();
                            list.add(args[2]);
                            bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-add")).replace("%type%", "Player").replace("%player%", args[2]));
                            bungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                            bungeeControl.save(SaveMode.EDITABLE);
                            bungeeControl.reloadFile(SaveMode.EDITABLE);
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
                            if (args[2].contains("-")) {
                                if (bungeeControl.getControl(Files.EDITABLE).get("whitelist.players-uuid") != null) {
                                    if(bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid").contains(args[2])) {
                                        List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-uuid");
                                        list.remove(args[2]);
                                        bungeeControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", list);
                                        bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "UUID").replace("%player%", args[2]));
                                        bungeeControl.save(SaveMode.EDITABLE);
                                        bungeeControl.reloadFile(SaveMode.EDITABLE);
                                        return;
                                    }
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "UUID").replace("%player%", args[2]));
                                    return;
                                }
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "UUID").replace("%player%", args[2]));
                                //spigotControl.getControl(Files.EDITABLE).set("whitelist.players-uuid", new ArrayList<String>().add(args[2]));
                                return;
                            }
                            if (bungeeControl.getControl(Files.EDITABLE).get("whitelist.players-name") != null) {
                                if(bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name").contains(args[2])) {
                                    List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("whitelist.players-name");
                                    list.remove(args[2]);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.whitelist-player-remove")).replace("%type%", "Player").replace("%player%", args[2]));
                                    bungeeControl.getControl(Files.EDITABLE).set("whitelist.players-name", list);
                                    bungeeControl.save(SaveMode.EDITABLE);
                                    bungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[2]));
                                return;
                            }
                            bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-whitelisted")).replace("%type%", "Player").replace("%player%", args[2]));
                            return;
                        }
                        if (args[1].equalsIgnoreCase("blacklist")) {
                            if (args.length == 2) {
                                sendMain(sender);
                                return;
                            }
                            if (args[2].contains("-")) {
                                if (bungeeControl.getControl(Files.EDITABLE).get("blacklist.players-uuid") != null) {
                                    if(bungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid").contains(args[2])){
                                        bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[2]));
                                        List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-uuid");
                                        list.remove(args[2]);
                                        bungeeControl.getControl(Files.EDITABLE).set("blacklist.players-uuid",list);
                                        bungeeControl.save(SaveMode.EDITABLE);
                                        bungeeControl.reloadFile(SaveMode.EDITABLE);
                                    }
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "UUID").replace("%player%", args[2]));
                                    return;

                                }
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "UUID").replace("%player%", args[2]));
                                return;
                            }
                            if (bungeeControl.getControl(Files.EDITABLE).get("blacklist.players-name") != null) {
                                if(bungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name").contains(args[2])) {
                                    List<String> list = bungeeControl.getControl(Files.EDITABLE).getStringList("blacklist.players-name");
                                    list.remove(args[2]);
                                    bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.blacklist-player-remove")).replace("%type%", "Player").replace("%player%", args[2]));
                                    bungeeControl.getControl(Files.EDITABLE).set("blacklist.players-name", list);
                                    bungeeControl.save(SaveMode.EDITABLE);
                                    bungeeControl.reloadFile(SaveMode.EDITABLE);
                                    return;
                                }
                                bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "Player").replace("%player%", args[2]));
                                return;
                            }
                            bungeeUtils.sendColored(sender, Objects.requireNonNull(bungeeControl.getControl(Files.EDITABLE).getString("messages.not-blacklisted")).replace("%type%", "Player").replace("%player%", args[2]));
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
                            bungeeControl.reloadFile(SaveMode.ALL);
                            String reload = bungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "ALL");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("cmd")) {
                            long timeMS = System.currentTimeMillis();
                            bungeeControl.reloadFile(SaveMode.COMMAND);
                            String reload = bungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "COMMANDS");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("edit")) {
                            long timeMS = System.currentTimeMillis();
                            bungeeControl.reloadFile(SaveMode.EDITABLE);
                            String reload = bungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "EDIT");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("modules")) {
                            long timeMS = System.currentTimeMillis();
                            bungeeControl.reloadFile(SaveMode.MODULES);
                            String reload = bungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MODULES");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("motds")) {
                            long timeMS = System.currentTimeMillis();
                            bungeeControl.reloadFile(SaveMode.MOTDS);
                            String reload = bungeeControl.getControl(Files.EDITABLE).getString("messages.reload");
                            if (reload == null) reload = "";
                            if (reload.contains("<ms>"))
                                reload = reload.replace("<ms>", (System.currentTimeMillis() - timeMS) + "");
                            if (reload.contains("<saveMode>")) reload = reload.replace("<saveMode>", "MOTDS");
                            bungeeUtils.sendColored(sender, reload);
                            return;
                        }
                    }
                }
                return;
            }
            return;
        } catch(Throwable throwable) {
            bungeeUtils.sendColored(sender,"&cPixelMOTD found a error using this command. Check console and report this error to the developer, please enable option &lshow-detailed-errors &cfor more info for the developer.");
            if(bungeeControl.isDetailed()) {
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] [Detailed Error] Information:");
                if(throwable.getMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Message: " + throwable.getMessage());
                }
                if(throwable.getLocalizedMessage() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + throwable.getLocalizedMessage());
                }
                if(throwable.getStackTrace() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] StackTrace: ");
                    for(StackTraceElement line : throwable.getStackTrace()) {
                        bungeePixelMOTD.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                    }
                }
                if(throwable.getSuppressed() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(throwable.getSuppressed()));
                }
                if(throwable.getClass().getName() != null) {
                    bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Class: " + throwable.getClass().getName() + ".class");
                }
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] Plugin version:" + bungeePixelMOTD.getInstance().getDescription().getVersion());
                bungeePixelMOTD.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
            }
        }
        return;
    }
    private String getMsg(String message) {
        if(message == null) return "";
        return message;
    }
}
