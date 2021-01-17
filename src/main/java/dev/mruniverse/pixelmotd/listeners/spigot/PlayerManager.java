package dev.mruniverse.pixelmotd.listeners.spigot;

import dev.mruniverse.pixelmotd.PixelSpigot;
import dev.mruniverse.pixelmotd.enums.MotdType;
import dev.mruniverse.pixelmotd.enums.ValueMode;
import dev.mruniverse.pixelmotd.utils.SpigotUtils;

import java.util.List;

public class PlayerManager {
    private final ValueMode valueMode;
    private final List<Integer> values;
    private final Integer ShowedType;
    public PlayerManager(MotdType motdType, String motdName, int result) {
        valueMode = PixelSpigot.getInstance().getSpigotUtils().getPlayersMode(motdType,motdName);
        values = PixelSpigot.getInstance().getSpigotUtils().getPlayersList(motdType,motdName);
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