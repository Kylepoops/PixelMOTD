package dev.mruniverse.pixelmotd.listeners.bungeecord;

import net.md_5.bungee.api.Favicon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IconManager {
    private boolean iStatus,iCustomFolder;
    private File iFile;
    public IconManager(boolean iconStatus, boolean iconCustomFile, File iconFile){
        iStatus = iconStatus;
        iCustomFolder = iconCustomFile;
        iFile = iconFile;
    }
    public boolean getIconStatus() {
        return iStatus;
    }
    public boolean getCustomFolderStatus() {
        return iCustomFolder;
    }
    public Favicon getIcon() {
        if(iFile == null) return null;
        try {
            return Favicon.create(ImageIO.read(iFile));
        } catch (IOException ignored) {
            return null;
        }
    }
    public BufferedImage getOriginalIcon() {
        if(iFile == null) return null;
        try {
            return ImageIO.read(iFile);
        } catch (IOException ignored) {
            return null;
        }
    }
}
