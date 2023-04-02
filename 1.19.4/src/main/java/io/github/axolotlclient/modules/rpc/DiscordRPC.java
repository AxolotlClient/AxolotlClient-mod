/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
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

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.rpc.gameSdk.GameSdkDownloader;
import io.github.axolotlclient.util.OSUtil;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;

/**
 * This DiscordRPC module is derived from <a href="https://github.com/DeDiamondPro/HyCord">HyCord</a>.
 *
 * @author DeDiamondPro
 * @license GPL-3.0
 */

public class DiscordRPC extends AbstractModule {

	public static Activity currentActivity;
	public static Core discordRPC;
	private static DiscordRPC Instance;
	private static boolean running;	public final BooleanOption enabled = new BooleanOption("enabled", value -> {
		if (value) {
			initRPC();
		} else {
			shutdown();
		}
	}, false);
	public final OptionCategory category = new OptionCategory("rpc");
	public final BooleanOption showActivity = new BooleanOption("showActivity", true);
	public final EnumOption showServerNameMode = new EnumOption("showServerNameMode",
		new String[]{"showIp", "showName", "off"}, "off");
	public final BooleanOption showTime = new BooleanOption("showTime", true);
	private final Instant time = Instant.now();

	public static void setWorld(String world) {
		if (running) {
			if (currentActivity == null) {
				DiscordRPC.getInstance().updateRPC();
			}
			currentActivity.setDetails("World: " + world);
			if (discordRPC.isOpen()) {
				discordRPC.activityManager().updateActivity(currentActivity);
			}
		}
	}

	public void updateRPC() {
		if (discordRPC != null && discordRPC.isOpen()) {
			updateActivity();
		}
	}

	public static DiscordRPC getInstance() {
		if (Instance == null)
			Instance = new DiscordRPC();
		return Instance;
	}

	public void updateActivity() {
		Activity activity = new Activity();

		String state = switch (showServerNameMode.get()) {
			case "showIp" -> MinecraftClient.getInstance().world == null ? "In the menu"
				: (MinecraftClient.getInstance().getCurrentServerEntry() == null ? "Singleplayer"
				: MinecraftClient.getInstance().getCurrentServerEntry().address);
			case "showName" -> MinecraftClient.getInstance().world == null ? "In the menu"
				: (MinecraftClient.getInstance().getCurrentServerEntry() == null ? "Singleplayer"
				: MinecraftClient.getInstance().getCurrentServerEntry().name);
			default -> "";
		};

		if (showActivity.get() && MinecraftClient.getInstance().getCurrentServerEntry() != null) {
			activity.setDetails(Util.getGame());
		} else if (showActivity.get() && currentActivity != null) {
			activity.setDetails(currentActivity.getDetails());
		}

		activity.setState(state);
		activity.setType(ActivityType.PLAYING);

		if (showTime.get()) {
			activity.timestamps().setStart(Instant.ofEpochMilli(time.toEpochMilli()));
		}

		if (currentActivity != null) {
			currentActivity.close();
		}

		activity.assets().setLargeText("AxolotlClient " + MinecraftClient.getInstance().getGameVersion());
		activity.assets().setLargeImage("icon");
		discordRPC.activityManager().updateActivity(activity);
		currentActivity = activity;
	}

	public static void shutdown() {
		running = false;
	}

	public void init() {
		category.add(enabled, showTime, showActivity, showServerNameMode);

		AxolotlClient.CONFIG.addCategory(category);

		if (OSUtil.getOS() == OSUtil.OperatingSystem.OTHER) {
			enabled.setForceOff(true, "crash");
		}
	}

	public void tick() {
		if (!running && enabled.get()) {
			initRPC();
		}

		if (running) {
			updateRPC();
		}
	}

	@SuppressWarnings("BusyWait")
	public void initRPC() {
		if (enabled.get()) {
			GameSdkDownloader.downloadSdk(AxolotlClient.LOGGER, enabled);
		}

		if (enabled.get()) {
			CreateParams params = new CreateParams();

			params.setClientID(875835666729152573L);
			params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

			DiscordEventAdapter handler = new DiscordEventAdapter() {
			};
			params.registerEventHandler(handler);

			try {
				discordRPC = new Core(params);

				running = true;
				Thread callBacks = new Thread(() -> {
					while (enabled.get() && running) {
						discordRPC.runCallbacks();

						try {
							Thread.sleep(16);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					discordRPC.close();
					Thread.currentThread().interrupt();
				});
				callBacks.start();
				AxolotlClient.LOGGER.info("Started RPC Core");
			} catch (Exception e) {
				if (!e.getMessage().contains("INTERNAL_ERROR")) {
					AxolotlClient.LOGGER.error("An error occurred: ");
					e.printStackTrace();
				} else {
					enabled.set(false);
				}
			}
		}
	}


}
