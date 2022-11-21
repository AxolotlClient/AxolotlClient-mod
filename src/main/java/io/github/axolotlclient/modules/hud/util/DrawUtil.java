/*
 * This File is part of AxolotlClient (mod)
 * Copyright (C) 2021-present moehreag + Contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.github.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class DrawUtil extends DrawableHelper{

    public static void fillRect(Rectangle rectangle, Color color) {
        fillRect(rectangle.x, rectangle.y, rectangle.width,
                rectangle.height,
                color.getAsInt());
    }

    public static void fillRect(int x, int y, int width, int height, Color color) {
        fillRect(x, y, x + width, y + height, color.getAsInt());
    }

    public static void fillRect(int x, int y, int width, int height, int color) {
        DrawableHelper.fill(x, y, x + width, y + height, color);
    }

    public static void outlineRect(Rectangle rectangle, Color color) {
        outlineRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
    }

    public static void outlineRect(int x, int y, int width, int height, int color) {
        fillRect(x, y, 1, height-1, color);
        fillRect(x + width - 1, y + 1, 1, height-1, color);
        fillRect(x+1, y, width-1, 1, color);
        fillRect(x, y + height - 1, width-1, 1, color);
    }


    public static void drawCenteredString(
            TextRenderer renderer,
            String text, int x, int y,
            Color color, boolean shadow
    ) {
        drawCenteredString(renderer, text, x, y, color.getAsInt(), shadow);
    }


    public static void drawCenteredString(
            TextRenderer renderer,
            String text, int x, int y,
            int color, boolean shadow
    ) {
        drawString(text, (float) (x - renderer.getStringWidth(text) / 2),
                (float) y,
                color, shadow
        );
    }

    public static void drawString(String text, float x, float y, Color color, boolean shadow){
        drawString(text, x, y, color.getAsInt(), shadow);
    }

    public static void drawString(
            TextRenderer textRenderer, String text, float x, float y,
            int color, boolean shadow
    ) {
        drawString(text, x, y, color, shadow);
    }

    public static void drawString(
            String text, float x, float y,
            int color, boolean shadow) {

        GlStateManager.enableTexture();
        MinecraftClient.getInstance().textRenderer.draw(text, x, y, color, shadow);
    }

}
