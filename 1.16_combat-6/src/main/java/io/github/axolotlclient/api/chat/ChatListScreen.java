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

package io.github.axolotlclient.api.chat;

import io.github.axolotlclient.api.SimpleTextInputScreen;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.types.Channel;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;

public class ChatListScreen extends Screen {

	private final Screen parent;
	private ChatListWidget dms;
	private ChatListWidget groups;

	public ChatListScreen(Screen parent) {
		super(new TranslatableText("api.chats"));
		this.parent = parent;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		dms.render(matrices, mouseX, mouseY, delta);
		groups.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);

		drawCenteredText(matrices, client.textRenderer, new TranslatableText("api.chats"), width / 2, 20, -1);
		drawCenteredText(matrices, client.textRenderer, new TranslatableText("api.chat.dms"), width / 2 + 80, 40, -1);
		drawCenteredText(matrices, client.textRenderer, new TranslatableText("api.chat.groups"), width / 2 - 80, 40, -1);
	}

	@Override
	protected void init() {
		addChild(dms = new ChatListWidget(this, width, height, width / 2 - 155, 55, 150, height - 105, c -> !c.isDM()));
		addChild(groups = new ChatListWidget(this, width, height, width / 2 + 5, 55, 150, height - 105, Channel::isDM));

		addButton(new ButtonWidget(this.width / 2 + 5, this.height - 40, 150, 20, ScreenTexts.BACK, buttonWidget ->
			client.openScreen(parent)));
		addButton(new ButtonWidget(this.width / 2 - 155, this.height - 40, 150, 20,
			new TranslatableText("api.chat.groups.create"), buttonWidget ->
			client.openScreen(new SimpleTextInputScreen(this, new TranslatableText("api.chat.groups.create"),
				new TranslatableText("api.chat.groups.create.label"), s -> {
				if (!s.trim().isEmpty()) {
					ChannelRequest.createGroup(Arrays.stream(s.split(",")).map(String::trim).toArray(String[]::new));
				}
			}))));
	}
}