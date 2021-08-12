package com.otg.presetpacker;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.io.File;
import java.io.IOException;

public class ClientOutdatedPresetScreen extends Screen {
    private ITextComponent errorHeader;
    private ITextComponent errorHeader2;
    private Screen parentScreen;

    protected ClientOutdatedPresetScreen(ITextComponent titleIn, Screen parentScreen) {
        super(titleIn);
        this.parentScreen = parentScreen;
    }

    @Override
    public void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.errorHeader = new StringTextComponent(TextFormatting.RED + "Updating OTG preset \"" + PresetUnpackUtil.thePreset + "\" to v" + PresetUnpackUtil.majorVersion + TextFormatting.RESET);
        errorHeader2 = new StringTextComponent(TextFormatting.RED + "Existing version v" + PresetUnpackUtil.majorVersionBefore + " found" + TextFormatting.RESET);
        int yOffset = 46;
        this.addButton(new ExtendedButton(50, this.height - yOffset, this.width / 2 - 55, 20, new StringTextComponent("Restart and Don't Update"), b -> {
            try {
                restartNoUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        this.addButton(new ExtendedButton(this.width / 2 + 5, this.height - yOffset, this.width / 2 - 55, 20, new StringTextComponent("Update and Restart"), b -> {
            try {
                restart();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        font.drawString(matrixStack, "Preset Update", 175, 25, 0xFFFFFF);
        font.drawString(matrixStack, this.errorHeader.getString(), 110, 70, 0xFF0000);
        font.drawString(matrixStack, this.errorHeader2.getString(), 150, 80, 0xFF0000);
        font.drawString(matrixStack, "Major version " + PresetUnpackUtil.majorVersion + " is greater and may break preexisting worlds.", 50, 90, 0xFF0000);
        this.buttons.forEach(button -> button.render(matrixStack, mouseX, mouseY, partialTicks));
    }

    private void restart() throws IOException {
        File markerFile = new File("./.REPLACE_ME");
        if (!markerFile.exists()) {
            markerFile.createNewFile();
        }
        Minecraft.getInstance().shutdownMinecraftApplet();
    }

    private void restartNoUpdate() throws IOException {
        File markerFile = new File("./.DENY_PRESET_UPDATE");
        if (!markerFile.exists()) {
            markerFile.createNewFile();
        }
        Minecraft.getInstance().shutdownMinecraftApplet();
    }
}
