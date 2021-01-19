package com.otg.presetpacker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class PresetPackerSpigot$B extends JavaPlugin {
    @Override
    public void onEnable()
    {
        java.util.logging.Logger logger = Bukkit.getLogger();
        try
        {
            String presetFolderPath = new File(".").getCanonicalPath() + File.separator + "plugins" + File.separator + "OpenTerrainGenerator" + File.separator + "Presets" + File.separator;
            logger.log(java.util.logging.Level.INFO, "Extracting ${displayName}");
            JarFile jarFile = new JarFile(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()));
            PresetUnpackUtil.extractPreset(jarFile, presetFolderPath);
            jarFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
