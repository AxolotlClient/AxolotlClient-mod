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

import io.github.axolotlclient.api.handlers.FriendHandler;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.util.notifications.Notifications;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.UUID;

public class AddFriendScreen extends Screen {

	private final Screen parent;
	private TextFieldWidget nameInput;

	public AddFriendScreen(Screen parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void render(int i, int j, float f) {
		renderBackground();
		super.render(i, j, f);
		textRenderer.drawWithShadow(I18n.translate("api.screen.friends.add.name"), width / 2F - 100, height / 2f - 20, -1);
		drawCenteredString(this.textRenderer, I18n.translate("api.screen.friends.add"), this.width / 2, 20, 16777215);
		nameInput.render();
	}

	@Override
	public void keyPressed(char c, int keyCode) {
		super.keyPressed(c, keyCode);
		nameInput.keyPressed(c, keyCode);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		nameInput.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void buttonClicked(ButtonWidget buttonWidget) {
		if (buttonWidget.id == 0) {
			client.setScreen(parent);
		} else if (buttonWidget.id == 1) {
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
		}
	}

	@Override
	public void init() {
		nameInput = new TextFieldWidget(0, textRenderer, width / 2 - 100, height / 2 - 10, 200, 20);

		buttons.add(new ButtonWidget(0, width / 2 - 155, height - 50, 150, 20, I18n.translate("gui.cancel")));
		buttons.add(new ButtonWidget(1, width / 2 + 5, height - 50, 150, 20, I18n.translate("gui.done")));
	}

	@Override
	public void tick() {
		nameInput.tick();
	}
}
