package io.github.axolotlclient.modules.hud.gui.layout;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public enum Justification {
    LEFT,
    CENTER,
    RIGHT
    ;

    public int getXOffset(Text text, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getStringWidth(text.asUnformattedString()), width);
    }

    public int getXOffset(String text, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getStringWidth(text), width);
    }

    public int getXOffset(int textWidth, int width) {
        if (this == LEFT) {
            return 0;
        }
        if (this == RIGHT) {
            return width - textWidth;
        }
        return (width - textWidth) / 2;
    }
}
