package io.github.axolotlclient.modules.hud.util;

import io.github.axolotlclient.config.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */
public class DrawUtil extends DrawableHelper{

    public static void fillRect(Rectangle rectangle, Color color) {
        fillRect(rectangle.x, rectangle.y, rectangle.width,
                rectangle.height,
                color.getAsInt());
    }

    private static void fillRect(int x, int y, int width, int height, int color) {
        DrawableHelper.fill(x, y, x + width, y + height, color);
    }

    public static void outlineRect(Rectangle rectangle, Color color) {
        outlineRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
    }

    private static void outlineRect(int x, int y, int width, int height, int color) {
        fillRect(x, y, 1, height, color);
        fillRect(x + width - 1, y, 1, height, color);
        fillRect(x, y, width, 1, color);
        fillRect(x, y + height - 1, width, 1, color);
    }


    public static void drawCenteredString(TextRenderer renderer,
                                          String text, DrawPosition position,
                                          Color color, boolean shadow) {
        drawCenteredString(renderer, text, position, color.getAsInt(), shadow);
    }


    public static void drawCenteredString(TextRenderer renderer,
                                          String text, DrawPosition position,
                                          int color, boolean shadow) {
        drawString(renderer, text, position.x - renderer.getStringWidth(text) / 2,
                position.y,
                color, shadow);
    }

    public static void drawString(TextRenderer renderer, String text, int x, int y,
                                  int color, boolean shadow) {
        if(shadow) {
            renderer.drawWithShadow(text, x, y, color);
        }
        else {
            renderer.draw(text, x, y, color);
        }
    }

}
