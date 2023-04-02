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

package io.github.axolotlclient.api;

import java.util.UUID;

import io.github.axolotlclient.api.handlers.FriendHandler;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.util.notifications.Notifications;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

public class AddFriendScreen extends Screen {

	private final Screen parent;
	private TextFieldWidget nameInput;

	public AddFriendScreen(Screen parent) {
		super(Text.translatable("api.screen.friends.add"));
		this.parent = parent;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button) || nameInput.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack matrices, int i, int j, float f) {
		renderBackground(matrices);
		super.render(matrices, i, j, f);
		textRenderer.drawWithShadow(matrices, Text.translatable("api.screen.friends.add.name"), width / 2F - 100, height / 2f - 20, -1);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		nameInput.render(matrices, i, j, f);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers) || nameInput.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void init() {
		addDrawableChild(nameInput = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 10, 200, 20, Text.empty()));

		addDrawableChild(new ButtonWidget(width / 2 - 155, height - 50, 150, 20, ScreenTexts.CANCEL, button -> client.setScreen(parent)));
		addDrawableChild(new ButtonWidget(width / 2 + 5, height - 50, 150, 20, ScreenTexts.DONE, button -> {
			if (API.getInstance().isConnected()) {
				String uuid;
				try {
					uuid = API.getInstance().sanitizeUUID(UUID.fromString(nameInput.getText()).toString());
				} catch (IllegalArgumentException e) {
					uuid = UUIDHelper.getUuid(nameInput.getText());
				}
				FriendHandler.getInstance().addFriend(uuid);
				client.setScreen(parent);
			} else {
				Notifications.getInstance().addStatus("api.error.notLoggedIn", "api.error.notLoggedIn.desc");
				client.setScreen(parent);
			}
		}));
	}

	@Override
	public void tick() {
		nameInput.tick();
	}
}
