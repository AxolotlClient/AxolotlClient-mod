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

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.api.handlers.ChatHandler;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.User;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

public class ChatScreen extends Screen {
	private final Channel channel;
	private final Screen parent;

	private ChatWidget widget;

	private TextFieldWidget input;

	public ChatScreen(Screen parent, Channel channel) {
		super(Text.translatable("api.screen.chat"));
		this.channel = channel;
		this.parent = parent;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		drawCenteredText(matrices, this.textRenderer, channel.getName(), this.width / 2, 20, 16777215);
	}

	@Override
	protected void init() {
		addDrawable(widget = new ChatWidget(channel, 50, 30, width-100, height-90));

		addDrawableChild(input = new TextFieldWidget(client.textRenderer, width/2-150, height-50,
				300, 20, Text.translatable("api.chat.enterMessage")) {

			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				if (keyCode == InputUtil.KEY_ENTER_CODE && !getText().isEmpty()) {
					// TODO send chat message
					ChatHandler.getInstance().sendMessage(channel, getText());
					setText("");
					return true;
				}
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		});

		input.setSuggestion(Text.translatable("api.chat.messageUser", channel.getName()).getString());
		input.setChangedListener(s -> {
			if(s.isEmpty()){
				input.setSuggestion(Text.translatable("api.chat.messageUser", channel.getName()).getString());
			} else {
				input.setSuggestion("");
			}
		});
		input.setMaxLength(1024);

		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
						.positionAndSize(this.width / 2 - 75, this.height - 28, 150, 20)
						.build()
		);
	}

	@Override
	public void tick() {
		input.tick();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if(widget != null){
			return widget.mouseScrolled(mouseX, mouseY, amount);
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public void removed() {
		if(widget != null) {
			widget.remove();
		}
	}
}