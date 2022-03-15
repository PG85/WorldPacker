package com.otg.presetpacker.$presetpackerid;

public class ClientOutdatedPresetScreen$B extends Screen {
    private final String presetName;
    private final int oldVersion;
    private final int newVersion;
    private final Path presetPath;

    protected ClientOutdatedPresetScreen$B(ITextComponent titleIn, String presetName, int oldVersion, int newVersion, Path presetPath) {
        super(titleIn);
        this.presetName = presetName;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.presetPath = presetPath;
    }
}
