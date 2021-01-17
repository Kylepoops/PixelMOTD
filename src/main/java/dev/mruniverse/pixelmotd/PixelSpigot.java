package dev.mruniverse.pixelmotd;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.files.FileManager;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.utils.LoaderUtils;
import dev.mruniverse.pixelmotd.listeners.SpigotMotd;
import dev.mruniverse.pixelmotd.utils.HexManager;
import dev.mruniverse.pixelmotd.utils.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.mruniverse.pixelmotd.utils.spigotLogger.error;
import static dev.mruniverse.pixelmotd.utils.spigotLogger.info;

public class PixelSpigot extends JavaPlugin implements Listener {
    private static PixelSpigot instance;

    private static HexManager hManager;
    private static FileManager fManager;

    private SpigotControl spigotControl;
    private SpigotUtils spigotUtils;

    private LoaderUtils loaderUtils;

    @Override
    public void onLoad() {
        instance = this;

        long temporalTimer = System.currentTimeMillis();

        loaderUtils = new LoaderUtils(false);

        fManager = new FileManager(this);
        fManager.loadFiles();
        fManager.loadConfiguration();

        spigotControl = new SpigotControl(this);
        spigotUtils = new SpigotUtils(this);

        SpigotControl.save(SaveMode.ALL);

        hManager = new HexManager();
        hManager.setHex(SpigotControl.getControl(Files.SETTINGS).getBoolean("settings.hexColors"));

        loaderUtils.pluginUpdater();

        info("All files loaded in &b" + (System.currentTimeMillis() - temporalTimer) + "&fms.");
    }

    @Override
    public void onEnable() {
        long temporalTimer = System.currentTimeMillis();

        loadHooks();

        loaderUtils.loadMetrics();
        loaderUtils.registerListeners();
        loaderUtils.registerCommands();

        info("All events loaded in &b" + (System.currentTimeMillis() - temporalTimer) + "&fms.");
    }

    @Override
    public void onDisable() {
        info("The plugin was disabled.");
    }

    public void loadHooks() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            error("This plugin needs ProtocolLib to work properly.");
            return;
        }

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener((new SpigotMotd()).getPacketAdapter());
        info("Hooked with ProtocolLib!");
    }
    public SpigotControl getSpigotControl() {
        return spigotControl;
    }
    public SpigotUtils getSpigotUtils() {
        return spigotUtils;
    }
    public static FileManager getFiles() {
        return fManager;
    }

    public static PixelSpigot getInstance() {
        return instance;
    }

    public static HexManager getHex() {
        return hManager;
    }
}
