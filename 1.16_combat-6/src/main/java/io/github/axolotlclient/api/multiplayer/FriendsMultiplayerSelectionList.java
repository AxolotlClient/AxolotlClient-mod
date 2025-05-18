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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.api.types.PkSystem;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.modules.auth.Auth;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class FriendsMultiplayerSelectionList extends AlwaysSelectedEntryListWidget<FriendsMultiplayerSelectionList.Entry> {
	private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
	static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
	static final Identifier ICONS = new Identifier("textures/gui/icons.png");
	static final Logger LOGGER = LogManager.getLogger();
	static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(
		5,
		new ThreadFactoryBuilder()
			.setNameFormat("Friends Server Pinger #%d")
			.setDaemon(true)
			.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER))
			.build()
	);
	static final Text CANT_RESOLVE_TEXT = new TranslatableText("multiplayer.status.cannot_resolve").styled(style -> style.withColor(TextColor.fromRgb(-65536)));
	static final Text CANT_CONNECT_TEXT = new TranslatableText("multiplayer.status.cannot_connect").styled(style -> style.withColor(TextColor.fromRgb(-65536)));
	static final Text INCOMPATIBLE_STATUS = new TranslatableText("multiplayer.status.incompatible");
	static final Text NO_CONNECTION_STATUS = new TranslatableText("multiplayer.status.no_connection");
	static final Text PINGING_STATUS = new TranslatableText("multiplayer.status.pinging");
	static final Text NOT_PUBLISHED_STATUS = new TranslatableText("api.worldhost.joinability.not_published").formatted(Formatting.RED);
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
		Entry entry = this.getSelected();
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
		public void render(MatrixStack graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (user.isSystem()) {
				MutableText fronters = new LiteralText(
					user.getSystem().getFronters().stream().map(PkSystem.Member::getDisplayName)
						.collect(Collectors.joining("/")));
				Text tag = new LiteralText("(" + user.getSystem().getName() + "/" + user.getName() + ")")
					.setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY));
				client.textRenderer.draw(graphics, fronters.append(tag), left + 3, top + 1, -1);
			} else {
				client.textRenderer.draw(graphics, user.getName(), left + 3 + 32, top + 1, -1);
			}

			if (user.getStatus().isOnline() && user.getStatus().getActivity() != null) {
				client.textRenderer.draw(graphics, user.getStatus().getTitle(), left + 3 + 32, top + 12, 8421504);
				client.textRenderer.draw(graphics, user.getStatus().getDescription(), left + 3 + 40, top + 23, 8421504);
			} else if (user.getStatus().getLastOnline() != null) {
				client.textRenderer.draw(graphics, user.getStatus().getLastOnline(), left + 3 + 32, top + 12, 8421504);
			}

			client.getTextureManager().bindTexture(Auth.getInstance().getSkinTexture(user));
			RenderSystem.enableBlend();
			drawTexture(graphics, left - 1, top - 1, 32, 32, 8, 8, 8, 8, 64, 64);
			drawTexture(graphics, left - 1, top - 1, 32, 32, 40, 8, 8, 8, 64, 64);
			RenderSystem.disableBlend();
		}
	}

	private static final int STATUS_ICON_HEIGHT = 8;
	private static final int STATUS_ICON_WIDTH = 10;

	protected class ServerEntry extends Entry {
		private static final int ICON_WIDTH = 32;
		private static final int ICON_HEIGHT = 32;
		private static final int SPACING = 5;
		private static final int STATUS_ICON_WIDTH = 10;
		private static final int STATUS_ICON_HEIGHT = 8;
		private final FriendsMultiplayerScreen screen;
		private final MinecraftClient minecraft;
		protected final ServerInfoEx serverData;
		private final Identifier iconId;
		private NativeImageBackedTexture icon;
		private String lastIconBytes;
		private long lastClickTime;
		@Nullable
		private List<Text> onlinePlayersTooltip;
		private Sprite statusIcon;
		@Nullable
		private Text statusIconTooltip;
		@Getter
		protected final User user;

		@SuppressWarnings("UnstableApiUsage")
		protected ServerEntry(FriendsMultiplayerScreen screen, ServerInfo serverData, User user) {
			this.screen = screen;
			this.minecraft = MinecraftClient.getInstance();
			this.serverData = new ServerInfoEx(serverData);
			this.iconId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(serverData.address != null ? serverData.address : user.getUuid() + "_" + serverData.name) + "/icon");
			this.user = user;
		}


		protected void refreshStatus() {
			this.onlinePlayersTooltip = null;
			if (!isPublished()) {
				this.serverData.setPingResult(PingResult.UNREACHABLE);
			}
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
					if (!isPublished()) {
						break;
					}
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

					this.statusIconTooltip = new TranslatableText("multiplayer.status.ping", this.serverData.serverInfo.ping);
					this.onlinePlayersTooltip = this.serverData.serverInfo.playerListSummary;
			}
		}

		@Override
		public void render(MatrixStack guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (this.serverData.pingResult() == PingResult.INITIAL) {
				this.serverData.setPingResult(PingResult.PINGING);
				this.serverData.serverInfo.label = LiteralText.EMPTY;
				this.serverData.serverInfo.playerCountLabel = LiteralText.EMPTY;
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

			drawStringWithShadow(guiGraphics, minecraft.textRenderer, this.serverData.serverInfo.name, left + ICON_WIDTH + 3, top + 1, -1);
			List<OrderedText> list = this.minecraft.textRenderer.wrapLines(this.serverData.serverInfo.label, width - ICON_WIDTH - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				minecraft.textRenderer.drawWithShadow(guiGraphics, list.get(i), left + ICON_WIDTH + 3, top + 12 + 9 * i, -8355712);
			}

			client.getTextureManager().bindTexture(this.icon != null ? this.iconId : UNKNOWN_SERVER_TEXTURE);
			drawTexture(guiGraphics, left, top, 0.0F, 0.0F, 32, 32, 32, 32);
			client.getTextureManager().bindTexture(Auth.getInstance().getSkinTexture(user));
			RenderSystem.enableBlend();
			drawTexture(guiGraphics, left + ICON_WIDTH - 10, top + ICON_HEIGHT - 10, 10, 10, 8, 8, 8, 8, 64, 64);
			drawTexture(guiGraphics, left + ICON_WIDTH - 10, top + ICON_HEIGHT - 10, 10, 10, 40, 8, 8, 8, 64, 64);
			RenderSystem.disableBlend();
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

			int i = left + width - STATUS_ICON_WIDTH - SPACING;
			if (this.statusIcon != null) {
				statusIcon.draw(guiGraphics, i, top);
			}

			String bs = this.serverData.serverInfo.getIcon();
			if (!Objects.equals(bs, this.lastIconBytes)) {
				if (this.uploadIcon(bs)) {
					this.lastIconBytes = bs;
				} else {
					this.serverData.serverInfo.setIcon(null);
				}
			}

			Text component;
			if (!isPublished()) {
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
			minecraft.textRenderer.drawWithShadow(guiGraphics, component, k, top + 1, -8355712);
			if (this.statusIconTooltip != null && mouseX >= i && mouseX <= i + STATUS_ICON_WIDTH && mouseY >= top && mouseY <= top + STATUS_ICON_HEIGHT) {
				this.screen.setDeferredTooltip(this.statusIconTooltip);
			} else if (this.onlinePlayersTooltip != null && mouseX >= k && mouseX <= k + j && mouseY >= top && mouseY <= top - 1 + 9) {
				this.screen.setDeferredTooltip(this.onlinePlayersTooltip);
			}

			if (this.minecraft.options.touchscreen || hovering) {
				int l = mouseX - left;
				int m = mouseY - top;
				if (this.canJoin()) {
					fill(guiGraphics, left, top, left + ICON_WIDTH, top + ICON_HEIGHT, -1601138544);
					if (l < ICON_WIDTH && l > ICON_WIDTH / 2) {
						Sprite.JOIN_HIGHLIGHTED_SPRITE.draw(guiGraphics, left, top);
					} else {
						Sprite.JOIN_SPRITE.draw(guiGraphics, left, top);
					}
				}
			}
		}

		protected boolean isPublished() {
			return true;
		}

		@Override
		public boolean canJoin() {
			return serverData.pingResult() == PingResult.SUCCESSFUL && isPublished();
		}

		private boolean uploadIcon(String iconBytes) {
			if (iconBytes == null) {
				this.minecraft.getTextureManager().destroyTexture(this.iconId);
				if (this.icon != null && this.icon.getImage() != null) {
					this.icon.getImage().close();
				}

				this.icon = null;
			} else {
				try {
					NativeImage nativeImage = NativeImage.read(iconBytes);
					if (nativeImage.getWidth() != 64 || nativeImage.getHeight() != 64) {
						throw new IllegalArgumentException("Icon must be 64x64, but was " + nativeImage.getWidth() + "x" + nativeImage.getHeight());
					}
					if (this.icon == null) {
						this.icon = new NativeImageBackedTexture(nativeImage);
					} else {
						this.icon.setImage(nativeImage);
						this.icon.upload();
					}

					this.minecraft.getTextureManager().registerTexture(this.iconId, this.icon);
				} catch (Throwable var3) {
					FriendsMultiplayerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.serverInfo.name, this.serverData.serverInfo().address, var3);
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
		public void close() {
			this.icon.close();
		}
	}

	private ExternalServerFriendEntry externalServerEntry(FriendsMultiplayerScreen screen, User friend) {
		Status.Activity.ExternalServerMetadata metadata = (Status.Activity.ExternalServerMetadata) friend.getStatus().getActivity().metadata().attributes();
		return new ExternalServerFriendEntry(screen, metadata, new ServerInfo(metadata.serverName(), metadata.address(), false), friend);
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
			serverData.serverInfo.label = Text.of(statusDescription.serverInfo().levelName());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LoadingHeader extends Entry {
		private final MinecraftClient minecraft = MinecraftClient.getInstance();

		@Override
		public void render(MatrixStack guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			int i = top + height / 2 - 9 / 2;

			String string = switch ((int) (Util.getMeasuringTimeMs() / 300L % 4L)) {
				case 1, 3 -> "o O o";
				case 2 -> "o o O";
				default -> "O o o";
			};
			minecraft.textRenderer.drawWithShadow(guiGraphics, string, this.minecraft.currentScreen.width / 2f - this.minecraft.textRenderer.getWidth(string) / 2f, i, -8355712);
		}
	}

	protected static final class ServerInfoEx {
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

	protected enum PingResult {
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

		public void draw(MatrixStack graphics, int x, int y) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(atlas);
			DrawableHelper.drawTexture(graphics, x, y, u, v, texWidth, texHeight, 256, 256);
		}

	}
}
