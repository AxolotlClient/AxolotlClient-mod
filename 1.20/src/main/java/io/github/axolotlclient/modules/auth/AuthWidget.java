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

package io.github.axolotlclient.modules.auth;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AuthWidget extends ButtonWidget {

	public AuthWidget() {
		super(10, 10,
			MinecraftClient.getInstance().textRenderer.getWidth(Auth.getInstance().getCurrent().getName()) + 28,
			20, Text.of("    " + Auth.getInstance().getCurrent().getName()), buttonWidget -> MinecraftClient.getInstance().setScreen(new AccountsScreen(MinecraftClient.getInstance().currentScreen)), DEFAULT_NARRATION);
	}

	@Override
	public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.drawWidget(graphics, mouseX, mouseY, delta);
		Identifier texture = Auth.getInstance().getSkinTexture(Auth.getInstance().getCurrent());
		RenderSystem.enableBlend();
		graphics.drawTexture(texture, getX() + 1, getY() + 1, getHeight() - 2, getHeight() - 2, 8, 8, 8, 8, 64, 64);
		graphics.drawTexture(texture, getX() + 1, getY() + 1, getHeight() - 2, getHeight() - 2, 40, 8, 8, 8, 64, 64);
		RenderSystem.disableBlend();
	}
}