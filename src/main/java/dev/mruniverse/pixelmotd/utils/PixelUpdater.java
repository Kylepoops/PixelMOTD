package dev.mruniverse.pixelmotd.utils;

import dev.mruniverse.pixelmotd.files.BungeeControl;
import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.init.BungeePixel;
import dev.mruniverse.pixelmotd.init.SpigotPixel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;


public class PixelUpdater {
    private final String currentVersion;
    private String newestVersion;
    public PixelUpdater(boolean isBungee,int projectID) {
        if(isBungee) {
            currentVersion = BungeePixel.getInstance().getDescription().getVersion();
        } else {
            currentVersion = SpigotPixel.getInstance().getDescription().getVersion();
        }
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
            HttpsURLConnection connection;
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            newestVersion = sb.toString();
        } catch (IOException ignored) {
            if(isBungee) {
                BungeePixel.redIssue();
                if(BungeeControl.isDetailed()) {
                    BungeePixel.sendConsole("&a[Pixel MOTD] [Detailed Error] Information:");
                    if(ignored.getMessage() != null) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] Message: " + ignored.getMessage());
                    }
                    if(ignored.getLocalizedMessage() != null) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + ignored.getLocalizedMessage());
                    }
                    if(ignored.getStackTrace() != null) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] StackTrace: ");
                        for(StackTraceElement line : ignored.getStackTrace()) {
                            BungeePixel.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                        }
                    }
                    if(ignored.getSuppressed() != null) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(ignored.getSuppressed()));
                    }
                    if(ignored.getClass() != null) {
                        BungeePixel.sendConsole("&a[Pixel MOTD] Class: " + ignored.getClass().getName());
                    }
                    BungeePixel.sendConsole("&a[Pixel MOTD] Plugin version:" + BungeePixel.getInstance().getDescription().getVersion());
                    BungeePixel.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
                }
            } else {
                SpigotPixel.redIssue();
                if(SpigotControl.isDetailed()) {
                    SpigotPixel.sendConsole("&a[Pixel MOTD] [Detailed Error] Information:");
                    if(ignored.getMessage() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] Message: " + ignored.getMessage());
                    }
                    if(ignored.getLocalizedMessage() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] LocalizedMessage: " + ignored.getLocalizedMessage());
                    }
                    if(ignored.getStackTrace() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] StackTrace: ");
                        for(StackTraceElement line : ignored.getStackTrace()) {
                            SpigotPixel.sendConsole("&a[Pixel MOTD] (" + line.getLineNumber() + ") " + line.toString());
                        }
                    }
                    if(ignored.getSuppressed() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] Suppressed: " + Arrays.toString(ignored.getSuppressed()));
                    }
                    if(ignored.getClass() != null) {
                        SpigotPixel.sendConsole("&a[Pixel MOTD] Class: " + ignored.getClass().getName());
                    }
                    SpigotPixel.sendConsole("&a[Pixel MOTD] Plugin version:" + SpigotPixel.getInstance().getDescription().getVersion());
                    SpigotPixel.sendConsole("&a[Pixel MOTD] --------------- [Detailed Error]");
                }
            }
        }
    }
    public String getVersionResult() {
        String update;
        String[] installed;
        if(currentVersion == null) {
            return "RED_PROBLEM";
        }
        update = currentVersion;
        if(currentVersion.contains(".")) update= currentVersion.replace(".","");
        installed= update.split("-");
        if(installed[1] != null) {
            if(installed[1].toLowerCase().contains("pre")) {
                if(installed[1].toLowerCase().contains("alpha")) {
                    return "PRE_ALPHA_VERSION";
                }
                return "PRE_RELEASE";
            }
            if(installed[1].toLowerCase().contains("alpha")) {
                return "ALPHA_VERSION";
            }
            if(installed[1].toLowerCase().contains("release")) {
                return "RELEASE";
            }
        }
        return "RELEASE";
    }
    public String getUpdateResult() {
        int using,latest;
        String update;
        String[] installed, spigot;
        //Version Verificator

        if(currentVersion == null || newestVersion == null) {
            return "RED_PROBLEM";
        }

        //Version Setup

        //* First Setup

        update= currentVersion;
        if(currentVersion.contains(".")) update= currentVersion.replace(".","");
        installed= update.split("-");
        update= newestVersion;
        if(newestVersion.contains(".")) update= newestVersion.replace(".","");
        spigot= update.split("-");

        //* Second Setup

        using= Integer.parseInt(installed[0]);
        latest= Integer.parseInt(spigot[0]);

        //Result Setup
        if(using == latest) {
            if(installed[1].equalsIgnoreCase(spigot[1])) {
                return "UPDATED";
            }
            return "NEW_VERSION";
        }
        if(using < latest) {
            return "NEW_VERSION";
        }
        if(using > latest) {
            if(installed[1].toLowerCase().contains("pre")) {
                if(installed[1].toLowerCase().contains("alpha")) {
                    return "PRE_ALPHA_VERSION";
                }
                return "BETA_VERSION";
            }
            if(installed[1].toLowerCase().contains("alpha")) {
                return "ALPHA_VERSION";
            }
        }
        return "RED_PROBLEM";
    }
}
