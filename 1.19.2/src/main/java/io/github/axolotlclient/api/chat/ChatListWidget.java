package io.github.axolotlclient.api.chat;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.types.Channel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ChatListWidget extends AlwaysSelectedEntryListWidget<ChatListWidget.ChatListEntry> {

	protected final Screen screen;
	public ChatListWidget(Screen screen, int screenWidth, int screenHeight, int x, int y, int width, int height) {
		super(MinecraftClient.getInstance(), screenWidth, screenHeight, y, y+height, 25);
		left = x;
		right = x + width;
		this.screen = screen;
		API.getInstance().send(ChannelRequest.getChannelList(list ->
			list.forEach(c -> {
				addEntryToTop(new ChatListEntry(c));
			}),
			API.getInstance().getUuid(), ChannelRequest.SortBy.LAST_MESSAGE, ChannelRequest.Include.USER_STATUS));
	}

	public class ChatListEntry extends Entry<ChatListEntry> {

		private final Channel channel;
		private final ButtonWidget widget;
		public ChatListEntry(Channel channel){
			this.channel = channel;
			widget = new ButtonWidget(0, 0, getRowWidth(), 20, Text.of(channel.getName()),
				buttonWidget -> client.setScreen(new ChatScreen(client.currentScreen, channel)));
		}

		@Override
		public Text getNarration() {
			return Text.of(channel.getName());
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			widget.x = (x);
			widget.y = (y);
			widget.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
}
