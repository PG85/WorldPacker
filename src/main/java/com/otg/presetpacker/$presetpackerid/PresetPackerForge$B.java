package com.otg.presetpacker.$presetpackerid;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("${presetpackerid}")
@Mod.EventBusSubscriber
public class PresetPackerForge$B {
    private static PresetUnpackUtil$B util;

    public PresetPackerForge$B() {
        // Directly reference a log4j logger.
        Logger logger = LogManager.getLogger("${presetpackerid}");
        try {
            String presetFolderPath = new File(".").getCanonicalPath() + File.separator + "config" + File.separator + "OpenTerrainGenerator" + File.separator + "Presets" + File.separator;

            logger.log(Level.INFO, "Starting extraction of ${modDisplayName}");
            // Fetch the world files from this mod's own jar
            JarFile jarFile = new JarFile(ModList.get().getModFileById("${presetpackerid}").getFile().getFilePath().toFile());
            boolean isServer = ForgeConfig.CLIENT == null;
            util = new PresetUnpackUtil$B();
            int filesWritten = util.extractPreset(jarFile, presetFolderPath, isServer);
            jarFile.close();
            if (filesWritten > 0)
                logger.log(Level.INFO, "Preset ${modDisplayName} extracted, wrote "+filesWritten+" files");
            else
                logger.log(Level.INFO, "Preset ${modDisplayName} was already up to date, skipped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
	@OnlyIn(Dist.CLIENT)
    public static void onGuiOpen(GuiOpenEvent evt) {
        if (evt.getGui() instanceof MainMenuScreen && !util.PRESET_NAME.isEmpty()) {
            evt.setGui(new ClientOutdatedPresetScreen$B(
                    new StringTextComponent("Preset Update"),
                    util.PRESET_NAME.remove(0),
                    util.OLD_MAJOR_VERSION.remove(0),
                    util.NEW_MAJOR_VERSION.remove(0),
                    util.PRESET_PATH.remove(0))
            );
        }
    }
}
