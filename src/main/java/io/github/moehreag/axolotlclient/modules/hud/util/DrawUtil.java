package io.github.moehreag.axolotlclient.modules.hud.util;

import io.github.moehreag.axolotlclient.config.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */
public class DrawUtil extends DrawableHelper{

    public static void fillRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        fillRect(matrices, rectangle.x, rectangle.y, rectangle.width,
                rectangle.height,
                color.getAsInt());
    }

    private static void fillRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
    }

    public static void outlineRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        outlineRect(matrices, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
    }

    private static void outlineRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        fillRect(matrices, x, y, 1, height, color);
        fillRect(matrices, x + width - 1, y, 1, height, color);
        fillRect(matrices, x, y, width, 1, color);
        fillRect(matrices, x, y + height - 1, width, 1, color);
    }


    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer,
                                          String text, DrawPosition position,
                                          Color color, boolean shadow) {
        drawCenteredString(matrices, renderer, text, position, color.getAsInt(), shadow);
    }


    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer,
                                          String text, DrawPosition position,
                                          int color, boolean shadow) {
        drawString(matrices, renderer, text, position.x - renderer.getWidth(text) / 2,
                position.y,
                color, shadow);
    }

    public static void drawString(MatrixStack matrices, TextRenderer renderer, String text, int x, int y,
                                  int color, boolean shadow) {
        if(shadow) {
            renderer.drawWithShadow(matrices, text, x, y, color);
        }
        else {
            renderer.draw(matrices, text, x, y, color);
        }
    }

}
