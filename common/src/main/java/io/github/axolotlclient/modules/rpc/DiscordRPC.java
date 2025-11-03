/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.rpc;

import java.time.Instant;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringArrayOption;
import io.github.axolotlclient.modules.AbstractCommonModule;
import io.github.axolotlclient.util.CommonUtil;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.OSUtil;
import io.github.axolotlclient.util.ThreadExecuter;
import io.github.axolotlclient.util.options.ForceableBooleanOption;

public class DiscordRPC extends AbstractCommonModule {
	private static final long CLIENT_ID = 875835666729152573L;
	private static boolean running;
	private static DiscordRPC Instance;
	private final OptionCategory category = OptionCategory.create("rpc");
	private final BooleanOption showActivity = new BooleanOption("showActivity", true);
	private final ForceableBooleanOption enabled = new ForceableBooleanOption("enabled", false, value -> {
		if (value) {
			ThreadExecuter.scheduleTask(this::initRPC);
		} else {
			ThreadExecuter.scheduleTask(this::shutdown);
		}
	});
	private final StringArrayOption showServerNameMode = new StringArrayOption("showServerNameMode",
		new String[]{"showIp", "showName", "off"}, "off");
	private final BooleanOption showTime = new BooleanOption("showTime", true);
	private final Instant time = Instant.now();
	private final Logger logger = AxolotlClientCommon.getInstance().getLogger();
	private IPCClient ipcClient;
	private String currentWorld = "";

	public static DiscordRPC getInstance() {
		if (Instance == null)
			Instance = new DiscordRPC();
		return Instance;
	}

	public void setWorld(String world) {
		currentWorld = world;
	}

	public void init() {
		category.add(enabled, showTime, showActivity, showServerNameMode);
		if (OSUtil.getOS() == OSUtil.OperatingSystem.OTHER) {
			enabled.setForceOff(true, "crash");
		}
		AxolotlClientCommon.getInstance().getConfig().addCategory(category);
	}

	public void tick() {
		if (!running && enabled.get()) {
			ThreadExecuter.scheduleTask(this::initRPC);
		}
		if (running) {
			ThreadExecuter.scheduleTask(this::updateRPC);
		}
	}

	public void shutdown() {
		if (running) {
			ipcClient.close();
			running = false;
		}
	}

	private RichPresence createRichPresence(String gameVersion, String state, String details) {
		RichPresence.Builder builder = new RichPresence.Builder();
		builder.setLargeImage("icon", "AxolotlClient " + gameVersion);
		if (showTime.get()) {
			builder.setStartTimestamp(time.getEpochSecond());
		}
		builder.setState(state).setDetails(details);
		builder.setActivityType(ActivityType.Playing);
		return builder.build();
	}

	private void createRichPresence() {
		String state = switch (showServerNameMode.get()) {
			case "showIp" -> client.br$getWorld() == null ? "In the menu"
				: (CommonUtil.getCurrentServerAddress() == null ? "Singleplayer" : CommonUtil.getCurrentServerAddress());
			case "showName" -> client.br$getWorld() == null ? "In the menu"
				: (client.br$getServerAddress() == null
				? (CommonUtil.getCurrentServerAddress() == null ? "Singleplayer"
				: CommonUtil.getCurrentServerAddress())
				: client.br$getServerName());
			default -> "";
		};

		String details;
		if (showActivity.get() && !client.br$isLocalServer()) {
			details = CommonUtil.getGame();
		} else if (showActivity.get() && !currentWorld.isEmpty()) {
			details = currentWorld;
			currentWorld = "";
		} else {
			details = "";
		}

		setRichPresence(createRichPresence(AxolotlClientCommon.VERSION, state, details));
	}

	private void setRichPresence(RichPresence presence) {
		if (running && ipcClient != null) {
			ipcClient.sendRichPresence(presence);
		}
	}

	private void updateRPC() {
		createRichPresence();
	}

	private void initRPC() {
		if (enabled.get()) {
			if (ipcClient == null) {
				ipcClient = new IPCClient(CLIENT_ID);
				ipcClient.setListener(new IPCListener() {
					@Override
					public void onPacketSent(IPCClient client, Packet packet) {

					}

					@Override
					public void onPacketReceived(IPCClient client, Packet packet) {

					}

					@Override
					public void onActivityJoin(IPCClient client, String secret) {

					}

					@Override
					public void onActivitySpectate(IPCClient client, String secret) {

					}

					@Override
					public void onActivityJoinRequest(IPCClient client, String secret, User user) {

					}

					@Override
					public void onReady(IPCClient client) {
						createRichPresence();
					}

					@Override
					public void onClose(IPCClient client, JsonObject json) {
						logger.info("RPC Closed");
						running = false;
					}

					@Override
					public void onDisconnect(IPCClient client, Throwable t) {
						running = false;
					}
				});
			}
			try {
				ipcClient.connect();
				logger.info("Started RPC");
				running = true;
			} catch (Exception e) {
				enabled.set(false);
			}
		}
	}
}
