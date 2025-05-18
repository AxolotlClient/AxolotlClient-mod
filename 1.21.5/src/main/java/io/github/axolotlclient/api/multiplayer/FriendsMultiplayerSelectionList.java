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
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.axolotlclient.api.types.PkSystem;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.modules.auth.Auth;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FriendsMultiplayerSelectionList extends ObjectSelectionList<FriendsMultiplayerSelectionList.Entry> {
	static final ResourceLocation INCOMPATIBLE_SPRITE = ResourceLocation.withDefaultNamespace("server_list/incompatible");
	static final ResourceLocation UNREACHABLE_SPRITE = ResourceLocation.withDefaultNamespace("server_list/unreachable");
	static final ResourceLocation PING_1_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_1");
	static final ResourceLocation PING_2_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_2");
	static final ResourceLocation PING_3_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_3");
	static final ResourceLocation PING_4_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_4");
	static final ResourceLocation PING_5_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_5");
	static final ResourceLocation PINGING_1_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_1");
	static final ResourceLocation PINGING_2_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_2");
	static final ResourceLocation PINGING_3_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_3");
	static final ResourceLocation PINGING_4_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_4");
	static final ResourceLocation PINGING_5_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_5");
	static final ResourceLocation JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/join_highlighted");
	static final ResourceLocation JOIN_SPRITE = ResourceLocation.withDefaultNamespace("server_list/join");
	static final Logger LOGGER = LogUtils.getLogger();
	static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(
		5,
		new ThreadFactoryBuilder()
			.setNameFormat("Friends Server Pinger #%d")
			.setDaemon(true)
			.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER))
			.build()
	);
	static final Component CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
	static final Component CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
	static final Component INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
	static final Component NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
	static final Component PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
	static final Component ONLINE_STATUS = Component.translatable("multiplayer.status.online");
	static final Component NOT_PUBLISHED_STATUS = Component.translatable("api.worldhost.joinability.not_published");
	private final FriendsMultiplayerScreen screen;
	private final List<Entry> friendEntries = new ArrayList<>();
	private final LoadingHeader loadingHeader = new LoadingHeader();

	public FriendsMultiplayerSelectionList(FriendsMultiplayerScreen screen, Minecraft minecraft, int width, int height, int y, int itemHeight) {
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
		FriendsMultiplayerSelectionList.Entry entry = this.getSelected();
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
	public abstract static class Entry extends ObjectSelectionList.Entry<FriendsMultiplayerSelectionList.Entry> implements AutoCloseable {
		public void close() {
		}

		public boolean canJoin() {
			return false;
		}

		public ServerData getServerData() {
			return null;
		}
	}

	public class StatusFriendEntry extends Entry {

		@Getter
		protected final User user;

		protected StatusFriendEntry(final FriendsMultiplayerScreen screen, final User friend) {
			this.user = friend;
		}

		@Override
		public Component getNarration() {
			return Component.literal(user.getName());
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (user.isSystem()) {
				MutableComponent fronters = Component.literal(
					user.getSystem().getFronters().stream().map(PkSystem.Member::getDisplayName)
						.collect(Collectors.joining("/")));
				Component tag = Component.literal("(" + user.getSystem().getName() + "/" + user.getName() + ")")
					.setStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY));
				graphics.drawString(minecraft.font, fronters.append(tag), left + 3, top + 1, -1, false);
			} else {
				graphics.drawString(minecraft.font, user.getName(), left + 3 + 32, top + 1, -1, false);
			}

			if (user.getStatus().isOnline() && user.getStatus().getActivity() != null) {
				graphics.drawString(minecraft.font, user.getStatus().getTitle(), left + 3 + 32, top + 12, 8421504, false);
				graphics.drawString(minecraft.font, user.getStatus().getDescription(), left + 3 + 40, top + 23, 8421504, false);
			} else if (user.getStatus().getLastOnline() != null) {
				graphics.drawString(minecraft.font, user.getStatus().getLastOnline(), left + 3 + 32, top + 12, 8421504, false);
			}

			ResourceLocation texture = Auth.getInstance().getSkinTexture(user);
			PlayerFaceRenderer.draw(graphics, texture, left, top, 32, true, false, -1);
		}
	}

	protected class ServerEntry extends Entry {
		private static final int ICON_WIDTH = 32;
		private static final int ICON_HEIGHT = 32;
		private static final int SPACING = 5;
		private static final int STATUS_ICON_WIDTH = 10;
		private static final int STATUS_ICON_HEIGHT = 8;
		private final FriendsMultiplayerScreen screen;
		private final Minecraft minecraft;
		protected final ServerData serverData;
		private final FaviconTexture icon;
		private byte @Nullable [] lastIconBytes;
		private long lastClickTime;
		@Nullable
		private List<Component> onlinePlayersTooltip;
		@Nullable
		private ResourceLocation statusIcon;
		@Nullable
		private Component statusIconTooltip;
		@Getter
		protected final User user;

		protected ServerEntry(FriendsMultiplayerScreen screen, ServerData serverData, User user) {
			this.screen = screen;
			this.minecraft = Minecraft.getInstance();
			this.serverData = serverData;
			this.icon = FaviconTexture.forServer(minecraft.getTextureManager(), serverData.ip != null ? serverData.ip : user.getUuid() + "_" + serverData.name);
			this.user = user;
			refreshStatus();
		}


		protected void refreshStatus() {
			this.onlinePlayersTooltip = null;
			if (!isPublished()) {
				this.serverData.setState(ServerData.State.UNREACHABLE);
			}
			switch (this.serverData.state()) {
				case INITIAL:
				case PINGING:
					this.statusIcon = FriendsMultiplayerSelectionList.PING_1_SPRITE;
					this.statusIconTooltip = FriendsMultiplayerSelectionList.PINGING_STATUS;
					break;
				case INCOMPATIBLE:
					this.statusIcon = FriendsMultiplayerSelectionList.INCOMPATIBLE_SPRITE;
					this.onlinePlayersTooltip = this.serverData.playerList;
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

					this.statusIconTooltip = Component.translatable("multiplayer.status.ping", this.serverData.ping);
					this.onlinePlayersTooltip = this.serverData.playerList;
			}
		}

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			if (this.serverData.state() == ServerData.State.INITIAL) {
				this.serverData.setState(ServerData.State.PINGING);
				this.serverData.motd = CommonComponents.EMPTY;
				this.serverData.status = CommonComponents.EMPTY;
				FriendsMultiplayerSelectionList.THREAD_POOL
					.submit(
						() -> {
							try {
								this.screen
									.getPinger()
									.pingServer(
										this.serverData,
										() -> {
										},
										() -> {
											this.serverData
												.setState(
													this.serverData.protocol == SharedConstants.getCurrentVersion().getProtocolVersion() ? ServerData.State.SUCCESSFUL : ServerData.State.INCOMPATIBLE
												);
											this.minecraft.execute(this::refreshStatus);
										}
									);
							} catch (UnknownHostException var2) {
								this.serverData.setState(ServerData.State.UNREACHABLE);
								this.serverData.motd = FriendsMultiplayerSelectionList.CANT_RESOLVE_TEXT;
								this.minecraft.execute(this::refreshStatus);
							} catch (Exception var3) {
								this.serverData.setState(ServerData.State.UNREACHABLE);
								this.serverData.motd = FriendsMultiplayerSelectionList.CANT_CONNECT_TEXT;
								this.minecraft.execute(this::refreshStatus);
							}
						}
					);
			}

			guiGraphics.drawString(this.minecraft.font, this.serverData.name, left + ICON_WIDTH + 3, top + 1, -1);
			List<FormattedCharSequence> list = this.minecraft.font.split(this.serverData.motd, width - ICON_WIDTH - 2);

			for (int i = 0; i < Math.min(list.size(), 2); i++) {
				guiGraphics.drawString(this.minecraft.font, list.get(i), left + ICON_WIDTH + 3, top + 12 + 9 * i, -8355712);
			}

			guiGraphics.blit(RenderType::guiTextured, this.icon.textureLocation(), left, top, 0.0F, 0.0F, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
			ResourceLocation texture = Auth.getInstance().getSkinTexture(user);
			PlayerFaceRenderer.draw(guiGraphics, texture, left + ICON_WIDTH - 10, top + ICON_HEIGHT - 10, 10, true, false, -1);
			if (this.serverData.state() == ServerData.State.PINGING) {
				int i = (int) (Util.getMillis() / 100L + index * 2 & 7L);
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
				guiGraphics.blitSprite(RenderType::guiTextured, this.statusIcon, i, top, STATUS_ICON_WIDTH, STATUS_ICON_HEIGHT);
			}

			byte[] bs = this.serverData.getIconBytes();
			if (!Arrays.equals(bs, this.lastIconBytes)) {
				if (this.uploadIcon(bs)) {
					this.lastIconBytes = bs;
				} else {
					this.serverData.setIconBytes(null);
				}
			}

			Component component;
			if (!isPublished()) {
				component = NOT_PUBLISHED_STATUS;
			} else {
				if (this.serverData.state() == ServerData.State.INCOMPATIBLE) {
					component = this.serverData.version.copy().withStyle(ChatFormatting.RED);
				} else {
					component = this.serverData.status;
				}
			}
			int j = this.minecraft.font.width(component);
			int k = i - j - SPACING;
			guiGraphics.drawString(this.minecraft.font, component, k, top + 1, -8355712);
			if (this.statusIconTooltip != null && mouseX >= i && mouseX <= i + STATUS_ICON_WIDTH && mouseY >= top && mouseY <= top + STATUS_ICON_HEIGHT) {
				this.screen.setTooltipForNextRenderPass(this.statusIconTooltip);
			} else if (this.onlinePlayersTooltip != null && mouseX >= k && mouseX <= k + j && mouseY >= top && mouseY <= top - 1 + 9) {
				this.screen.setTooltipForNextRenderPass(Lists.transform(this.onlinePlayersTooltip, Component::getVisualOrderText));
			}

			if (this.minecraft.options.touchscreen().get() || hovering) {
				int l = mouseX - left;
				int m = mouseY - top;
				if (this.canJoin()) {
					guiGraphics.fill(left, top, left + ICON_WIDTH, top + ICON_HEIGHT, -1601138544);
					if (l < ICON_WIDTH && l > ICON_WIDTH / 2) {
						guiGraphics.blitSprite(RenderType::guiTextured, FriendsMultiplayerSelectionList.JOIN_HIGHLIGHTED_SPRITE, left, top, ICON_WIDTH, ICON_HEIGHT);
					} else {
						guiGraphics.blitSprite(RenderType::guiTextured, FriendsMultiplayerSelectionList.JOIN_SPRITE, left, top, ICON_WIDTH, ICON_HEIGHT);
					}
				}
			}
		}

		protected boolean isPublished() {
			return true;
		}

		@Override
		public boolean canJoin() {
			return serverData.state() == ServerData.State.SUCCESSFUL && isPublished();
		}

		private boolean uploadIcon(byte @Nullable [] iconBytes) {
			if (iconBytes == null) {
				this.icon.clear();
			} else {
				try {
					this.icon.upload(NativeImage.read(iconBytes));
				} catch (Throwable var3) {
					FriendsMultiplayerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, var3);
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
			if (Util.getMillis() - this.lastClickTime < 250L && canJoin()) {
				this.screen.joinSelectedServer();
			}

			this.lastClickTime = Util.getMillis();
			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public @NotNull Component getNarration() {
			MutableComponent mutableComponent = Component.empty();
			mutableComponent.append(Component.translatable("narrator.select", this.serverData.name));
			mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
			switch (this.serverData.state()) {
				case PINGING:
					mutableComponent.append(FriendsMultiplayerSelectionList.PINGING_STATUS);
					break;
				case INCOMPATIBLE:
					mutableComponent.append(FriendsMultiplayerSelectionList.INCOMPATIBLE_STATUS);
					mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
					mutableComponent.append(Component.translatable("multiplayer.status.version.narration", this.serverData.version));
					mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
					mutableComponent.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
					break;
				case UNREACHABLE:
					mutableComponent.append(FriendsMultiplayerSelectionList.NO_CONNECTION_STATUS);
					break;
				default:
					mutableComponent.append(FriendsMultiplayerSelectionList.ONLINE_STATUS);
					mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
					mutableComponent.append(Component.translatable("multiplayer.status.ping.narration", this.serverData.ping));
					mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
					mutableComponent.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
					if (this.serverData.players != null) {
						mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
						mutableComponent.append(
							Component.translatable("multiplayer.status.player_count.narration", this.serverData.players.online(), this.serverData.players.max())
						);
						mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
						mutableComponent.append(ComponentUtils.formatList(this.serverData.playerList, Component.literal(", ")));
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
		return new ExternalServerFriendEntry(screen, metadata, new ServerData(metadata.serverName(), metadata.address(), ServerData.Type.OTHER), friend);
	}

	public class ExternalServerFriendEntry extends ServerEntry {
		private final Status.Activity.ExternalServerMetadata statusDescription;

		private ExternalServerFriendEntry(FriendsMultiplayerScreen screen, Status.Activity.ExternalServerMetadata statusDescription, ServerData serverData, User friend) {
			super(screen, serverData, friend);
			this.statusDescription = statusDescription;
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

		protected E4mcServerFriendEntry(FriendsMultiplayerScreen screen, Status.Activity.E4mcMetadata statusDescription, ServerData serverData, User friend) {
			super(screen, serverData, friend);
			this.statusDescription = statusDescription;
		}

		@Override
		protected void refreshStatus() {
			super.refreshStatus();
			serverData.motd = Component.nullToEmpty(statusDescription.serverInfo().levelName());
		}

		@Override
		protected boolean isPublished() {
			return statusDescription.domain() != null;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LoadingHeader extends FriendsMultiplayerSelectionList.Entry {
		private final Minecraft minecraft = Minecraft.getInstance();

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			int i = top + height / 2 - 9 / 2;
			String string = LoadingDotsText.get(Util.getMillis());
			guiGraphics.drawString(this.minecraft.font, string, this.minecraft.screen.width / 2 - this.minecraft.font.width(string) / 2, i, -8355712);
		}

		@Override
		public @NotNull Component getNarration() {
			return Component.empty();
		}
	}

}
