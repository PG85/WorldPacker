package com.otg.presetpacker;

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

    public static GuiOpenEvent guiOpenEvent;

    public PresetPackerForge$B() {
        // Directly reference a log4j logger.
        Logger logger = LogManager.getLogger("${presetpackerid}");
        try {
            String presetFolderPath = new File(".").getCanonicalPath() + File.separator + "config" + File.separator + "OpenTerrainGenerator" + File.separator + "Presets" + File.separator;

            logger.log(Level.INFO, "Starting extraction of ${modDisplayName}");
            // Fetch the world files from this mod's own jar
            JarFile jarFile = new JarFile(ModList.get().getModFileById("${presetpackerid}").getFile().getFilePath().toFile());
            boolean isServer = ForgeConfig.CLIENT == null;
            int filesWritten = PresetUnpackUtil.extractPreset(jarFile, presetFolderPath, isServer);
            jarFile.close();
            if (filesWritten > 0)
                logger.log(Level.INFO, "Preset ${modDisplayName} extracted, wrote {} files", filesWritten);
            else
                logger.log(Level.INFO, "Preset ${modDisplayName} was already up to date, skipped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent evt) {
        if (evt.getGui() instanceof MainMenuScreen && PresetUnpackUtil.requiresErrorScreen) {
            guiOpenEvent = evt;
            Screen mainMenuScreen = evt.getGui();
            evt.setGui(new ClientOutdatedPresetScreen(new StringTextComponent("Preset Update"), mainMenuScreen));
        }
    }
}
