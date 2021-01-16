package dev.mruniverse.pixelmotd.init;

import dev.mruniverse.pixelmotd.bstats.BungeeMetrics;
import dev.mruniverse.pixelmotd.commands.BungeeCMD;
import dev.mruniverse.pixelmotd.enums.Files;
import dev.mruniverse.pixelmotd.enums.InitMode;
import dev.mruniverse.pixelmotd.enums.SaveMode;
import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.files.FileManager;
import dev.mruniverse.pixelmotd.listeners.BungeeEvents;
import dev.mruniverse.pixelmotd.listeners.BungeeMotd;
import dev.mruniverse.pixelmotd.utils.HexManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

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

        for(String command : BungeeControl.getControl(Files.COMMAND).getStringList("command.list")) {
            getProxy().getPluginManager().registerCommand(this,new BungeeCMD(command));
        }

        loaderUtils = new LoaderUtils(true);
        loaderUtils.pluginUpdater();

        sendConsole("All files loaded");
    }
    @Override
    public void onDisable() {
        sendConsole("The plugin was unloaded.");
    }
    @Override
    public void onEnable() {
        long temporalTimer = System.currentTimeMillis();

        loaderUtils.loadMetrics();

        getProxy().getPluginManager().registerListener(this, new BungeeEvents());
        getProxy().getPluginManager().registerListener(this, new BungeeMotd());
        sendConsole("All events loaded in &b" + (System.currentTimeMillis() - temporalTimer) + "&fms.");
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

    public static void redIssue() {
        instance.getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',"&b[Pixel MOTD] &fCan't connect to SpigotMC and bStats, please check host internet or disable plugin autoUpdater and bStats to hide this message.")));
    }

    public static void sendConsole(String message) {
        instance.getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',"&b[Pixel MOTD] &f" + message)));
    }
}

