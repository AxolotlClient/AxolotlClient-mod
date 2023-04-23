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

import io.github.axolotlclient.api.handlers.ChatHandler;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.ChatMessage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatWidget extends DrawableHelper implements Element, Drawable, Selectable {

	private final List<ChatMessage> messages = new ArrayList<>();
	private final List<ChatLine> lines = new ArrayList<>();
	private final Channel channel;
	private final MinecraftClient client;
	private boolean focused;
	private int scrollAmount;
	@Setter
	@Getter
	private int x, y, width, height;

	public ChatWidget(Channel channel, int x, int y, int width, int height) {
		this.channel = channel;
		this.client = MinecraftClient.getInstance();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		Arrays.stream(channel.getMessages()).forEach(this::addMessage);

		ChatHandler.getInstance().setMessagesConsumer(chatMessages -> chatMessages.forEach(this::addMessage));
		ChatHandler.getInstance().setMessageConsumer(this::addMessage);
		ChatHandler.getInstance().setEnableNotifications(message -> !Arrays.stream(channel.getUsers()).collect(Collectors.toUnmodifiableSet()).contains(message.getSender()));
	}

	private void addMessage(ChatMessage message) {
		List<OrderedText> list = client.textRenderer.wrapLines(Text.of(message.getContent()), width - 23);

		if (messages.size() > 0) {
			ChatMessage prev = messages.get(messages.size() - 1);
			if (!prev.getSender().equals(message.getSender())) {
				lines.add(new ChatLine.NameChatLine(message));
			} else {
				if (prev.getTimestamp() - message.getTimestamp() > 150) {
					lines.add(new ChatLine.NameChatLine(message));
				}
			}
		} else {
			lines.add(new ChatLine.NameChatLine(message));
		}

		list.forEach(t -> lines.add(new ChatLine(t, message)));
		messages.add(message);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		enableScissor(x, y, x + width, y + height+2);

		matrices.push();
		matrices.translate(0, scrollAmount * (client.textRenderer.fontHeight / 2F), 0);

		if (scrollAmount < 0) {
			loadMessages();
		}

		int y = this.y + height - client.textRenderer.fontHeight * lines.size() - 10;

		for (ChatLine line : lines) {
			y = line.render(matrices, x + 22, y, -1, mouseX, mouseY);
			if (y > this.y + this.height) {
				break;
			}
		}

		matrices.pop();
		disableScissor();
	}

	private void loadMessages() {
		long before;
		if (messages.size() != 0) {
			before = messages.get(Math.max(messages.size() - 1, 0)).getTimestamp();
		} else {
			before = Instant.now().getEpochSecond();
		}
		//ChatHandler.getInstance().getMessagesBefore(before);
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {

		int visibleLineCount = (lines.size() - (height / client.textRenderer.fontHeight)) * 2;
		scrollAmount = MathHelper.clamp(scrollAmount + (int) amount, Math.min(0, visibleLineCount), Math.max(0, visibleLineCount));
		return true;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	public void remove() {
		ChatHandler.getInstance().setMessagesConsumer(ChatHandler.DEFAULT_MESSAGES_CONSUMER);
		ChatHandler.getInstance().setMessageConsumer(ChatHandler.DEFAULT_MESSAGE_CONSUMER);
		ChatHandler.getInstance().setEnableNotifications(ChatHandler.DEFAULT);
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}
}
