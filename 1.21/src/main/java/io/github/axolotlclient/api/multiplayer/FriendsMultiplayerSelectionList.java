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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.texture.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.axolotlclient.api.types.PkSystem;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.modules.auth.Auth;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.PlayerFaceRenderer;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.widget.list.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.FaviconTexture;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FriendsMultiplayerSelectionList extends AlwaysSelectedEntryListWidget<FriendsMultiplayerSelectionList.Entry> {
	static final Identifier INCOMPATIBLE_SPRITE = Identifier.ofDefault("server_list/incompatible");
	static final Identifier UNREACHABLE_SPRITE = Identifier.ofDefault("server_list/unreachable");
	static final Identifier PING_1_SPRITE = Identifier.ofDefault("server_list/ping_1");
	static final Identifier PING_2_SPRITE = Identifier.ofDefault("server_list/ping_2");
	static final Identifier PING_3_SPRITE = Identifier.ofDefault("server_list/ping_3");
	static final Identifier PING_4_SPRITE = Identifier.ofDefault("server_list/ping_4");
	static final Identifier PING_5_SPRITE = Identifier.ofDefault("server_list/ping_5");
	static final Identifier PINGING_1_SPRITE = Identifier.ofDefault("server_list/pinging_1");
	static final Identifier PINGING_2_SPRITE = Identifier.ofDefault("server_list/pinging_2");
	static final Identifier PINGING_3_SPRITE = Identifier.ofDefault("server_list/pinging_3");
	static final Identifier PINGING_4_SPRITE = Identifier.ofDefault("server_list/pinging_4");
	static final Identifier PINGING_5_SPRITE = Identifier.ofDefault("server_list/pinging_5");
	static final Identifier JOIN_HIGHLIGHTED_SPRITE = Identifier.ofDefault("server_list/join_highlighted");
	static final Identifier JOIN_SPRITE = Identifier.ofDefault("server_list/join");
	static final Logger LOGGER = LogUtils.getLogger();
	static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(
		5,
		new ThreadFactoryBuilder()
			.setNameFormat("Friends Server Pinger #%d")
			.setDaemon(true)
			.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER))
			.build()
	);

	static final Text CANT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").setColor(-65536);
	static final Text CANT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").setColor(-65536);
	static final Text INCOMPATIBLE_STATUS = Text.translatable("multiplayer.status.incompatible");
	static final Text NO_CONNECTION_STATUS = Text.translatable("multiplayer.status.no_connection");
	static final Text PINGING_STATUS = Text.translatable("multiplayer.status.pinging");
	static final Text ONLINE_STATUS = Text.translatable("multiplayer.status.online");
	static final Text NOT_PUBLISHED_STATUS = Text.translatable("api.worldhost.joinability.not_published").formatted(Formatting.RED);
	private final FriendsMultiplayerScreen screen;
	private final List<Entry> friendEntries = new ArrayList<>();
	private final LoadingHeader loadingHeader = new LoadingHeader();

	public FriendsMultiplayerSelectionList(FriendsMultiplayerScreen screen, MinecraftClient minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
		this.screen = screen;
		addEntry(loadingHeader);
	}

	private void refreshEntries() {
		this.clearEntries();
		this.friendEntries.forEach(this::addEntry);
	}

	public void setSelected(@Nullable FriendsMultiplayerSelectionList.Entry entry) {
		super.setSelected(entry);
		this.screen.onSelectedChange();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		Entry entry = this.getSelectedOrNull();
		return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void updateList(List<User> friends) {
		this.friendEntries.clear();

		for (User friend : friends) {
			if (friend.getStatus().isOnline()) {
				this.friendEntries.add(createEntry(friend));
			}
		}

		this.refreshEntries();
	}

	private Entry createEntry(User friend) {
		if (friend.getStatus().getActivity() != null && friend.getStatus().getActivity().hasMetadata()) {
			if (friend.getStatus().getActivity().hasMetadata(Status.Activity.ExternalServerMetadata.ID)) {
				return externalServerEntry(this.screen, friend);
			} else {
				return e4mcServerFriendEntry(this.screen, friend);
			}
		}
		return new StatusFriendEntry(screen, friend);
	}

	public void updateEntry(User user) {
		this.friendEntries.stream().filter(e1 -> {
			if (e1 instanceof StatusFriendEntry statusFriendEntry) {
				return statusFriendEntry.getUser().equals(user);
			} else if (e1 instanceof ServerEntry serverEntry) {
				return serverEntry.getUser().equals(user);
			}
			return false;
		}).findFirst().ifPresent(e -> {
			this.friendEntries.set(friendEntries.indexOf(e), createEntry(user));
			refreshEntries();
		});
	}

	@Override
	public int getRowWidth() {
		return 305;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
		public void close() {
		}

		public boolean canJoin() {
			return false;
		}

		public ServerInfo getServerData() {
			return null;
		}
	}

	@Getter
	public class StatusFriendEntry extends Entry {

		private final User user;

		protected StatusFriendEntry(final FriendsMultiplayerScreen screen, final User friend) {
			this.user = friend;
		}

		@Override
		public Text getNarration() {
			return Text.literal(user.getName());
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (user.isSystem()) {
				MutableText fronters = Text.literal(
					user.getSystem().getFronters().stream().map(PkSystem.Member::getDisplayName)
						.collect(Collectors.joining("/")));
				Text tag = Text.literal("(" + user.getSystem().getName() + "/" + user.getName() + ")")
					.setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY));
				graphics.drawText(client.textRenderer, fronters.append(tag), left + 3, top + 1, -1, false);
			} else {
				graphics.drawText(client.textRenderer, user.getName(), left + 3 + 32, top + 1, -1, false);
			}

			if (user.getStatus().isOnline() && user.getStatus().getActivity() != null) {
				graphics.drawText(client.textRenderer, user.getStatus().getTitle(), left + 3 + 32, top + 12, 8421504, false);
				graphics.drawText(client.textRenderer, user.getStatus().getDescription(), left + 3 + 40, top + 23, 8421504, false);
			} else if (user.getStatus().getLastOnline() != null) {
				graphics.drawText(client.textRenderer, user.getStatus().getLastOnline(), left + 3 + 32, top + 12, 8421504, false);
			}

			Identifier texture = Auth.getInstance().getSkinTexture(user);
			PlayerFaceRenderer.draw(graphics, texture, left - 1, top - 1, 32, true, false);
		}
	}

	protected class ServerEntry extends Entry {
		private static final int ICON_WIDTH = 32;
		private static final int ICON_HEIGHT = 32;
		private static final int SPACING = 5;
		private static final int STATUS_ICON_WIDTH = 10;
		private static final int STATUS_ICON_HEIGHT = 8;
		private final FriendsMultiplayerScreen screen;
		private final MinecraftClient minecraft;
		protected final ServerInfo serverData;
		private final FaviconTexture icon;
		private byte @Nullable [] lastIconBytes;
		private long lastClickTime;
		@Nullable
		private List<Text> onlinePlayersTooltip;
		@Nullable
		private Identifier statusIcon;
		@Nullable
		private Text statusIconTooltip;
		@Getter
		protected final User user;

		protected ServerEntry(FriendsMultiplayerScreen screen, ServerInfo serverData, User user) {
			this.screen = screen;
			this.minecraft = MinecraftClient.getInstance();
			this.serverData = serverData;
			this.icon = FaviconTexture.createServerFaviconTexture(minecraft.getTextureManager(), serverData.address != null ? serverData.address : user.getUuid() + "_" + serverData.name);
			this.user = user;
		}


		protected void refreshStatus() {
			this.onlinePlayersTooltip = null;
			if (!isPublished()) {
				this.serverData.setPingResult(ServerInfo.PingResult.UNREACHABLE);
			}
			switch (this.serverData.getPingResult()) {
				case INITIAL:
				case PINGING:
					this.statusIcon = FriendsMultiplayerSelectionList.PING_1_SPRITE;
					this.statusIconTooltip = FriendsMultiplayerSelectionList.PINGING_STATUS;
					break;
				case INCOMPATIBLE:
					this.statusIcon = FriendsMultiplayerSelectionList.INCOMPATIBLE_SPRITE;
					this.onlinePlayersTooltip = this.serverData.playerListSummary;
					this.statusIconTooltip = FriendsMultiplayerSelectionList.INCOMPATIBLE_STATUS;
					break;
				case UNREACHABLE:
					this.statusIcon = FriendsMultiplayerSelectionList.UNREACHABLE_SPRITE;
					if (!isPublished()) {
						break;
					}
					this.statusIconTooltip = FriendsMultiplayerSelectionList.NO_CONNECTION_STATUS;
					break;
				case SUCCESSFUL:
					if (this.serverData.ping < 150L) {
						this.statusIcon = FriendsMultiplayerSelectionList.PING_5_SPRITE;
					} else if (this.serverData.ping < 300L) {
						this.statusIcon = FriendsMultiplayerSelectionList.PING_4_SPRITE;
					} else if (this.serverData.ping < 600L) {
						this.statusIcon = FriendsMultiplayerSelectionList.PING_3_SPRITE;
					} else if (this.serverData.ping < 1000L) {
						this.statusIcon = FriendsMultiplayerSelectionList.PING_2_SPRITE;
					} else {
						this.statusIcon = FriendsMultiplayerSelectionList.PING_1_SPRITE;
					}

					this.statusIconTooltip = Text.translatable("multiplayer.status.ping", this.serverData.ping);
					this.onlinePlayersTooltip = this.serverData.playerListSummary;
			}
		}

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (this.serverData.getPingResult() == ServerInfo.PingResult.INITIAL) {
				this.serverData.setPingResult(ServerInfo.PingResult.PINGING);
				this.serverData.label = CommonTexts.EMPTY;
				this.serverData.playerCountLabel = CommonTexts.EMPTY;
				FriendsMultiplayerSelectionList.THREAD_POOL
					.submit(
						() -> {
							try {
								this.screen
									.getPinger()
									.add(
										this.serverData,
										() -> {
										},
										() -> {
											this.serverData
												.setPingResult(
													this.serverData.protocolVersion == SharedConstants.getGameVersion().getProtocolVersion() ? ServerInfo.PingResult.SUCCESSFUL : ServerInfo.PingResult.INCOMPATIBLE
												);
											this.minecraft.execute(this::refreshStatus);
										}
									);
							} catch (UnknownHostException var2) {
								this.serverData.setPingResult(ServerInfo.PingResult.UNREACHABLE);
								this.serverData.label = FriendsMultiplayerSelectionList.CANT_RESOLVE_TEXT;
								this.minecraft.execute(this::refreshStatus);
							} catch (Exception var3) {
								this.serverData.setPingResult(ServerInfo.PingResult.UNREACHABLE);
								this.serverData.label = FriendsMultiplayerSelectionList.CANT_CONNECT_TEXT;
								this.minecraft.execute(this::refreshStatus);
							}
						}
					);
			}

			guiGraphics.drawShadowedText(this.minecraft.textRenderer, this.serverData.name, left + ICON_WIDTH + 3, top + 1, -1);
			List<OrderedText> list = this.minecraft.textRenderer.wrapLines(this.serverData.label, width - ICON_WIDTH - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				guiGraphics.drawShadowedText(this.minecraft.textRenderer, list.get(i), left + ICON_WIDTH + 3, top + 12 + 9 * i, -8355712);
			}

			guiGraphics.drawTexture(this.icon.getTextureId(), left, top, 0.0F, 0.0F, 32, 32, 32, 32);
			Identifier texture = Auth.getInstance().getSkinTexture(user);
			PlayerFaceRenderer.draw(guiGraphics, texture, left + ICON_WIDTH - 10, top + ICON_HEIGHT - 10, 10, true, false);
			if (this.serverData.getPingResult() == ServerInfo.PingResult.PINGING) {
				int i = (int) (Util.getMeasuringTimeMs() / 100L + index * 2 & 7L);
				if (i > 4) {
					i = 8 - i;
				}
				this.statusIcon = switch (i) {
					case 1 -> FriendsMultiplayerSelectionList.PINGING_2_SPRITE;
					case 2 -> FriendsMultiplayerSelectionList.PINGING_3_SPRITE;
					case 3 -> FriendsMultiplayerSelectionList.PINGING_4_SPRITE;
					case 4 -> FriendsMultiplayerSelectionList.PINGING_5_SPRITE;
					default -> FriendsMultiplayerSelectionList.PINGING_1_SPRITE;
				};
			}

			int i = left + width - STATUS_ICON_WIDTH - SPACING;
			if (this.statusIcon != null) {
				guiGraphics.drawGuiTexture(this.statusIcon, i, top, STATUS_ICON_WIDTH, STATUS_ICON_HEIGHT);
			}

			byte[] bs = this.serverData.getFavicon();
			if (!Arrays.equals(bs, this.lastIconBytes)) {
				if (this.uploadIcon(bs)) {
					this.lastIconBytes = bs;
				} else {
					this.serverData.setFavicon(null);
				}
			}

			Text component;
			if (!isPublished()) {
				component = NOT_PUBLISHED_STATUS;
			} else {
				if (this.serverData.getPingResult() == ServerInfo.PingResult.INCOMPATIBLE) {
					component = this.serverData.version.copy().formatted(Formatting.RED);
				} else {
					component = this.serverData.playerCountLabel;
				}
			}
			int j = this.minecraft.textRenderer.getWidth(component);
			int k = i - j - SPACING;
			guiGraphics.drawShadowedText(this.minecraft.textRenderer, component, k, top + 1, -8355712);
			if (this.statusIconTooltip != null && mouseX >= i && mouseX <= i + STATUS_ICON_WIDTH && mouseY >= top && mouseY <= top + STATUS_ICON_HEIGHT) {
				this.screen.setDeferredTooltip(this.statusIconTooltip);
			} else if (this.onlinePlayersTooltip != null && mouseX >= k && mouseX <= k + j && mouseY >= top && mouseY <= top - 1 + 9) {
				this.screen.setDeferredTooltip(Lists.transform(this.onlinePlayersTooltip, Text::asOrderedText));
			}

			if (this.minecraft.options.getTouchscreen().get() || hovering) {
				int l = mouseX - left;
				int m = mouseY - top;
				if (this.canJoin()) {
					guiGraphics.fill(left, top, left + ICON_WIDTH, top + ICON_HEIGHT, -1601138544);
					if (l < ICON_WIDTH && l > ICON_WIDTH / 2) {
						guiGraphics.drawGuiTexture(FriendsMultiplayerSelectionList.JOIN_HIGHLIGHTED_SPRITE, left, top, ICON_WIDTH, ICON_HEIGHT);
					} else {
						guiGraphics.drawGuiTexture(FriendsMultiplayerSelectionList.JOIN_SPRITE, left, top, ICON_WIDTH, ICON_HEIGHT);
					}
				}
			}
		}

		protected boolean isPublished() {
			return true;
		}

		@Override
		public boolean canJoin() {
			return serverData.getPingResult() == ServerInfo.PingResult.SUCCESSFUL && isPublished();
		}

		private boolean uploadIcon(byte @Nullable [] iconBytes) {
			if (iconBytes == null) {
				this.icon.clear();
			} else {
				try {
					this.icon.upload(NativeImage.read(iconBytes));
				} catch (Throwable var3) {
					FriendsMultiplayerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.address, var3);
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d = mouseX - FriendsMultiplayerSelectionList.this.getRowLeft();
			double e = mouseY - FriendsMultiplayerSelectionList.this.getRowTop(FriendsMultiplayerSelectionList.this.children().indexOf(this));
			if (d <= 32.0) {
				if (d < 32.0 && d > 16.0 && this.canJoin()) {
					this.screen.setSelected(this);
					this.screen.joinSelectedServer();
					return true;
				}
			}

			this.screen.setSelected(this);
			if (Util.getMeasuringTimeMs() - this.lastClickTime < 250L && canJoin()) {
				this.screen.joinSelectedServer();
			}

			this.lastClickTime = Util.getMeasuringTimeMs();
			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public @NotNull Text getNarration() {
			MutableText mutableComponent = Text.empty();
			mutableComponent.append(Text.translatable("narrator.select", this.serverData.name));
			mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
			switch (this.serverData.getPingResult()) {
				case PINGING:
					mutableComponent.append(FriendsMultiplayerSelectionList.PINGING_STATUS);
					break;
				case INCOMPATIBLE:
					mutableComponent.append(FriendsMultiplayerSelectionList.INCOMPATIBLE_STATUS);
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.version.narration", this.serverData.version));
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.motd.narration", this.serverData.label));
					break;
				case UNREACHABLE:
					mutableComponent.append(FriendsMultiplayerSelectionList.NO_CONNECTION_STATUS);
					break;
				default:
					mutableComponent.append(FriendsMultiplayerSelectionList.ONLINE_STATUS);
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.ping.narration", this.serverData.ping));
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.motd.narration", this.serverData.label));
					if (this.serverData.players != null) {
						mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
						mutableComponent.append(
							Text.translatable("multiplayer.status.player_count.narration", this.serverData.players.online(), this.serverData.players.max())
						);
						mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
						mutableComponent.append(Texts.join(this.serverData.playerListSummary, Text.literal(", ")));
					}
			}

			return mutableComponent;
		}

		@Override
		public void close() {
			this.icon.close();
		}
	}

	private ExternalServerFriendEntry externalServerEntry(FriendsMultiplayerScreen screen, User friend) {
		Status.Activity.ExternalServerMetadata metadata = (Status.Activity.ExternalServerMetadata) friend.getStatus().getActivity().metadata().attributes();
		return new ExternalServerFriendEntry(screen, metadata, new ServerInfo(metadata.serverName(), metadata.address(), ServerInfo.ServerType.OTHER), friend);
	}

	public class ExternalServerFriendEntry extends ServerEntry {
		private final Status.Activity.ExternalServerMetadata statusDescription;

		private ExternalServerFriendEntry(FriendsMultiplayerScreen screen, Status.Activity.ExternalServerMetadata statusDescription, ServerInfo serverData, User friend) {
			super(screen, serverData, friend);
			this.statusDescription = statusDescription;
			refreshStatus();
		}

		@Override
		public boolean canJoin() {
			return statusDescription.address() != null;
		}

	}

	private E4mcServerFriendEntry e4mcServerFriendEntry(FriendsMultiplayerScreen screen, User friend) {
		var activity = friend.getStatus().getActivity();
		Status.Activity.E4mcMetadata metadata;
		if (activity.hasMetadata(Status.Activity.WorldHostMetadata.ID)) {
			metadata = ((Status.Activity.WorldHostMetadata) activity.metadata().attributes()).asE4mcMetadata();
		} else {
			metadata = (Status.Activity.E4mcMetadata) activity.metadata().attributes();
		}
		return new E4mcServerFriendEntry(screen, metadata, ServerInfoUtil.getServerData(friend.getName(), metadata), friend);
	}

	public class E4mcServerFriendEntry extends ServerEntry {

		private final Status.Activity.E4mcMetadata statusDescription;

		protected E4mcServerFriendEntry(FriendsMultiplayerScreen screen, Status.Activity.E4mcMetadata statusDescription, ServerInfo serverData, User friend) {
			super(screen, serverData, friend);
			this.statusDescription = statusDescription;
			refreshStatus();
		}

		@Override
		protected boolean isPublished() {
			return statusDescription.domain() != null;
		}

		@Override
		protected void refreshStatus() {
			super.refreshStatus();
			serverData.label = Text.of(statusDescription.serverInfo().levelName());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LoadingHeader extends Entry {
		private final MinecraftClient minecraft = MinecraftClient.getInstance();

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			int i = top + height / 2 - 9 / 2;

			String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
			guiGraphics.drawShadowedText(this.minecraft.textRenderer, string, this.minecraft.currentScreen.width / 2 - this.minecraft.textRenderer.getWidth(string) / 2, i, -8355712);
		}

		@Override
		public @NotNull Text getNarration() {
			return Text.empty();
		}
	}
}
