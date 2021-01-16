package dev.mruniverse.pixelmotd.listeners.bungeecord;

import dev.mruniverse.pixelmotd.PixelBungee;
import dev.mruniverse.pixelmotd.enums.MotdType;
import dev.mruniverse.pixelmotd.enums.ValueMode;
import dev.mruniverse.pixelmotd.utils.BungeeUtils;

import java.util.List;

public class PlayerManager {
    private final ValueMode valueMode;
    private final List<Integer> values;
    private final Integer ShowedType;

    public PlayerManager(PixelBungee plugin, MotdType motdType, String motdName,int result) {
        BungeeUtils bUtils = plugin.getBungeeUtils();

        valueMode = bUtils.getPlayersMode(motdType,motdName);
        values = bUtils.getPlayersList(motdType,motdName);
        ShowedType = result;
    }

    public ValueMode getMode() {
        return valueMode;
    }

    public List<Integer> getValues() {
        return values;
    }

    public Integer getResult() {
        return ShowedType;
    }
}
