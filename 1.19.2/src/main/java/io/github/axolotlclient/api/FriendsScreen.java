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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

public class FriendsScreen extends Screen {

	private final Screen parent;

	private UserListWidget widget;

	private ButtonWidget chatButton, removeButton;

	protected FriendsScreen(Screen parent) {
		super(Text.translatable("api.screen.friends"));
		this.parent = parent;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.widget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected void init() {
		addSelectableChild(widget = new UserListWidget(this, client,  width, height, 32, height - 64, 35));
		FriendHandler.getInstance().getFriends(list -> widget.setUsers(list));

		/*addDrawableChild(loginButton = new ButtonWidget.Builder(Text.translatable("auth.login"),
				buttonWidget -> login()).positionAndSize(this.width / 2 - 154, this.height - 52, 150, 20).build());*/

		this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, Text.translatable("api.friends.add"),
						button -> client.setScreen(new AddFriendScreen(this))));

		this.removeButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height - 28, 100, 20,
				Text.translatable("api.friends.remove"), button -> {
			UserListWidget.UserListEntry entry = this.widget.getSelectedOrNull();
			if (entry != null) {
				FriendHandler.getInstance().removeFriend(entry.getUser());
				refresh();
			}
		}));


		this.addDrawableChild(chatButton = new ButtonWidget(this.width / 2 - 154, this.height - 28, 100, 20,
				Text.translatable("api.friends.chat"), button -> openChat())
		);

		this.addDrawableChild(
				new ButtonWidget(this.width / 2 + 4 + 50, this.height - 28, 100, 20,
						ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
		updateButtonActivationStates();
	}

	private void openChat() {
		// TODO chat API
	}

	private void refresh(){
		client.setScreen(new FriendsScreen(parent));
	}

	private void updateButtonActivationStates() {
		UserListWidget.UserListEntry entry = widget.getSelectedOrNull();
		if (client.world == null && entry != null) {
			chatButton.active = removeButton.active = true;
		} else {
			chatButton.active = removeButton.active = false;
		}
	}

	public void select(UserListWidget.UserListEntry entry) {
		this.widget.setSelected(entry);
		this.updateButtonActivationStates();
	}
}
