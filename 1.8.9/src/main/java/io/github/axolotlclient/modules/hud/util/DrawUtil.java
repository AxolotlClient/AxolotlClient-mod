/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
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
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class DrawUtil extends DrawableHelper {

	public static void fillRect(Rectangle rectangle, Color color) {
		fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
	}

	public static void fillRect(int x, int y, int width, int height, int color) {
		DrawableHelper.fill(x, y, x + width, y + height, color);
	}

	public static void fillRect(int x, int y, int width, int height, Color color) {
		fillRect(x, y, x + width, y + height, color.getAsInt());
	}

	public static void outlineRect(Rectangle rectangle, Color color) {
		outlineRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.getAsInt());
	}

	public static void outlineRect(int x, int y, int width, int height, int color) {
		fillRect(x, y, 1, height - 1, color);
		fillRect(x + width - 1, y + 1, 1, height - 1, color);
		fillRect(x + 1, y, width - 1, 1, color);
		fillRect(x, y + height - 1, width - 1, 1, color);
	}

	public static void drawCenteredString(TextRenderer renderer, String text, int x, int y, Color color,
										  boolean shadow) {
		drawCenteredString(renderer, text, x, y, color.getAsInt(), shadow);
	}

	public static void drawCenteredString(TextRenderer renderer, String text, int x, int y, int color, boolean shadow) {
		drawString(text, (float) (x - renderer.getStringWidth(text) / 2), (float) y, color, shadow);
	}

	public static void drawString(String text, float x, float y, int color, boolean shadow) {
		GlStateManager.enableTexture();
		MinecraftClient.getInstance().textRenderer.draw(text, x, y, color, shadow);
	}

	public static void drawString(String text, float x, float y, Color color, boolean shadow) {
		drawString(text, x, y, color.getAsInt(), shadow);
	}

	public static void drawString(TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow) {
		drawString(text, x, y, color, shadow);
	}

	public static void drawScrollableText(TextRenderer textRenderer, String text, int left, int top, int right, int bottom, int color) {
		int i = textRenderer.getStringWidth(text);
		int j = (top + bottom - 9) / 2 + 1;
		int k = right - left;
		if (i > k) {
			int l = i - k;
			double d = (double) MinecraftClient.getTime() / 1000.0;
			double e = Math.max((double) l * 0.5, 3.0);
			double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
			double g = f * l;
			enableScissor(left, top, right, bottom);
			textRenderer.drawWithShadow(text, left - (int) g, j, color);
			disableScissor();
		} else {
			drawCenteredString(textRenderer, text, (left + right) / 2, j, color, true);
		}
	}

	public static void enableScissor(int x1, int y1, int x2, int y2) {
		GlStateManager.pushMatrix();
		Window window = io.github.axolotlclient.util.Util.getWindow();
		int i = MinecraftClient.getInstance().height;
		double d = window.getScaleFactor();
		double e = (double) x1 * d;
		double f = (double) i - (double) y2 * d;
		double g = (double) (x2 - x1) * d;
		double h = (double) (y2 - y1) * d;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) e, (int) f, Math.max(0, (int) g), Math.max(0, (int) h));
	}

	public static void disableScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
	}
}
