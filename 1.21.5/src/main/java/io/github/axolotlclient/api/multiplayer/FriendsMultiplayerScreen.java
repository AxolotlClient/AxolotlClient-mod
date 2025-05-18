/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.api.multiplayer;

import io.github.axolotlclient.api.FriendsScreen;
import io.github.axolotlclient.api.handlers.StatusUpdateHandler;
import io.github.axolotlclient.api.requests.FriendRequest;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class FriendsMultiplayerScreen extends Screen {
	@Getter
	private final ServerStatusPinger pinger = new ServerStatusPinger();
	private final Screen lastScreen;
	protected FriendsMultiplayerSelectionList serverSelectionList;
	private Button selectButton;
	private final Button friendsCountButton = Button.builder(Component.translatable("api.servers.friends", "..."), button -> {
	}).build();
	private ServerData editingServer;
	private boolean initialized;
	private static final Component NO_ONLINE_FRIENDS = Component.translatable("api.servers.friends.no_online_friends");

	public FriendsMultiplayerScreen(Screen lastScreen) {
		super(Component.translatable("api.servers.friends.title"));
		this.lastScreen = lastScreen;
	}

	@Override
	protected void init() {
		if (this.initialized) {
			this.serverSelectionList.setRectangle(this.width, this.height - 64 - 60, 0, 60);
		} else {
			this.serverSelectionList = new FriendsMultiplayerSelectionList(this, this.minecraft, this.width, this.height - 64 - 60, 60, 36);
		}
		StatusUpdateHandler.addUpdateListener("friends_multiplayer_screen", serverSelectionList::updateEntry);

		this.addRenderableWidget(this.serverSelectionList);
		addRenderableWidget(Button.builder(Component.translatable("api.servers"), button ->
			minecraft.setScreen(new JoinMultiplayerScreen(lastScreen))).pos(this.width / 2 - 102, 32).width(100).build());
		addRenderableWidget(friendsCountButton).setRectangle(100, 20, width / 2 + 2, 32);
		friendsCountButton.active = false;

		if (!initialized) {
			initialized = true;
			FriendRequest.getInstance().getFriends().thenAccept(friends -> {
				friendsCountButton.setMessage(Component.translatable("api.servers.friends", friends.stream().filter(u -> u.getStatus().isOnline()).count()));
				this.serverSelectionList.updateList(friends);
			});
		}

		this.selectButton = this.addRenderableWidget(
			Button.builder(Component.translatable("selectServer.select"), buttonx -> this.joinSelectedServer()).width(100).build()
		);
		Button directConnect = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.direct"), buttonx -> {
			this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
			this.minecraft.setScreen(new DirectJoinServerScreen(this, this::directJoinCallback, this.editingServer));
		}).width(100).build());
		Button friends = this.addRenderableWidget(Button.builder(Component.translatable("api.friends"), buttonx ->
			this.minecraft.setScreen(new FriendsScreen(this))).width(100).build());
		Button editButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.edit"), buttonx -> {
		}).width(74).build());
		editButton.active = false;
		Button deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.delete"), buttonx -> {
		}).width(74).build());
		deleteButton.active = false;
		Button refreshList = this.addRenderableWidget(
			Button.builder(Component.translatable("selectServer.refresh"), buttonx -> this.refreshServerList()).width(74).build()
		);
		Button back = this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, buttonx -> this.onClose()).width(74).build());
		LinearLayout linearLayout = LinearLayout.vertical();
		EqualSpacingLayout equalSpacingLayout = linearLayout.addChild(new EqualSpacingLayout(308, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
		equalSpacingLayout.addChild(this.selectButton);
		equalSpacingLayout.addChild(directConnect);
		equalSpacingLayout.addChild(friends);
		linearLayout.addChild(SpacerElement.height(4));
		EqualSpacingLayout equalSpacingLayout2 = linearLayout.addChild(new EqualSpacingLayout(308, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
		equalSpacingLayout2.addChild(editButton);
		equalSpacingLayout2.addChild(deleteButton);
		equalSpacingLayout2.addChild(refreshList);
		equalSpacingLayout2.addChild(back);
		linearLayout.arrangeElements();
		FrameLayout.centerInRectangle(linearLayout, 0, this.height - 64, this.width, 64);
		this.onSelectedChange();
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.lastScreen);
	}

	@Override
	public void tick() {
		super.tick();
		this.pinger.tick();
	}

	@Override
	public void removed() {
		StatusUpdateHandler.removeUpdateListener("friends_multiplayer_screen");
		this.pinger.removeAll();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(font, getTitle(), width / 2, 15, -1);

		if (serverSelectionList.children().isEmpty()) {
			guiGraphics.drawCenteredString(font, NO_ONLINE_FRIENDS, width / 2, height / 2 - font.lineHeight / 2, -1);
		}
	}

	private void refreshServerList() {
		this.minecraft.setScreen(new FriendsMultiplayerScreen(this.lastScreen));
	}

	private void directJoinCallback(boolean confirmed) {
		if (confirmed) {
			ServerList servers = new ServerList(minecraft);
			servers.load();
			ServerData serverData = servers.get(this.editingServer.ip);
			if (serverData == null) {
				servers.add(this.editingServer, true);
				servers.save();
				this.join(this.editingServer);
			} else {
				this.join(serverData);
			}
		} else {
			this.minecraft.setScreen(this);
		}
	}

	public void joinSelectedServer() {
		FriendsMultiplayerSelectionList.Entry entry = this.serverSelectionList.getSelected();
		this.join(entry.getServerData());
	}

	private void join(ServerData server) {
		if (server == null) {
			return;
		}
		ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(server.ip), server, false, null);
	}

	public void setSelected(FriendsMultiplayerSelectionList.Entry selected) {
		this.serverSelectionList.setSelected(selected);
		this.onSelectedChange();
	}

	protected void onSelectedChange() {
		this.selectButton.active = false;
		FriendsMultiplayerSelectionList.Entry entry = this.serverSelectionList.getSelected();
		if (entry != null && !(entry instanceof FriendsMultiplayerSelectionList.LoadingHeader)) {
			this.selectButton.active = entry.canJoin();
		}
	}
}
