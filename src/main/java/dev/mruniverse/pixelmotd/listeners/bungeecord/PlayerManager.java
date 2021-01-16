package dev.mruniverse.pixelmotd.listeners.bungeecord;

import dev.mruniverse.pixelmotd.enums.MotdType;
import dev.mruniverse.pixelmotd.enums.ValueMode;
import dev.mruniverse.pixelmotd.utils.BungeeUtils;

import java.util.List;

public class PlayerManager {
    private final ValueMode valueMode;
    private final List<Integer> values;
    private final Integer ShowedType;
    public PlayerManager(MotdType motdType, String motdName,int result) {
        valueMode = BungeeUtils.getPlayersMode(motdType,motdName);
        values = BungeeUtils.getPlayersList(motdType,motdName);
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
