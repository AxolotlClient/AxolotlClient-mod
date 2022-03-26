package io.github.moehreag.axolotlclient.config.widgets;

import net.minecraft.client.MinecraftClient;

public class TextFieldWidget extends net.minecraft.client.gui.widget.TextFieldWidget {


    public TextFieldWidget(int id, int x, int y) {
        super(id, MinecraftClient.getInstance().textRenderer, x, y, 150, 20);
    }
    public TextFieldWidget(int id, int x, int y, int width){
        super(id, MinecraftClient.getInstance().textRenderer, x, y, width, 20);
    }
}
