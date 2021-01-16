package dev.mruniverse.pixelmotd.init;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.InitMode;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.files.FileManager;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.listeners.SpigotMotd;
import dev.mruniverse.pixelmotd.utils.HexManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.mruniverse.pixelmotd.utils.Logger.error;
import static dev.mruniverse.pixelmotd.utils.Logger.info;

public class SpigotPixel extends JavaPlugin implements Listener {
    private static SpigotPixel instance;
    private static HexManager hManager;
    private static FileManager fManager;

    private LoaderUtils loaderUtils;

    @Override
    public void onLoad() {
        instance = this;

        long temporalTimer = System.currentTimeMillis();

        fManager = new FileManager(InitMode.SPIGOT_VERSION);
        fManager.loadFiles();
        fManager.loadConfiguration();

        SpigotControl.save(SaveMode.ALL);

        hManager = new HexManager();
        hManager.setHex(SpigotControl.getControl(Files.SETTINGS).getBoolean("settings.hexColors"));

        loaderUtils = new LoaderUtils(false);
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
        info("The plugin was unloaded.");
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

    public static FileManager getFiles() {
        return fManager;
    }

    public static SpigotPixel getInstance() {
        return instance;
    }

    public static HexManager getHex() {
        return hManager;
    }
}
