package com.otg.presetpacker.$presetpackerid;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ClientOutdatedPresetScreen$B extends Screen {
    private final String presetName;
    private final int oldVersion;
    private final int newVersion;
    private final Path presetPath;
    private Component errorHeader;
    private Screen mainMenu;

    protected ClientOutdatedPresetScreen$B(Component titleIn, String presetName, int oldVersion, int newVersion, Path presetPath, Screen mainMenu) {
        super(titleIn);
        this.presetName = presetName;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.presetPath = presetPath;
        this.mainMenu = mainMenu;
    }

    @Override
    public void init() {
        super.init();
        this.renderables.clear();
        this.errorHeader = new TextComponent(ChatFormatting.RED + "Tried to update \"" + presetName
                + "\" from v" + oldVersion + " to " + newVersion + ChatFormatting.RESET);
        int yOffset = 46;

        this.addRenderableWidget(
                new Button(
                        10,
                        this.height - yOffset,
                        this.width / 3 - 15,
                        20,
                        new TextComponent("Update and restart"),
                        b -> {
                            try {
                                File markerFile = presetPath.resolve(".REPLACE_ME").toFile();
                                if (!markerFile.exists()) {
                                    markerFile.createNewFile();
                                }
                                Minecraft.getInstance().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
        ));

        this.addRenderableWidget(
                new Button(
                        (this.width / 3)+5,
                        this.height - yOffset,
                        this.width / 3 - 15,
                        20,
                        new TextComponent("Ignore for now"),
                        b -> Minecraft.getInstance().forceSetScreen(mainMenu)));

        this.addRenderableWidget(
                new Button(
                        5+(this.width / 3 )*2,
                        this.height - yOffset,
                        this.width / 3 - 15,
                        20,
                        new TextComponent("Ignore this update"),
                        b -> {
                            try {
                                File markerFile = presetPath.resolve(".DENY_PRESET_UPDATE").toFile();
                                if (!markerFile.exists()) {
                                    markerFile.createNewFile();
                                }
                                Minecraft.getInstance().forceSetScreen(mainMenu);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
        ));
    }



    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        font.draw(matrixStack, "Preset Update", (this.width >> 1) - 50, 25, 0xFFFFFF);
        font.draw(matrixStack, this.errorHeader.getString(), (this.width >> 1) - 110, 70, 0xFF0000);
        font.draw(matrixStack, "Update may break preexisting OTG worlds using "+ presetName +".", (this.width >> 1) - 150, 90, 0xFF0000);
        this.renderables.forEach(button -> button.render(matrixStack, mouseX, mouseY, partialTicks));
    }
}
