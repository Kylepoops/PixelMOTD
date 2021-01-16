package dev.mruniverse.pixelmotd.init;

import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.InitMode;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.files.FileManager;
import dev.mruniverse.pixelmotd.utils.HexManager;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import static dev.mruniverse.pixelmotd.utils.Logger.info;

public class BungeePixel extends Plugin implements Listener {
    private static BungeePixel instance;
    private static FileManager fManager;
    private static HexManager hManager;

    private LoaderUtils loaderUtils;

    @Override
    public void onLoad() {
        instance = this;

        fManager = new FileManager(InitMode.BUNGEE_VERSION);
        fManager.loadFiles();

        hManager = new HexManager();
        fManager.loadConfiguration();

        BungeeControl.save(SaveMode.ALL);

        hManager.setHex(BungeeControl.getControl(Files.SETTINGS).getBoolean("settings.hexColors"));

        loaderUtils = new LoaderUtils(true);
        loaderUtils.pluginUpdater();
        loaderUtils.registerCommands();

        info("All files loaded");
    }

    @Override
    public void onEnable() {
        long temporalTimer = System.currentTimeMillis();

        loaderUtils.loadMetrics();
        loaderUtils.registerListeners();

        info("All events loaded in &b" + (System.currentTimeMillis() - temporalTimer) + "&fms.");
    }

    @Override
    public void onDisable() {
        info("The plugin was unloaded.");
    }

    public static FileManager getFiles() {
        return fManager;
    }

    public static BungeePixel getInstance() {
        return instance;
    }

    public static HexManager getHex() {
        return hManager;
    }
}

