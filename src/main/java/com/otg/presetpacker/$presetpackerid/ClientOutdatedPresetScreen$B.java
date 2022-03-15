package com.otg.presetpacker.$presetpackerid;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ClientOutdatedPresetScreen$B extends Screen {
    private final String presetName;
    private final int oldVersion;
    private final int newVersion;
    private final Path presetPath;
    private ITextComponent errorHeader;

    protected ClientOutdatedPresetScreen$B(ITextComponent titleIn, String presetName, int oldVersion, int newVersion, Path presetPath) {
        super(titleIn);
        this.presetName = presetName;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.presetPath = presetPath;
    }

    @Override
    public void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.errorHeader = new StringTextComponent(TextFormatting.RED + "Tried to update \"" + presetName
                + "\" from v" + oldVersion + " to " + newVersion + TextFormatting.RESET);
        int yOffset = 46;

        this.addButton(
                new ExtendedButton(
                        10,
                        this.height - yOffset,
                        this.width / 3 - 15,
                        20,
                        new StringTextComponent("Update and restart"),
                        b -> {
                            try {
                                File markerFile = presetPath.resolve(".REPLACE_ME").toFile();
                                if (!markerFile.exists()) {
                                    markerFile.createNewFile();
                                }
                                Minecraft.getInstance().shutdownMinecraftApplet();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
        ));

        this.addButton(
                new ExtendedButton(
                        (this.width / 3)+5,
                        this.height - yOffset,
                        this.width / 3 - 15,
                        20,
                        new StringTextComponent("Ignore for now"),
                        b -> this.closeScreen()));

        this.addButton(
                new ExtendedButton(
                        5+(this.width / 3 )*2,
                        this.height - yOffset,
                        this.width / 3 - 15,
                        20,
                        new StringTextComponent("Ignore this update"),
                        b -> {
                            try {
                                File markerFile = presetPath.resolve(".DENY_PRESET_UPDATE").toFile();
                                if (!markerFile.exists()) {
                                    markerFile.createNewFile();
                                }
                                this.closeScreen();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        font.drawString(matrixStack, "Preset Update", (this.width >> 1) - 50, 25, 0xFFFFFF);
        font.drawString(matrixStack, this.errorHeader.getString(), (this.width >> 1) - 110, 70, 0xFF0000);
        font.drawString(matrixStack, "Update may break preexisting OTG worlds using "+ presetName +".", (this.width >> 1) - 150, 90, 0xFF0000);
        this.buttons.forEach(button -> button.render(matrixStack, mouseX, mouseY, partialTicks));
    }
}
