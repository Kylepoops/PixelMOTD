package dev.mruniverse.pixelmotd.utils;

import dev.mruniverse.pixelmotd.enums.Whitelist;

public class Extras {
    public static String getWorldPath(Whitelist path, String worldName) {
        if(path.equals(Whitelist.PLAYERS_NAME)) {
            return "modules.world-whitelist.worlds." + worldName + " .players-name";
        }
        if(path.equals(Whitelist.PLAYERS_UUID)) {
            return "modules.world-whitelist.worlds." + worldName + " .players-uuid";
        }
        if(path.equals(Whitelist.STATUS)) {
            return "modules.world-whitelist.worlds." + worldName + " .whitelist-status";
        }
        if(path.equals(Whitelist.REASON)) {
            return "modules.world-whitelist.worlds." + worldName + " .whitelist-reason";
        }
        return "modules.world-whitelist.worlds." + worldName + " .whitelist-author";
    }
}
