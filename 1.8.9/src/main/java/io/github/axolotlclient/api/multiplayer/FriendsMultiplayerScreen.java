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

import java.util.Collections;
import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.ButtonWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.api.FriendsScreen;
import io.github.axolotlclient.api.handlers.StatusUpdateHandler;
import io.github.axolotlclient.api.requests.FriendRequest;
import lombok.Getter;
import net.minecraft.client.gui.screen.ConfirmationListener;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.input.Keyboard;

public class FriendsMultiplayerScreen extends io.github.axolotlclient.AxolotlClientConfig.impl.ui.Screen implements ConfirmationListener {
	@Getter
	private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
	private final Screen lastScreen;
	protected FriendsMultiplayerSelectionList serverSelectionList;
	private ButtonWidget selectButton;
	private ServerListEntry editingServer;
	private boolean initialized;
	private List<String> tooltipText;
	private final ButtonWidget friendsCountButton = new VanillaButtonWidget(0, 0, 150, 20, I18n.translate("api.servers.friends", "..."), button -> {
	});
	private boolean directConnectDialog = false;
	private static final String NO_ONLINE_FRIENDS = I18n.translate("api.servers.friends.no_online_friends");

	public FriendsMultiplayerScreen(Screen lastScreen) {
		super(I18n.translate("api.servers.friends.title"));
		this.lastScreen = lastScreen;
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
		if (this.initialized) {
			this.serverSelectionList.updateSize(this.width, this.height, 60, this.height - 64);
		} else {
			this.serverSelectionList = new FriendsMultiplayerSelectionList(this, this.minecraft, this.width, this.height - 64 - 60, 60, 36);
		}
		StatusUpdateHandler.addUpdateListener("friends_multiplayer_screen", serverSelectionList::updateEntry);
		this.addDrawableChild(this.serverSelectionList);
		addDrawableChild(new VanillaButtonWidget(this.width / 2 - 102, 32, 100, 20, I18n.translate("api.servers"), button ->
			minecraft.openScreen(new MultiplayerScreen(lastScreen))));
		addDrawableChild(friendsCountButton);
		friendsCountButton.setX(width / 2 + 2);
		friendsCountButton.setY(32);
		friendsCountButton.setWidth(100);
		friendsCountButton.active = false;

		if (!initialized) {
			initialized = true;
			FriendRequest.getInstance().getFriends().thenAccept(friends -> {
				friendsCountButton.setMessage(I18n.translate("api.servers.friends", friends.stream().filter(u -> u.getStatus().isOnline()).count()));
				this.serverSelectionList.updateList(friends);
			});
		}

		this.selectButton = this.addDrawableChild(
			new VanillaButtonWidget(width / 2 - 154, height - 64 + 12, 100, 20,
				I18n.translate("selectServer.select"), buttonx -> this.joinSelectedServer()));
		this.addDrawableChild(new VanillaButtonWidget(width / 2 - 50, height - 64 + 12, 100, 20,
			I18n.translate("selectServer.direct"), buttonx -> {
			directConnectDialog = true;
			this.editingServer = new ServerListEntry(I18n.translate("selectServer.defaultName"), "", false);
			this.minecraft.openScreen(new DirectConnectScreen(this, this.editingServer));
		}));
		this.addDrawableChild(new VanillaButtonWidget(width / 2 + 50 + 4, height - 64 + 12, 100, 20,
			I18n.translate("api.friends"), buttonx ->
			this.minecraft.openScreen(new FriendsScreen(this))));
		ButtonWidget editButton = this.addDrawableChild(new VanillaButtonWidget(width / 2 - 154, height - 64 + 12 + 20 + 4, 74, 20,
			I18n.translate("selectServer.edit"), buttonx -> {
		}));
		editButton.active = false;
		ButtonWidget deleteButton = this.addDrawableChild(new VanillaButtonWidget(width / 2 - 76, height - 64 + 12 + 20 + 4, 74, 20,
			I18n.translate("selectServer.delete"), buttonx -> {
		}));
		deleteButton.active = false;
		this.addDrawableChild(
			new VanillaButtonWidget(width / 2 + 2, height - 64 + 12 + 20 + 4, 74, 20,
				I18n.translate("selectServer.refresh"), buttonx -> this.refreshServerList()));
		this.addDrawableChild(new VanillaButtonWidget(width / 2 + 80, height - 64 + 12 + 20 + 4, 74, 20,
			I18n.translate("gui.back"), buttonx -> this.minecraft.openScreen(this.lastScreen)));

		this.onSelectedChange();
	}

	@Override
	public void tick() {
		super.tick();
		this.pinger.tick();
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
		this.pinger.cancel();
		StatusUpdateHandler.removeUpdateListener("friends_multiplayer_screen");
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.tooltipText = null;
		super.render(mouseX, mouseY, partialTick);
		drawCenteredString(textRenderer, getTitle(), width / 2, 15, -1);

		if (serverSelectionList.children().isEmpty()) {
			drawCenteredString(textRenderer, NO_ONLINE_FRIENDS, width / 2, height / 2 - textRenderer.fontHeight / 2, -1);
		}
		if (this.tooltipText != null) {
			this.renderTooltip(this.tooltipText, mouseX, mouseY);
		}
	}

	private void refreshServerList() {
		this.minecraft.openScreen(new FriendsMultiplayerScreen(this.lastScreen));
	}

	private void directJoinCallback(boolean confirmed) {
		if (confirmed) {
			this.join(this.editingServer);
		} else {
			this.minecraft.openScreen(this);
		}
	}

	public void joinSelectedServer() {
		FriendsMultiplayerSelectionList.Entry entry = this.serverSelectionList.getSelectedOrNull();
		this.join(entry.getServerData());
	}

	private void join(ServerListEntry server) {
		if (server == null) {
			return;
		}
		this.minecraft.openScreen(new ConnectScreen(this, this.minecraft, server));
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

	public void setDeferredTooltip(List<String> text) {
		this.tooltipText = text;
	}

	public void setDeferredTooltip(String text) {
		if (text != null && !text.isEmpty()) {
			this.tooltipText = Collections.singletonList(text);
		}
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (this.directConnectDialog) {
			this.directConnectDialog = false;
			directJoinCallback(bl);
		}
	}
}
