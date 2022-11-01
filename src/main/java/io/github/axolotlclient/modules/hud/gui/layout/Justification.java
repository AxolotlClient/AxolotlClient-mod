package io.github.axolotlclient.modules.hud.gui.layout;

import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

@AllArgsConstructor
public enum Justification {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right")
    ;

    private final String key;

    public int getXOffset(Text text, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getWidth(text), width);
    }

    public int getXOffset(String text, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getWidth(text), width);
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
