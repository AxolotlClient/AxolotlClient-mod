package io.github.axolotlclient.api.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.menu.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.LanScanWidget;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.gui.widget.ServerListEntryWidget;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class FriendsMultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
	private Screen parent;
	private FriendsMultiplayerSelectionList serverListWidget;
	private ServerList serverList;
	private ButtonWidget editButton;
	private ButtonWidget joinButton;
	private ButtonWidget deleteButton;
	private boolean deleteServerConfirmationDialogOpen;
	private boolean addServerDialogOpen;
	private boolean editServerDialogOpen;
	private boolean serverOpen;
	private String tooltipText;
	private ServerListEntry serverEntry;
	private boolean initialized;

	public FriendsMultiplayerScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		if (!this.initialized) {
			this.initialized = true;
			this.serverList = new ServerList(this.minecraft);
			this.serverList.load();

			this.serverListWidget = new FriendsMultiplayerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
			this.serverListWidget.setServers(this.serverList);
		} else {
			this.serverListWidget.updateBounds(this.width, this.height, 32, this.height - 64);
		}

		this.addButtons();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.serverListWidget.handleMouse();
	}

	public void addButtons() {
		this.buttons.add(this.editButton = new ButtonWidget(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit")));
		this.buttons.add(this.deleteButton = new ButtonWidget(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete")));
		this.buttons.add(this.joinButton = new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select")));
		this.buttons.add(new ButtonWidget(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct")));
		this.buttons.add(new ButtonWidget(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add")));
		this.buttons.add(new ButtonWidget(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh")));
		this.buttons.add(new ButtonWidget(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel")));
		this.moveToServer(this.serverListWidget.getCurrentServerIndex());
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
	}

	@Override
	protected void buttonClicked(ButtonWidget buttonWidget) {
		if (buttonWidget.active) {
			EntryListWidget.Entry entry = this.serverListWidget.getCurrentServerIndex() < 0
				? null
				: this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex());
			if (buttonWidget.id == 2 && entry instanceof ServerListEntryWidget) {
				String string = ((ServerListEntryWidget)entry).fetchServer().name;
				if (string != null) {
					this.deleteServerConfirmationDialogOpen = true;
					String string2 = I18n.translate("selectServer.deleteQuestion");
					String string3 = "'" + string + "' " + I18n.translate("selectServer.deleteWarning");
					String string4 = I18n.translate("selectServer.deleteButton");
					String string5 = I18n.translate("gui.cancel");
					ConfirmScreen confirmScreen = new ConfirmScreen(this, string2, string3, string4, string5, this.serverListWidget.getCurrentServerIndex());
					this.minecraft.openScreen(confirmScreen);
				}
			} else if (buttonWidget.id == 1) {
				this.connect();
			} else if (buttonWidget.id == 4) {
				this.serverOpen = true;
				this.minecraft.openScreen(new DirectConnectScreen(this, this.serverEntry = new ServerListEntry(I18n.translate("selectServer.defaultName"), "", false)));
			} else if (buttonWidget.id == 3) {
				this.addServerDialogOpen = true;
				this.minecraft.openScreen(new AddServerScreen(this, this.serverEntry = new ServerListEntry(I18n.translate("selectServer.defaultName"), "", false)));
			} else if (buttonWidget.id == 7 && entry instanceof ServerListEntryWidget) {
				this.editServerDialogOpen = true;
				ServerListEntry serverListEntry = ((ServerListEntryWidget)entry).fetchServer();
				this.serverEntry = new ServerListEntry(serverListEntry.name, serverListEntry.address, false);
				this.serverEntry.set(serverListEntry);
				this.minecraft.openScreen(new AddServerScreen(this, this.serverEntry));
			} else if (buttonWidget.id == 0) {
				this.minecraft.openScreen(this.parent);
			} else if (buttonWidget.id == 8) {
				this.refresh();
			}
		}
	}

	private void refresh() {
		this.minecraft.openScreen(new MultiplayerScreen(this.parent));
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		EntryListWidget.Entry entry = this.serverListWidget.getCurrentServerIndex() < 0
			? null
			: this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex());
		if (this.deleteServerConfirmationDialogOpen) {
			this.deleteServerConfirmationDialogOpen = false;
			if (bl && entry instanceof ServerListEntryWidget) {
				this.serverList.remove(this.serverListWidget.getCurrentServerIndex());
				this.serverList.save();
				this.serverListWidget.setCurrentServerIndex(-1);
				this.serverListWidget.setServers(this.serverList);
			}

			this.minecraft.openScreen(this);
		} else if (this.serverOpen) {
			this.serverOpen = false;
			if (bl) {
				this.connect(this.serverEntry);
			} else {
				this.minecraft.openScreen(this);
			}
		} else if (this.addServerDialogOpen) {
			this.addServerDialogOpen = false;
			if (bl) {
				this.serverList.add(this.serverEntry);
				this.serverList.save();
				this.serverListWidget.setCurrentServerIndex(-1);
				this.serverListWidget.setServers(this.serverList);
			}

			this.minecraft.openScreen(this);
		} else if (this.editServerDialogOpen) {
			this.editServerDialogOpen = false;
			if (bl && entry instanceof ServerListEntryWidget) {
				ServerListEntry serverListEntry = ((ServerListEntryWidget)entry).fetchServer();
				serverListEntry.name = this.serverEntry.name;
				serverListEntry.address = this.serverEntry.address;
				serverListEntry.set(this.serverEntry);
				this.serverList.save();
				this.serverListWidget.setServers(this.serverList);
			}

			this.minecraft.openScreen(this);
		}
	}

	@Override
	protected void keyPressed(char c, int i) {
		int j = this.serverListWidget.getCurrentServerIndex();
		EntryListWidget.Entry entry = j < 0 ? null : this.serverListWidget.getEntry(j);
		if (i == 63) {
			this.refresh();
		} else {
			if (j >= 0) {
				if (i == 200) {
					if (isShiftDown()) {
						if (j > 0 && entry instanceof ServerListEntryWidget) {
							this.serverList.swap(j, j - 1);
							this.moveToServer(this.serverListWidget.getCurrentServerIndex() - 1);
							this.serverListWidget.scroll(-this.serverListWidget.getEntryHeight());
							this.serverListWidget.setServers(this.serverList);
						}
					} else if (j > 0) {
						this.moveToServer(this.serverListWidget.getCurrentServerIndex() - 1);
						this.serverListWidget.scroll(-this.serverListWidget.getEntryHeight());
						if (this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex()) instanceof LanScanWidget) {
							if (this.serverListWidget.getCurrentServerIndex() > 0) {
								this.moveToServer(this.serverListWidget.size() - 1);
								this.serverListWidget.scroll(-this.serverListWidget.getEntryHeight());
							} else {
								this.moveToServer(-1);
							}
						}
					} else {
						this.moveToServer(-1);
					}
				} else if (i == 208) {
					if (isShiftDown()) {
						if (j < this.serverList.size() - 1) {
							this.serverList.swap(j, j + 1);
							this.moveToServer(j + 1);
							this.serverListWidget.scroll(this.serverListWidget.getEntryHeight());
							this.serverListWidget.setServers(this.serverList);
						}
					} else if (j < this.serverListWidget.size()) {
						this.moveToServer(this.serverListWidget.getCurrentServerIndex() + 1);
						this.serverListWidget.scroll(this.serverListWidget.getEntryHeight());
						if (this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex()) instanceof LanScanWidget) {
							if (this.serverListWidget.getCurrentServerIndex() < this.serverListWidget.size() - 1) {
								this.moveToServer(this.serverListWidget.size() + 1);
								this.serverListWidget.scroll(this.serverListWidget.getEntryHeight());
							} else {
								this.moveToServer(-1);
							}
						}
					} else {
						this.moveToServer(-1);
					}
				} else if (i != 28 && i != 156) {
					super.keyPressed(c, i);
				} else {
					this.buttonClicked(this.buttons.get(2));
				}
			} else {
				super.keyPressed(c, i);
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.tooltipText = null;
		this.renderBackground();
		this.serverListWidget.render(i, j, f);
		this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.title"), this.width / 2, 20, 16777215);
		super.render(i, j, f);
		if (this.tooltipText != null) {
			this.renderTooltip(Lists.<String>newArrayList(Splitter.on("\n").split(this.tooltipText)), i, j);
		}
	}

	public void connect() {
		EntryListWidget.Entry entry = this.serverListWidget.getCurrentServerIndex() < 0
			? null
			: this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex());
		if (entry instanceof ServerListEntryWidget) {
			this.connect(((ServerListEntryWidget)entry).fetchServer());
		} else if (entry instanceof LanServerEntry) {
			LanServerQueryManager.LanServerInfo lanServerInfo = ((LanServerEntry)entry).getLanServerInfo();
			this.connect(new ServerListEntry(lanServerInfo.getMotd(), lanServerInfo.getPort(), true));
		}
	}

	private void connect(ServerListEntry serverListEntry) {
		this.minecraft.openScreen(new ConnectScreen(this, this.minecraft, serverListEntry));
	}

	public void moveToServer(int i) {
		this.serverListWidget.setCurrentServerIndex(i);
		EntryListWidget.Entry entry = i < 0 ? null : this.serverListWidget.getEntry(i);
		this.joinButton.active = false;
		this.editButton.active = false;
		this.deleteButton.active = false;
		if (entry != null && !(entry instanceof LanScanWidget)) {
			this.joinButton.active = true;
			if (entry instanceof ServerListEntryWidget) {
				this.editButton.active = true;
				this.deleteButton.active = true;
			}
		}
	}

	public MultiplayerServerListPinger getServerListPinger() {
		return this.pinger;
	}

	public void setTooltip(String string) {
		this.tooltipText = string;
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		this.serverListWidget.mouseClicked(i, j, k);
	}

	@Override
	protected void mouseReleased(int i, int j, int k) {
		super.mouseReleased(i, j, k);
		this.serverListWidget.mouseReleased(i, j, k);
	}

	public ServerList getServerList() {
		return this.serverList;
	}
}
