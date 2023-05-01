package io.github.axolotlclient.api.chat;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;

import java.util.ArrayList;
import java.util.List;

public class ChatListWidget extends EntryListWidget {

	protected final Screen screen;

	private final List<ChatListEntry> entries = new ArrayList<>();

	public ChatListWidget(Screen screen, int screenWidth, int screenHeight, int x, int y, int width, int height) {
		super(MinecraftClient.getInstance(), screenWidth, screenHeight, y, y+height, 25);
		xStart = x;
		xEnd = x + width;
		this.screen = screen;
		API.getInstance().send(ChannelRequest.getChannelList(list ->
			list.forEach(c -> {
				entries.add(0, new ChatListEntry(c));
			}),
			API.getInstance().getUuid(), ChannelRequest.SortBy.LAST_MESSAGE, ChannelRequest.Include.USER_STATUS));
	}

	@Override
	protected boolean isEntrySelected(int i) {
		return i == selectedEntry;
	}

	@Override
	public Entry getEntry(int i) {
		return entries.get(i);
	}

	@Override
	protected int getEntryCount() {
		return entries.size();
	}

	@Override
	protected void renderList(int x, int y, int mouseX, int mouseY) {
		DrawUtil.enableScissor(x, this.yStart, x+this.width, y+height);
		super.renderList(x, y, mouseX, mouseY);
		DrawUtil.disableScissor();
	}

	public class ChatListEntry implements Entry {

		private final Channel channel;
		private final ButtonWidget widget;
		public ChatListEntry(Channel channel){
			this.channel = channel;
			widget = new ButtonWidget(0, 0, 0, getRowWidth(), 20, channel.getName());
		}

		@Override
		public void updatePosition(int i, int j, int k) {

		}

		@Override
		public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			widget.x = (x);
			widget.y = (y);
			widget.render(client, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(int i, int j, int k, int l, int m, int n) {
			if(widget.isHovered()){
				client.setScreen(new ChatScreen(client.currentScreen, channel));
			}
			return false;
		}

		@Override
		public void mouseReleased(int i, int j, int k, int l, int m, int n) {

		}
	}
}
