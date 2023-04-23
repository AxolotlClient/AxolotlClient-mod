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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.api.handlers.ChatHandler;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.api.types.User;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

public class ChatScreen extends Screen {
	private final User user;
	private final Screen parent;

	private final List<ChatMessage> messages = new ArrayList<>();
	private int firstVisibleMessage;
	private int visibleHeight;

	private TextFieldWidget input;

	public ChatScreen(Screen parent, User user) {
		super(Text.translatable("api.screen.chat"));
		this.user = user;
		this.parent = parent;
		firstVisibleMessage = 0;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		drawCenteredText(matrices, this.textRenderer, user.getName(), this.width / 2, 20, 16777215);

		enableScissor(0, 30, width, height - 30);
		int y = 30 - client.textRenderer.fontHeight; // counting from the bottom
		for (int i = firstVisibleMessage; i < messages.size(); i++) {
			List<OrderedText> list = client.textRenderer.wrapLines(Text.of(messages.get(i).getContent()), width - 80);

			for (OrderedText text : list) {
				if (y >= visibleHeight + 30) {
					break;
				}

				client.textRenderer.draw(matrices, text, 40, height - y, -1);
				y += client.textRenderer.fontHeight;
			}

			if (y >= visibleHeight + 30) {
				break;
			}
		}
		if (y < visibleHeight + 30) {
			loadMessages();
		}
		disableScissor();
	}

	private void loadMessages() {
		long before;
		if (messages.size() != 0) {
			before = messages.get(Math.max(messages.size() - 1, 0)).getTimestamp();
		} else {
			before = Instant.now().getEpochSecond();
		}
		//ChatHandler.getInstance().getMessagesBefore(user, before);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void init() {
		visibleHeight = height - 60;
		ChatHandler.getInstance().setMessagesConsumer(messages::addAll);

		addDrawableChild(input = new TextFieldWidget(client.textRenderer, width / 2 - 150, height - 50,
			300, 20, Text.translatable("api.chat.enterMessage")) {

			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				if (keyCode == InputUtil.KEY_ENTER_CODE) {
					// TODO send chat message
					//ChatHandler.getInstance().sendMessage(user, getText());
					setText("");
					return true;
				}
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		});

		input.setChangedListener(s -> {
			if (s.isEmpty()) {
				input.setSuggestion(Text.translatable("api.chat.messageUser", user.getName()).getString());
			} else {
				input.setSuggestion("");
			}
		});

		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
				.positionAndSize(this.width / 2 - 75, this.height - 28, 150, 20)
				.build()
		);
	}

	@Override
	public void tick() {
		super.tick();
		input.tick();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		firstVisibleMessage = Math.max(firstVisibleMessage + (int) amount, 0);
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}
