package com.otg.presetpacker.$presetpackerid;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("${presetpackerid}")
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
            util = new PresetUnpackUtil$B();
            int filesWritten = util.extractPreset(jarFile, presetFolderPath);
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
