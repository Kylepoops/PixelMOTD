package dev.mruniverse.pixelmotd.utils;

import dev.mruniverse.pixelmotd.files.SpigotControl;
import dev.mruniverse.pixelmotd.PixelBungee;
import dev.mruniverse.pixelmotd.PixelSpigot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;



public class PixelUpdater {
    private final String currentVersion;
    private boolean isDetailed;

    private String newestVersion;

    public PixelUpdater(boolean isBungee, int projectID) {
        if(isBungee) {
            currentVersion = PixelBungee.getInstance().getDescription().getVersion();
            isDetailed     = PixelBungee.getInstance().getBungeeControl().isDetailed();
        } else {
            currentVersion = PixelSpigot.getInstance().getDescription().getVersion();
            // TODO ISDETAILED FOR SPIGOT
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
                bungeeLogger.warn("Can't connect to SpigotMC and bStats");
                if(isDetailed) {
                    bungeeLogger.error("Information:");
                    if(ignored.getMessage() != null) {
                        bungeeLogger.error("Message: " + ignored.getMessage());
                    }
                    if(ignored.getLocalizedMessage() != null) {
                        bungeeLogger.error("LocalizedMessage: " + ignored.getLocalizedMessage());
                    }
                    if(ignored.getStackTrace() != null) {
                        bungeeLogger.error("StackTrace: ");
                        for(StackTraceElement line : ignored.getStackTrace()) {
                            bungeeLogger.error("(" + line.getLineNumber() + ") " + line.toString());
                        }
                    }
                    if(ignored.getSuppressed() != null) {
                        bungeeLogger.error("Suppressed: " + Arrays.toString(ignored.getSuppressed()));
                    }
                    if(ignored.getClass() != null) {
                        bungeeLogger.error("Class: " + ignored.getClass().getName());
                    }
                    bungeeLogger.error("Plugin version:" + PixelBungee.getInstance().getDescription().getVersion());
                    bungeeLogger.error("--------------- [Detailed Error]");
                }
            } else {
                spigotLogger.warn("Can't connect to SpigotMC and bStats");
                if(SpigotControl.isDetailed()) {
                    spigotLogger.error("[Detailed Error] Information:");
                    if(ignored.getMessage() != null) {
                        spigotLogger.error("Message: " + ignored.getMessage());
                    }
                    if(ignored.getLocalizedMessage() != null) {
                        spigotLogger.error("LocalizedMessage: " + ignored.getLocalizedMessage());
                    }
                    if(ignored.getStackTrace() != null) {
                        spigotLogger.error("StackTrace: ");
                        for(StackTraceElement line : ignored.getStackTrace()) {
                            spigotLogger.error("(" + line.getLineNumber() + ") " + line.toString());
                        }
                    }
                    if(ignored.getSuppressed() != null) {
                        spigotLogger.error("Suppressed: " + Arrays.toString(ignored.getSuppressed()));
                    }
                    if(ignored.getClass() != null) {
                        spigotLogger.error("Class: " + ignored.getClass().getName());
                    }
                    spigotLogger.error("Plugin version:" + PixelSpigot.getInstance().getDescription().getVersion());
                    spigotLogger.error("---------------");
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
