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

package io.github.axolotlclient.modules.hypixel;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHeadMode;
import io.github.axolotlclient.util.ThreadExecuter;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.reply.PlayerReply;

/**
 * Based on Osmium by Intro-Dev
 * (<a href="https://github.com/Intro-Dev/Osmium">Github</a>)
 *
 * @license CC0-1.0
 * @implNote Provides a layer between the hypixel api and the client to obtain information with minimal api calls
 */

public class HypixelAbstractionLayer {

	private static final HashMap<String, CompletableFuture<PlayerReply>> cachedPlayerData = new HashMap<>();
	private static final AtomicInteger hypixelApiCalls = new AtomicInteger(0);
	private static Supplier<String> keySupplier;
	private static HypixelAPI api;
	private static boolean validApiKey = false;

	public static void setApiKeySupplier(Supplier<String> supplier) {
		keySupplier = supplier;
	}

	public static boolean hasValidAPIKey() {
		return validApiKey;
	}

	public static JsonObject getPlayerProperty(String uuid, String stat) {
		if (loadPlayerDataIfAbsent(uuid)) {
			PlayerReply.Player player = getPlayer(uuid);
			return player == null ? null : player.getProperty(stat).getAsJsonObject();
		}
		return null;
	}

	public static int getPlayerLevel(String uuid, String mode) {
		if (api == null) {
			loadApiKey();
		}
		if (loadPlayerDataIfAbsent(uuid)) {
			try {
				if (Objects.equals(mode, LevelHeadMode.NETWORK.toString())) {
					return (int) cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer().getNetworkLevel();
				} else if (Objects.equals(mode, LevelHeadMode.BEDWARS.toString())) {
					return cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer()
						.getIntProperty("achievements.bedwars_level", 0);
				} else if (Objects.equals(mode, LevelHeadMode.SKYWARS.toString())) {
					int exp = cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer()
						.getIntProperty("stats.SkyWars.skywars_experience", 0);
					return Math.round(ExpCalculator.getLevelForExp(exp));
				}
			} catch (TimeoutException | InterruptedException | ExecutionException e) {
				return -1;
			}
		}
		return 0;
	}

	private static PlayerReply.Player getPlayer(String uuid) {
		if (api == null) {
			loadApiKey();
		}
		if (loadPlayerDataIfAbsent(uuid)) {
			try {
				return cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer();
			} catch (TimeoutException | InterruptedException | ExecutionException ignored) {
			}
		}
		return null;
	}

	public static void loadApiKey() {
		String API_KEY = keySupplier.get();
		if (API_KEY == null) {
			return;
		}
		if (!Objects.equals(API_KEY, "")) {
			try {
				api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(API_KEY)));
				validApiKey = true;
			} catch (Exception ignored) {
				validApiKey = false;
			}
		} else {
			validApiKey = false;
		}
	}

	private static boolean loadPlayerDataIfAbsent(String uuid) {
		if (cachedPlayerData.get(uuid) == null) {
			// set at 115 to have a buffer in case of disparity between threads
			if (hypixelApiCalls.get() <= 115) {
				cachedPlayerData.put(uuid, api.getPlayerByUuid(uuid));
				hypixelApiCalls.incrementAndGet();
				ThreadExecuter.scheduleTask(hypixelApiCalls::decrementAndGet, 1, TimeUnit.MINUTES);
				return true;
			}
			return false;
		}
		return true;
	}

	public static void clearPlayerData() {
		cachedPlayerData.clear();
	}

	public static void handleDisconnectEvents(UUID uuid) {
		freePlayerData(uuid.toString());
	}

	private static void freePlayerData(String uuid) {
		cachedPlayerData.remove(uuid);
	}
}
