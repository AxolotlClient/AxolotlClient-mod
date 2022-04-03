package io.github.moehreag.axolotlclient.modules.hud.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */
public class DrawUtil extends DrawableHelper{

    public static void fillRect(Rectangle rectangle, Color color) {
        fillRect(rectangle.x, rectangle.y, rectangle.width,
                rectangle.height,
                color.color);
    }

    private static void fillRect(int x, int y, int width, int height, int color) {
        DrawableHelper.fill(x, y, x + width, y + height, color);
    }

    public static void outlineRect(Rectangle rectangle, Color color) {
        outlineRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.color);
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
        drawCenteredString(renderer, text, position, color.color, shadow);
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

    public static void applyScissor(Rectangle scissor) {
        Window window = new Window(MinecraftClient.getInstance());
        double scale = window.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (scissor.x * scale), (int) ((window.getScaledHeight() - scissor.height - scissor.y) * scale), (int) (scissor.width * scale), (int) (scissor.height * scale));
    }

    public static void removeScissors() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

}
