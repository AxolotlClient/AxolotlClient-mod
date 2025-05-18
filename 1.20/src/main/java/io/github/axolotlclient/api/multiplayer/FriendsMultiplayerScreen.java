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
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class FriendsMultiplayerScreen extends Screen {
	@Getter
	private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
	private final Screen lastScreen;
	protected FriendsMultiplayerSelectionList serverSelectionList;
	private ButtonWidget selectButton;
	private ServerInfo editingServer;
	private boolean initialized;
	private final ButtonWidget friendsCountButton = ButtonWidget.builder(Text.translatable("api.servers.friends", "..."), button -> {
	}).build();
	private static final Text NO_ONLINE_FRIENDS = Text.translatable("api.servers.friends.no_online_friends");

	public FriendsMultiplayerScreen(Screen lastScreen) {
		super(Text.translatable("api.servers.friends.title"));
		this.lastScreen = lastScreen;
	}

	@Override
	protected void init() {
		if (this.initialized) {
			this.serverSelectionList.updateSize(this.width, this.height, 60, this.height-64);
		} else {
			this.serverSelectionList = new FriendsMultiplayerSelectionList(this, this.client, this.width, this.height - 64 - 60, 60, 36);
		}
		StatusUpdateHandler.addUpdateListener("friends_multiplayer_screen", serverSelectionList::updateEntry);
		this.addDrawableChild(this.serverSelectionList);
		addDrawableChild(ButtonWidget.builder(Text.translatable("api.servers"), button ->
			client.setScreen(new MultiplayerScreen(lastScreen))).position(this.width / 2 - 102, 32).width(100).build());
		addDrawableChild(friendsCountButton).setPosition(width / 2 + 2, 32);
		friendsCountButton.setWidth(100);
		friendsCountButton.active = false;

		if (!initialized) {
			initialized = true;
			FriendRequest.getInstance().getFriends().thenAccept(friends -> {
				friendsCountButton.setMessage(Text.translatable("api.servers.friends", friends.stream().filter(u -> u.getStatus().isOnline()).count()));
				this.serverSelectionList.updateList(friends);
			});
		}

		this.selectButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectServer.select"), buttonx -> this.joinSelectedServer()).width(100).build()
		);
		ButtonWidget directConnect = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.direct"), buttonx -> {
			this.editingServer = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
			this.client.setScreen(new DirectConnectScreen(this, this::directJoinCallback, this.editingServer));
		}).width(100).build());
		ButtonWidget friends = this.addDrawableChild(ButtonWidget.builder(Text.translatable("api.friends"), buttonx ->
			this.client.setScreen(new FriendsScreen(this))).width(100).build());
		ButtonWidget editButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.edit"), buttonx -> {
		}).width(74).build());
		editButton.active = false;
		ButtonWidget deleteButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.delete"), buttonx -> {
		}).width(74).build());
		deleteButton.active = false;
		ButtonWidget refreshList = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("selectServer.refresh"), buttonx -> this.refreshServerList()).width(74).build()
		);
		ButtonWidget back = this.addDrawableChild(ButtonWidget.builder(CommonTexts.BACK, buttonx -> this.closeScreen()).width(74).build());
		GridWidget gridWidget = new GridWidget();
		GridWidget.AdditionHelper additionHelper = gridWidget.createAdditionHelper(1);
		LinearLayoutWidget equalSpacingLayout = additionHelper.add(new LinearLayoutWidget(308, 20, LinearLayoutWidget.Orientation.HORIZONTAL));
		equalSpacingLayout.addChild(this.selectButton);
		equalSpacingLayout.addChild(directConnect);
		equalSpacingLayout.addChild(friends);
		additionHelper.add(SpacerWidget.withHeight(4));
		LinearLayoutWidget equalSpacingLayout2 = additionHelper.add(new LinearLayoutWidget(308, 20, LinearLayoutWidget.Orientation.HORIZONTAL));
		equalSpacingLayout2.addChild(editButton);
		equalSpacingLayout2.addChild(deleteButton);
		equalSpacingLayout2.addChild(refreshList);
		equalSpacingLayout2.addChild(back);
		gridWidget.arrangeElements();
		FrameWidget.align(gridWidget, 0, this.height - 64, this.width, 64);
		this.onSelectedChange();
	}

	@Override
	public void closeScreen() {
		this.client.setScreen(this.lastScreen);
	}

	@Override
	public void tick() {
		super.tick();
		this.pinger.tick();
	}

	@Override
	public void removed() {
		this.pinger.cancel();
		StatusUpdateHandler.removeUpdateListener("friends_multiplayer_screen");
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredShadowedText(textRenderer, getTitle(), width / 2, 15, -1);

		if (serverSelectionList.children().isEmpty()) {
			guiGraphics.drawCenteredShadowedText(textRenderer, NO_ONLINE_FRIENDS, width / 2, height / 2 - textRenderer.fontHeight / 2, -1);
		}
	}

	private void refreshServerList() {
		this.client.setScreen(new FriendsMultiplayerScreen(this.lastScreen));
	}

	private void directJoinCallback(boolean confirmed) {
		if (confirmed) {
			ServerList servers = new ServerList(client);
			servers.loadFile();
			ServerInfo serverData = servers.get(this.editingServer.address);
			if (serverData == null) {
				servers.add(this.editingServer, true);
				servers.saveFile();
				this.join(this.editingServer);
			} else {
				this.join(serverData);
			}
		} else {
			this.client.setScreen(this);
		}
	}

	public void joinSelectedServer() {
		FriendsMultiplayerSelectionList.Entry entry = this.serverSelectionList.getSelectedOrNull();
		this.join(entry.getServerData());
	}

	private void join(ServerInfo server) {
		if (server == null) {
			return;
		}
		ConnectScreen.connect(this, this.client, ServerAddress.parse(server.address), server, false);
	}

	public void setSelected(FriendsMultiplayerSelectionList.Entry selected) {
		this.serverSelectionList.setSelected(selected);
		this.onSelectedChange();
	}

	protected void onSelectedChange() {
		this.selectButton.active = false;
		FriendsMultiplayerSelectionList.Entry entry = this.serverSelectionList.getSelectedOrNull();
		if (entry != null && !(entry instanceof FriendsMultiplayerSelectionList.LoadingHeader)) {
			this.selectButton.active = entry.canJoin();
		}
	}

	@Override
	public void setDeferredTooltip(Text text) {
		super.setDeferredTooltip(text);
	}
}
