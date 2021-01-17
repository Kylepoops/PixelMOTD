package dev.mruniverse.pixelmotd;

import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.files.FileManager;
import dev.mruniverse.pixelmotd.utils.BungeeUtils;
import dev.mruniverse.pixelmotd.utils.HexManager;
import dev.mruniverse.pixelmotd.utils.LoaderUtils;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import static dev.mruniverse.pixelmotd.utils.bungeeLogger.info;

public class PixelBungee extends Plugin implements Listener {
    private static PixelBungee instance;

    private FileManager fManager;
    private HexManager hManager;

    private BungeeControl bungeeControl;
    private BungeeUtils bungeeUtils;

    private LoaderUtils loaderUtils;
    @Override
    public void onLoad() {
        instance = this;

        // Set value to isBungee boolean.
        loaderUtils = new LoaderUtils(true);

        bungeeControl = new BungeeControl();
        bungeeControl.save(SaveMode.ALL);

        bungeeUtils = new BungeeUtils(this);

        hManager.setHex(bungeeControl.getControl(Files.SETTINGS).getBoolean("settings.hexColors"));

        fManager = new FileManager(this);
        fManager.loadFiles();

        hManager = new HexManager();
        fManager.loadConfiguration();

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

    public FileManager getFiles() {
        return fManager;
    }

    public static PixelBungee getInstance() {
        return instance;
    }
    public LoaderUtils getLoader() { return loaderUtils; }
    public HexManager getHex() {
        return hManager;
    }

    public BungeeControl getBungeeControl() {
        return bungeeControl;
    }

    public BungeeUtils getBungeeUtils() {
        return bungeeUtils;
    }
}

