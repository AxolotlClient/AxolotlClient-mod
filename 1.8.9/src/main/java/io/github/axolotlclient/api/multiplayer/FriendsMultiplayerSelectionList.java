package io.github.axolotlclient.api.multiplayer;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.multiplayer.LanScanWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FriendsMultiplayerSelectionList extends EntryListWidget {
	private final FriendsMultiplayerScreen parent;
	private final List<ServerListEntryWidget> servers = Lists.newArrayList();
	private final List<LanServerEntry> lanServers = Lists.newArrayList();
	private final EntryListWidget.Entry scanningWidget = new LanScanWidget();
	private int currentServerIndex = -1;

	public FriendsMultiplayerSelectionList(FriendsMultiplayerScreen multiplayerScreen, Minecraft minecraft, int i, int j, int k, int l, int m) {
		super(minecraft, i, j, k, l, m);
		this.parent = multiplayerScreen;
	}

	@Override
	public EntryListWidget.Entry getEntry(int i) {
		if (i < this.servers.size()) {
			return this.servers.get(i);
		} else {
			i -= this.servers.size();
			return i == 0 ? this.scanningWidget : this.lanServers.get(--i);
		}
	}

	@Override
	protected int size() {
		return this.servers.size() + 1 + this.lanServers.size();
	}

	public void setCurrentServerIndex(int i) {
		this.currentServerIndex = i;
	}

	@Override
	protected boolean isEntrySelected(int i) {
		return i == this.currentServerIndex;
	}

	public int getCurrentServerIndex() {
		return this.currentServerIndex;
	}

	public void setServers(ServerList serverList) {
		this.servers.clear();

		for (int i = 0; i < serverList.size(); i++) {
			this.servers.add(new ServerListEntryWidget(this.parent, serverList.get(i)));
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 30;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}

	public class ServerListEntryWidget implements EntryListWidget.Entry {
		private static final Logger LOGGER = LogManager.getLogger();
		private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(
			5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
		);
		private static final Identifier f_7988648 = new Identifier("textures/misc/unknown_server.png");
		private static final Identifier f_3265079 = new Identifier("textures/gui/server_selection.png");
		private final FriendsMultiplayerScreen screen;
		private final Minecraft minecraft;
		private final ServerListEntry entry;
		private final Identifier iconIdentifier;
		private String icon;
		private DynamicTexture iconTexture;
		private long f_0713251;

		protected ServerListEntryWidget(FriendsMultiplayerScreen multiplayerScreen, ServerListEntry serverListEntry) {
			this.screen = multiplayerScreen;
			this.entry = serverListEntry;
			this.minecraft = Minecraft.getInstance();
			this.iconIdentifier = new Identifier("servers/" + serverListEntry.address + "/icon");
			this.iconTexture = (DynamicTexture)this.minecraft.getTextureManager().get(this.iconIdentifier);
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl) {
			if (!this.entry.isLoaded) {
				this.entry.isLoaded = true;
				this.entry.ping = -2L;
				this.entry.description = "";
				this.entry.onlinePlayers = "";
				EXECUTOR.submit(() -> {
					try {
						screen.getServerListPinger().add(this.entry);
					} catch (UnknownHostException var2) {
						entry.ping = -1L;
						entry.description = Formatting.DARK_RED + "Can't resolve hostname";
					} catch (Exception var3) {
						this.entry.ping = -1L;
						this.entry.description = Formatting.DARK_RED + "Can't connect to server.";
					}
				});
			}

			boolean bl2 = this.entry.protocol > 47;
			boolean bl3 = this.entry.protocol < 47;
			boolean bl4 = bl2 || bl3;
			this.minecraft.textRenderer.draw(this.entry.name, j + 32 + 3, k + 1, 16777215);
			List<String> list = this.minecraft.textRenderer.split(this.entry.description, l - 32 - 2);

			for (int p = 0; p < Math.min(list.size(), 2); p++) {
				this.minecraft.textRenderer.draw(list.get(p), j + 32 + 3, k + 12 + this.minecraft.textRenderer.fontHeight * p, 8421504);
			}

			String string = bl4 ? Formatting.DARK_RED + this.entry.version : this.entry.onlinePlayers;
			int q = this.minecraft.textRenderer.getWidth(string);
			this.minecraft.textRenderer.draw(string, j + l - q - 15 - 2, k + 1, 8421504);
			int r = 0;
			String string2 = null;
			int s;
			String string3;
			if (bl4) {
				s = 5;
				string3 = bl2 ? "Client out of date!" : "Server out of date!";
				string2 = this.entry.playerListString;
			} else if (this.entry.isLoaded && this.entry.ping != -2L) {
				if (this.entry.ping < 0L) {
					s = 5;
				} else if (this.entry.ping < 150L) {
					s = 0;
				} else if (this.entry.ping < 300L) {
					s = 1;
				} else if (this.entry.ping < 600L) {
					s = 2;
				} else if (this.entry.ping < 1000L) {
					s = 3;
				} else {
					s = 4;
				}

				if (this.entry.ping < 0L) {
					string3 = "(no connection)";
				} else {
					string3 = this.entry.ping + "ms";
					string2 = this.entry.playerListString;
				}
			} else {
				r = 1;
				s = (int)(Minecraft.getTime() / 100L + i * 2 & 7L);
				if (s > 4) {
					s = 8 - s;
				}

				string3 = "Pinging...";
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bind(GuiElement.ICONS_LOCATION);
			GuiElement.drawTexture(j + l - 15, k, r * 10, 176 + s * 8, 10, 8, 256.0F, 256.0F);
			if (this.entry.getIcon() != null && !this.entry.getIcon().equals(this.icon)) {
				this.icon = this.entry.getIcon();
				this.loadServerIcon();
				this.screen.getServerList().save();
			}

			if (this.iconTexture != null) {
				this.m_5818390(j, k, this.iconIdentifier);
			} else {
				this.m_5818390(j, k, f_7988648);
			}

			int t = n - j;
			int u = o - k;
			if (t >= l - 15 && t <= l - 5 && u >= 0 && u <= 8) {
				this.screen.setTooltip(string3);
			} else if (t >= l - q - 15 - 2 && t <= l - 15 - 2 && u >= 0 && u <= 8) {
				this.screen.setTooltip(string2);
			}

			if (this.minecraft.options.touchscreen || bl) {
				this.minecraft.getTextureManager().bind(f_3265079);
				GuiElement.fill(j, k, j + 32, k + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int v = n - j;
				int w = o - k;
				if (this.m_1489497()) {
					if (v < 32 && v > 16) {
						GuiElement.drawTexture(j, k, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						GuiElement.drawTexture(j, k, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		protected void m_5818390(int i, int j, Identifier identifier) {
			this.minecraft.getTextureManager().bind(identifier);
			GlStateManager.enableBlend();
			GuiElement.drawTexture(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
			GlStateManager.disableBlend();
		}

		private boolean m_1489497() {
			return true;
		}

		private void loadServerIcon() {
			if (this.entry.getIcon() == null) {
				this.minecraft.getTextureManager().close(this.iconIdentifier);
				this.iconTexture = null;
			} else {
				ByteBuf byteBuf = Unpooled.copiedBuffer(this.entry.getIcon(), Charsets.UTF_8);
				ByteBuf byteBuf2 = Base64.decode(byteBuf);

				BufferedImage bufferedImage;
				label62: {
					try {
						bufferedImage = TextureUtil.readImage(new ByteBufInputStream(byteBuf2));
						Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
						Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
						break label62;
					} catch (Throwable var8) {
						LOGGER.error("Invalid icon for server " + this.entry.name + " (" + this.entry.address + ")", var8);
						this.entry.setIcon(null);
					} finally {
						byteBuf.release();
						byteBuf2.release();
					}

					return;
				}

				if (this.iconTexture == null) {
					this.iconTexture = new DynamicTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
					this.minecraft.getTextureManager().register(this.iconIdentifier, this.iconTexture);
				}

				bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.iconTexture.getPixels(), 0, bufferedImage.getWidth());
				this.iconTexture.upload();
			}
		}

		@Override
		public boolean mouseClicked(int i, int j, int k, int l, int m, int n) {
			if (m <= 32) {
				if (m < 32 && m > 16 && this.m_1489497()) {
					this.screen.moveToServer(i);
					this.screen.connect();
					return true;
				}
			}

			this.screen.moveToServer(i);
			if (Minecraft.getTime() - this.f_0713251 < 250L) {
				this.screen.connect();
			}

			this.f_0713251 = Minecraft.getTime();
			return false;
		}

		@Override
		public void renderOutOfBounds(int i, int j, int k) {
		}

		@Override
		public void mouseReleased(int i, int j, int k, int l, int m, int n) {
		}

		public ServerListEntry fetchServer() {
			return this.entry;
		}
	}
}
