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
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.axolotlclient.api.e4mc.E4mcStatusDescription;
import io.github.axolotlclient.api.requests.StatusUpdate;
import io.github.axolotlclient.api.types.PkSystem;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.util.GsonHelper;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.PlayerFaceRenderer;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
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
	static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
	static final Identifier ICONS = new Identifier("textures/gui/icons.png");
	static final Logger LOGGER = LogUtils.getLogger();
	static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(
		5,
		new ThreadFactoryBuilder()
			.setNameFormat("Friends Server Pinger #%d")
			.setDaemon(true)
			.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER))
			.build()
	);
	static final Text CANT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").styled(style -> style.withColor(-65536));
	static final Text CANT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").styled(style -> style.withColor(-65536));
	static final Text INCOMPATIBLE_STATUS = Text.translatable("multiplayer.status.incompatible");
	static final Text NO_CONNECTION_STATUS = Text.translatable("multiplayer.status.no_connection");
	static final Text PINGING_STATUS = Text.translatable("multiplayer.status.pinging");
	static final Text ONLINE_STATUS = Text.translatable("multiplayer.status.online");
	static final Text NOT_PUBLISHED_STATUS = Text.translatable("api.worldhost.joinability.not_published").formatted(Formatting.RED);
	private final FriendsMultiplayerScreen screen;
	private final List<Entry> friendEntries = new ArrayList<>();
	private final LoadingHeader loadingHeader = new LoadingHeader();

	public FriendsMultiplayerSelectionList(FriendsMultiplayerScreen screen, MinecraftClient minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, screen.width, screen.height, y, y + height, itemHeight);
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
				if (friend.getStatus().getActivity().title().startsWith(StatusUpdate.SPECIAL_STATUS_PREFIX)) {
					if (StatusUpdate.E4MC_STATUS_TITLE.equals(friend.getStatus().getActivity().title())) {
						this.friendEntries.add(new E4mcFriendServerEntry(this.screen, friend));
					} else {
						this.friendEntries.add(new ExternalServerFriendEntry(this.screen, friend));
					}
				} else {
					this.friendEntries.add(new StatusFriendEntry(screen, friend));
				}
			}
		}

		this.refreshEntries();
	}

	@Override
	public int getRowWidth() {
		return 305;
	}

	public void removed() {
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
				graphics.drawText(client.textRenderer, user.getName(), left + 3 + 33, top + 1, -1, false);
			}

			if (user.getStatus().isOnline() && user.getStatus().getActivity() != null) {
				graphics.drawText(client.textRenderer, user.getStatus().getTitle(), left + 3 + 33, top + 12, 8421504, false);
				graphics.drawText(client.textRenderer, user.getStatus().getDescription(), left + 3 + 40, top + 23, 8421504, false);
			} else if (user.getStatus().getLastOnline() != null) {
				graphics.drawText(client.textRenderer, user.getStatus().getLastOnline(), left + 3 + 33, top + 12, 8421504, false);
			}

			Identifier texture = Auth.getInstance().getSkinTexture(user.getUuid(), user.getName());
			PlayerFaceRenderer.draw(graphics, texture, left - 1, top - 1, 33, true, false);
		}

	}

	private static final int STATUS_ICON_HEIGHT = 8;
	private static final int STATUS_ICON_WIDTH = 10;

	public class ExternalServerFriendEntry extends Entry {
		private static final int ICON_WIDTH = 32;
		private static final int ICON_HEIGHT = 32;
		private static final int SPACING = 5;
		private final FriendsMultiplayerScreen screen;
		private final MinecraftClient minecraft;
		protected final ServerInfoEx serverData;
		private final FaviconTexture icon;
		private byte @Nullable [] lastIconBytes;
		private long lastClickTime;
		@Nullable
		private List<Text> onlinePlayersTooltip;
		@Nullable
		private Sprite statusIcon;
		@Nullable
		private Text statusIconTooltip;
		private final StatusDescription statusDescription;
		private final User friend;


		protected ExternalServerFriendEntry(final FriendsMultiplayerScreen screen, final User friend) {
			this.screen = screen;
			this.statusDescription = StatusDescription.read(friend.getStatus().getActivity().rawDescription());
			this.friend = friend;
			serverData = new ServerInfoEx(new ServerInfo(statusDescription.serverName(), statusDescription.serverIp(), false));

			this.minecraft = MinecraftClient.getInstance();
			this.icon = FaviconTexture.createServerFaviconTexture(this.minecraft.getTextureManager(), serverData.serverInfo.address);
			this.refreshStatus();
		}

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (serverData.pingResult == PingResult.INITIAL) {
				serverData.pingResult = PingResult.PINGING;
				this.serverData.serverInfo.ping = -2L;
				this.serverData.serverInfo.label = CommonTexts.EMPTY;
				this.serverData.serverInfo.playerCountLabel = CommonTexts.EMPTY;
				FriendsMultiplayerSelectionList.THREAD_POOL.submit(() -> {
					try {
						this.screen.getPinger().add(this.serverData.serverInfo, () -> {
						});
					} catch (UnknownHostException var2) {
						this.serverData.setPingResult(PingResult.UNREACHABLE);
						this.serverData.serverInfo.label = FriendsMultiplayerSelectionList.CANT_RESOLVE_TEXT;
						this.minecraft.execute(this::refreshStatus);
					} catch (Exception var3) {
						this.serverData.setPingResult(PingResult.UNREACHABLE);
						this.serverData.serverInfo.label = FriendsMultiplayerSelectionList.CANT_CONNECT_TEXT;
						this.minecraft.execute(this::refreshStatus);
					}
				});
			}
			if (serverData.pingResult == PingResult.PINGING && serverData.serverInfo.ping != -2) {
				this.serverData.setPingResult(
					this.serverData.serverInfo.protocolVersion == SharedConstants.getGameVersion().getProtocolVersion() ? PingResult.SUCCESSFUL : PingResult.INCOMPATIBLE
				);
				if (friend.isSystem()) {
					serverData.serverInfo.name = friend.getSystem().getFronters().stream().map(PkSystem.Member::getDisplayName)
						.collect(Collectors.joining("/")) + "(" + friend.getSystem().getName() + "/" + friend.getName() + ")";
				} else {
					serverData.serverInfo.name = friend.getName();
				}
			}
			refreshStatus();

			guiGraphics.drawShadowedText(this.minecraft.textRenderer, this.serverData.serverInfo.name, left + ICON_WIDTH + 3, top + 1, -1);
			List<OrderedText> list = this.minecraft.textRenderer.wrapLines(this.serverData.serverInfo.label, width - ICON_WIDTH - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				guiGraphics.drawShadowedText(this.minecraft.textRenderer, list.get(i), left + ICON_WIDTH + 3, top + 12 + 9 * i, -8355712);
			}

			this.drawIcon(guiGraphics, left, top, this.icon.getTextureId());
			if (this.serverData.pingResult() == PingResult.PINGING) {
				int i = (int) (Util.getMeasuringTimeMs() / 100L + index * 2 & 7L);
				if (i > 4) {
					i = 8 - i;
				}
				this.statusIcon = switch (i) {
					case 1 -> Sprite.PINGING_2_SPRITE;
					case 2 -> Sprite.PINGING_3_SPRITE;
					case 3 -> Sprite.PINGING_4_SPRITE;
					case 4 -> Sprite.PINGING_5_SPRITE;
					default -> Sprite.PINGING_1_SPRITE;
				};
			}

			int i = left + width - 10 - 5;
			if (this.statusIcon != null) {
				statusIcon.draw(guiGraphics, i, top);
			}

			byte[] bs = this.serverData.serverInfo.getFavicon();
			if (!Arrays.equals(bs, this.lastIconBytes)) {
				if (this.uploadIcon(bs)) {
					this.lastIconBytes = bs;
				} else {
					this.serverData.serverInfo.setFavicon(null);
					this.updateServerList();
				}
			}

			Text component;

			if (this.serverData.pingResult() == PingResult.INCOMPATIBLE) {
				component = this.serverData.serverInfo.version.copy().formatted(Formatting.RED);
			} else {
				component = this.serverData.serverInfo.playerCountLabel;
			}
			int j = this.minecraft.textRenderer.getWidth(component);
			int k = i - j - 5;
			guiGraphics.drawShadowedText(this.minecraft.textRenderer, component, k, top + 1, -8355712);
			if (this.statusIconTooltip != null && mouseX >= i && mouseX <= i + STATUS_ICON_WIDTH && mouseY >= top && mouseY <= top + STATUS_ICON_HEIGHT) {
				this.screen.setDeferredTooltip(this.statusIconTooltip);
			} else if (this.onlinePlayersTooltip != null && mouseX >= k && mouseX <= k + j && mouseY >= top && mouseY <= top - 1 + 9) {
				this.screen.setDeferredTooltip(Lists.transform(this.onlinePlayersTooltip, Text::asOrderedText));
			}

			if (this.minecraft.options.getTouchscreen().get() || hovering) {
				guiGraphics.fill(left, top, left + ICON_WIDTH, top + ICON_HEIGHT, -1601138544);
				int l = mouseX - left;
				int m = mouseY - top;
				if (this.canJoin()) {
					if (l < 32 && l > 16) {
						Sprite.JOIN_HIGHLIGHTED_SPRITE.draw(guiGraphics, left, top);
					} else {
						Sprite.JOIN_SPRITE.draw(guiGraphics, left, top);
					}
				}
			}
		}

		private void refreshStatus() {
			this.onlinePlayersTooltip = null;

			switch (this.serverData.pingResult()) {
				case INITIAL:
				case PINGING:
					this.statusIcon = Sprite.PING_1_SPRITE;
					this.statusIconTooltip = FriendsMultiplayerSelectionList.PINGING_STATUS;
					break;
				case INCOMPATIBLE:
					this.statusIcon = Sprite.INCOMPATIBLE_SPRITE;
					this.onlinePlayersTooltip = this.serverData.serverInfo.playerListSummary;
					this.statusIconTooltip = FriendsMultiplayerSelectionList.INCOMPATIBLE_STATUS;
					break;
				case UNREACHABLE:
					this.statusIcon = Sprite.UNREACHABLE_SPRITE;

					this.statusIconTooltip = FriendsMultiplayerSelectionList.NO_CONNECTION_STATUS;
					break;
				case SUCCESSFUL:
					if (this.serverData.serverInfo.ping < 150L) {
						this.statusIcon = Sprite.PING_5_SPRITE;
					} else if (this.serverData.serverInfo.ping < 300L) {
						this.statusIcon = Sprite.PING_4_SPRITE;
					} else if (this.serverData.serverInfo.ping < 600L) {
						this.statusIcon = Sprite.PING_3_SPRITE;
					} else if (this.serverData.serverInfo.ping < 1000L) {
						this.statusIcon = Sprite.PING_2_SPRITE;
					} else {
						this.statusIcon = Sprite.PING_1_SPRITE;
					}

					this.statusIconTooltip = Text.translatable("multiplayer.status.ping", this.serverData.serverInfo.ping);
					this.onlinePlayersTooltip = this.serverData.serverInfo.playerListSummary;
			}
		}

		public void updateServerList() {

		}

		protected void drawIcon(GuiGraphics guiGraphics, int x, int y, Identifier icon) {
			guiGraphics.drawTexture(icon, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
		}

		private boolean uploadIcon(byte @Nullable [] iconBytes) {
			if (iconBytes == null) {
				this.icon.clear();
			} else {
				try {
					this.icon.upload(NativeImage.read(iconBytes));
				} catch (Throwable var3) {
					FriendsMultiplayerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.serverInfo.name, this.serverData.serverInfo.address, var3);
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
			mutableComponent.append(Text.translatable("narrator.select", this.serverData.serverInfo.name));
			mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
			switch (this.serverData.pingResult()) {
				case PINGING:
					mutableComponent.append(FriendsMultiplayerSelectionList.PINGING_STATUS);
					break;
				case INCOMPATIBLE:
					mutableComponent.append(FriendsMultiplayerSelectionList.INCOMPATIBLE_STATUS);
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.version.narration", this.serverData.serverInfo.version));
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.motd.narration", this.serverData.serverInfo.label));
					break;
				case UNREACHABLE:
					mutableComponent.append(FriendsMultiplayerSelectionList.NO_CONNECTION_STATUS);
					break;
				default:
					mutableComponent.append(FriendsMultiplayerSelectionList.ONLINE_STATUS);
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.ping.narration", this.serverData.serverInfo.ping));
					mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableComponent.append(Text.translatable("multiplayer.status.motd.narration", this.serverData.serverInfo.label));
					if (this.serverData.serverInfo.players != null) {
						mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
						mutableComponent.append(
							Text.translatable("multiplayer.status.player_count.narration", this.serverData.serverInfo.players.online(), this.serverData.serverInfo.players.max())
						);
						mutableComponent.append(CommonTexts.SENTENCE_SEPARATOR);
						mutableComponent.append(Texts.join(this.serverData.serverInfo.playerListSummary, Text.literal(", ")));
					}
			}

			return mutableComponent;
		}

		@Override
		public void close() {
			this.icon.close();
		}

		private record StatusDescription(String serverIp, String serverName) {
			@SuppressWarnings("unchecked")
			public static StatusDescription read(String json) {
				try {
					var map = (Map<String, String>) GsonHelper.read(json);
					return new StatusDescription(map.get("server_ip"), map.get("server_name"));
				} catch (Exception e) {
					return null;
				}
			}
		}

		@Override
		public boolean canJoin() {
			return statusDescription.serverIp() != null;
		}

		@Override
		public ServerInfo getServerData() {
			return serverData.serverInfo();
		}
	}

	@Environment(EnvType.CLIENT)
	public class E4mcFriendServerEntry extends Entry {
		private static final int ICON_WIDTH = 32;
		private static final int ICON_HEIGHT = 32;
		private static final int SPACING = 5;
		private static final int STATUS_ICON_WIDTH = 10;
		private static final int STATUS_ICON_HEIGHT = 8;
		private final FriendsMultiplayerScreen screen;
		private final MinecraftClient minecraft;
		private final E4mcStatusDescription statusDescription;
		private final ServerInfoEx serverData;
		private final FaviconTexture icon;
		private byte @Nullable [] lastIconBytes;
		private long lastClickTime;
		@Nullable
		private List<Text> onlinePlayersTooltip;
		@Nullable
		private FriendsMultiplayerSelectionList.Sprite statusIcon;
		@Nullable
		private Text statusIconTooltip;

		protected E4mcFriendServerEntry(final FriendsMultiplayerScreen screen, final User friend) {
			this.screen = screen;
			statusDescription = E4mcStatusDescription.read(friend.getStatus().getActivity().rawDescription());
			String name;
			if (friend.isSystem()) {
				name = friend.getSystem().getFronters().stream().map(PkSystem.Member::getDisplayName)
					.collect(Collectors.joining("/")) + "(" + friend.getSystem().getName() + "/" + friend.getName() + ")";
			} else {
				name = friend.getName();
			}
			this.serverData = new ServerInfoEx(statusDescription.getServerData(name));
			this.minecraft = MinecraftClient.getInstance();
			this.icon = FaviconTexture.createServerFaviconTexture(this.minecraft.getTextureManager(), serverData.serverInfo.address != null ? serverData.serverInfo.address : friend.getUuid() + "_" + serverData.serverInfo.name);
			this.refreshStatus();
		}

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (this.serverData.pingResult() == PingResult.INITIAL) {
				this.serverData.setPingResult(PingResult.PINGING);
				this.serverData.serverInfo.label = CommonTexts.EMPTY;
				this.serverData.serverInfo.playerCountLabel = CommonTexts.EMPTY;
				FriendsMultiplayerSelectionList.THREAD_POOL
					.submit(
						() -> {
							try {
								this.screen
									.getPinger()
									.add(
										this.serverData.serverInfo,
										() -> {
										}
									);
							} catch (UnknownHostException var2) {
								this.serverData.setPingResult(PingResult.UNREACHABLE);
								this.serverData.serverInfo.label = FriendsMultiplayerSelectionList.CANT_RESOLVE_TEXT;
								this.minecraft.execute(this::refreshStatus);
							} catch (Exception var3) {
								this.serverData.setPingResult(PingResult.UNREACHABLE);
								this.serverData.serverInfo.label = FriendsMultiplayerSelectionList.CANT_CONNECT_TEXT;
								this.minecraft.execute(this::refreshStatus);
							}
						}
					);
			}

			if (serverData.pingResult == PingResult.PINGING && serverData.serverInfo.ping != -2) {
				this.serverData.setPingResult(
					this.serverData.serverInfo.protocolVersion == SharedConstants.getGameVersion().getProtocolVersion() ? PingResult.SUCCESSFUL : PingResult.INCOMPATIBLE
				);
			}
			refreshStatus();

			guiGraphics.drawShadowedText(this.minecraft.textRenderer, this.serverData.serverInfo.name, left + ICON_WIDTH + 3, top + 1, -1);
			List<OrderedText> list = this.minecraft.textRenderer.wrapLines(this.serverData.serverInfo.label, width - ICON_WIDTH - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				guiGraphics.drawShadowedText(this.minecraft.textRenderer, list.get(i), left + ICON_WIDTH + 3, top + 12 + 9 * i, -8355712);
			}

			this.drawIcon(guiGraphics, left, top, this.icon.getTextureId());
			if (this.serverData.pingResult() == PingResult.PINGING) {
				int i = (int) (Util.getMeasuringTimeMs() / 100L + index * 2 & 7L);
				if (i > 4) {
					i = 8 - i;
				}
				this.statusIcon = switch (i) {
					case 1 -> Sprite.PINGING_2_SPRITE;
					case 2 -> Sprite.PINGING_3_SPRITE;
					case 3 -> Sprite.PINGING_4_SPRITE;
					case 4 -> Sprite.PINGING_5_SPRITE;
					default -> Sprite.PINGING_1_SPRITE;
				};
			}

			int i = left + width - 10 - 5;
			if (this.statusIcon != null) {
				statusIcon.draw(guiGraphics, i, top);
			}

			byte[] bs = this.serverData.serverInfo.getFavicon();
			if (!Arrays.equals(bs, this.lastIconBytes)) {
				if (this.uploadServerIcon(bs)) {
					this.lastIconBytes = bs;
				} else {
					this.serverData.serverInfo.setFavicon(null);
					this.updateServerList();
				}
			}

			Text component;
			if (statusDescription.domain() == null) {
				component = NOT_PUBLISHED_STATUS;
			} else {
				if (this.serverData.pingResult() == PingResult.INCOMPATIBLE) {
					component = this.serverData.serverInfo.version.copy().formatted(Formatting.RED);
				} else {
					component = this.serverData.serverInfo.playerCountLabel;
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
				guiGraphics.fill(left, top, left + ICON_WIDTH, top + ICON_HEIGHT, -1601138544);
				int l = mouseX - left;
				int m = mouseY - top;
				if (this.canJoin()) {
					if (l < 32 && l > 16) {
						Sprite.JOIN_HIGHLIGHTED_SPRITE.draw(guiGraphics, left, top);
					} else {
						Sprite.JOIN_SPRITE.draw(guiGraphics, left, top);
					}
				}
			}
		}

		private void refreshStatus() {
			this.onlinePlayersTooltip = null;
			if (statusDescription.domain() == null) {
				this.serverData.setPingResult(PingResult.UNREACHABLE);
			}
			switch (this.serverData.pingResult()) {
				case INITIAL:
				case PINGING:
					this.statusIcon = Sprite.PING_1_SPRITE;
					this.statusIconTooltip = PINGING_STATUS;
					break;
				case INCOMPATIBLE:
					this.statusIcon = Sprite.INCOMPATIBLE_SPRITE;
					this.onlinePlayersTooltip = this.serverData.serverInfo.playerListSummary;
					this.statusIconTooltip = INCOMPATIBLE_STATUS;
					break;
				case UNREACHABLE:
					this.statusIcon = Sprite.UNREACHABLE_SPRITE;
					if (statusDescription.domain() == null) {
						break;
					}
					this.statusIconTooltip = NO_CONNECTION_STATUS;
					break;
				case SUCCESSFUL:
					if (this.serverData.serverInfo.ping < 150L) {
						this.statusIcon = Sprite.PING_5_SPRITE;
					} else if (this.serverData.serverInfo.ping < 300L) {
						this.statusIcon = Sprite.PING_4_SPRITE;
					} else if (this.serverData.serverInfo.ping < 600L) {
						this.statusIcon = Sprite.PING_3_SPRITE;
					} else if (this.serverData.serverInfo.ping < 1000L) {
						this.statusIcon = Sprite.PING_2_SPRITE;
					} else {
						this.statusIcon = Sprite.PING_1_SPRITE;
					}

					this.statusIconTooltip = Text.translatable("multiplayer.status.ping", this.serverData.serverInfo.ping);
					this.onlinePlayersTooltip = this.serverData.serverInfo.playerListSummary;
			}
		}

		public void updateServerList() {

		}

		protected void drawIcon(GuiGraphics guiGraphics, int x, int y, Identifier icon) {
			RenderSystem.enableBlend();
			guiGraphics.drawTexture(icon, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
		}

		public boolean canJoin() {
			return statusDescription.domain() != null;
		}

		private boolean uploadServerIcon(byte @Nullable [] iconBytes) {
			if (iconBytes == null) {
				this.icon.clear();
			} else {
				try {
					this.icon.upload(NativeImage.read(iconBytes));
				} catch (Throwable var3) {
					FriendsMultiplayerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.serverInfo.name, this.serverData.serverInfo.address, var3);
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
			MutableText mutableText = Text.empty();
			mutableText.append(Text.translatable("narrator.select", this.serverData.serverInfo.name));
			mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
			switch (this.serverData.pingResult()) {
				case PINGING:
					mutableText.append(FriendsMultiplayerSelectionList.PINGING_STATUS);
					break;
				case INCOMPATIBLE:
					mutableText.append(FriendsMultiplayerSelectionList.INCOMPATIBLE_STATUS);
					mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.version.narration", this.serverData.serverInfo.version));
					mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.serverData.serverInfo.label));
					break;
				case UNREACHABLE:
					mutableText.append(FriendsMultiplayerSelectionList.NO_CONNECTION_STATUS);
					if (statusDescription.domain() == null) {
						mutableText.append(NOT_PUBLISHED_STATUS);
					}
					break;
				default:
					mutableText.append(FriendsMultiplayerSelectionList.ONLINE_STATUS);
					mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.ping.narration", this.serverData.serverInfo.ping));
					mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
					mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.serverData.serverInfo.label));
					if (this.serverData.serverInfo.players != null) {
						mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
						mutableText.append(
							Text.translatable("multiplayer.status.player_count.narration", this.serverData.serverInfo.players.online(), this.serverData.serverInfo.players.max())
						);
						mutableText.append(CommonTexts.SENTENCE_SEPARATOR);
						mutableText.append(Texts.join(this.serverData.serverInfo.playerListSummary, Text.literal(", ")));
					}
			}

			return mutableText;
		}

		@Override
		public void close() {
			this.icon.close();
		}

		@Override
		public ServerInfo getServerData() {
			return serverData.serverInfo();
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

	static final class ServerInfoEx {
		private final ServerInfo serverInfo;
		@Setter
		private PingResult pingResult;

		private ServerInfoEx(ServerInfo serverInfo) {
			this.serverInfo = serverInfo;
			this.pingResult = PingResult.INITIAL;
		}

		public ServerInfo serverInfo() {
			return serverInfo;
		}

		public PingResult pingResult() {
			return pingResult;
		}

	}

	enum PingResult {
		INITIAL,
		PINGING,
		UNREACHABLE,
		INCOMPATIBLE,
		SUCCESSFUL
	}

	private enum Sprite {
		UNREACHABLE_SPRITE(0, 5),
		INCOMPATIBLE_SPRITE(0, 5),
		PING_1_SPRITE(0, 4),
		PING_2_SPRITE(0, 3),
		PING_3_SPRITE(0, 2),
		PING_4_SPRITE(0, 3),
		PING_5_SPRITE(0, 0),
		PINGING_1_SPRITE(10, 0),
		PINGING_2_SPRITE(10, 1),
		PINGING_3_SPRITE(10, 2),
		PINGING_4_SPRITE(10, 3),
		PINGING_5_SPRITE(10, 4),
		JOIN_HIGHLIGHTED_SPRITE(0, 32, 32, 32, SERVER_SELECTION_TEXTURE),
		JOIN_SPRITE(0, 0, 32, 32, SERVER_SELECTION_TEXTURE),

		;
		private final int u, v, texWidth, texHeight;
		private final Identifier atlas;

		Sprite(int u, int v, int texWidth, int texHeight, Identifier texture) {
			this.u = u;
			this.v = v;
			this.texWidth = texWidth;
			this.texHeight = texHeight;
			this.atlas = texture;
		}

		Sprite(int u, int v) {
			this(u, 176 + v * 8, STATUS_ICON_WIDTH, STATUS_ICON_HEIGHT, ICONS);
		}

		public void draw(GuiGraphics graphics, int x, int y) {
			graphics.drawTexture(atlas, x, y, u, v, texWidth, texHeight, 256, 256);
		}

	}
}
