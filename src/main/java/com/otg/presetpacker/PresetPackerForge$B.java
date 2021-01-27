package com.otg.presetpacker;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("${presetpackerid}")
public class PresetPackerForge$B
{
    public PresetPackerForge$B() {
        // Register the setup method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    // We use the Register Blocks event to ensure we unpack files before OTG starts loading biomes
    // The block registry is the first event to fire, which is why we specifically use this event
    @SubscribeEvent
    public void setup(RegistryEvent.Register<Block> event)
    {
        // Directly reference a log4j logger.
        Logger logger = LogManager.getLogger("${presetpackerid}");
        try
        {
            String presetFolderPath = new File(".").getCanonicalPath() + File.separator + "config" + File.separator + "OpenTerrainGenerator" + File.separator + "Presets" + File.separator;

            logger.log(Level.INFO, "Starting extraction of ${modDisplayName}");
            // Fetch the world files from this mod's own jar
            JarFile jarFile = new JarFile(ModList.get().getModFileById("${presetpackerid}").getFile().getFilePath().toFile());
            int filesWritten = PresetUnpackUtil.extractPreset(jarFile, presetFolderPath);
            jarFile.close();
            InterModComms.sendTo("OTG", "loadNewPreset", () -> "${modDisplayName}");
            logger.log(Level.INFO, "Preset ${modDisplayName} extracted, wrote {} files", filesWritten);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
