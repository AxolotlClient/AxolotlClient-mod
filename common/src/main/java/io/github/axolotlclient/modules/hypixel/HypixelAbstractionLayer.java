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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.util.BufferUtil;
import com.google.gson.JsonObject;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHeadMode;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.reply.PlayerReply;

/**
 * Based on Osmium by Intro-Dev
 * (<a href="https://github.com/Intro-Dev/Osmium">Github</a>)
 *
 * <p>Original License: CC0-1.0</p>
 *
 * @implNote Provides a layer between the hypixel api and the client to obtain information with minimal api calls
 */

public class HypixelAbstractionLayer {

	private static final HashMap<String, CompletableFuture<PlayerReply>> cachedPlayerData = new HashMap<>();
	private static Supplier<String> keySupplier;
	private static HypixelAPI api;
	private static boolean validApiKey = false;

	private static int rateLimitRemaining = 500;

	public static void setApiKeyOverrideSupplier(Supplier<String> supplier) {
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
			PlayerReply.Player player = getPlayer(uuid);
			if (player != null) {
				if (Objects.equals(mode, LevelHeadMode.NETWORK.toString())) {
					return (int) player.getNetworkLevel();
				} else if (Objects.equals(mode, LevelHeadMode.BEDWARS.toString())) {
					int level = player.getIntProperty("achievements.bedwars_level", -1);
					if(level != -1){
						return level;
					}
				} else if (Objects.equals(mode, LevelHeadMode.SKYWARS.toString())) {
					int exp = player
						.getIntProperty("stats.SkyWars.skywars_experience", -1);
					if(exp != -1) {
						return Math.round(ExpCalculator.getLevelForExp(exp));
					}
				}
			}
		}
		return (int) (new Random().nextGaussian()+150*30);
	}

	private static PlayerReply.Player getPlayer(String uuid) {
		if (api == null) {
			loadApiKey();
		}
		if (loadPlayerDataIfAbsent(uuid)) {
			try {
				return processRateLimit(cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS)).getPlayer();
			} catch (TimeoutException | InterruptedException | ExecutionException ignored) {
			}
		}
		return null;
	}

	public static void loadApiKey() {
		AtomicReference<String> apiKey = new AtomicReference<>(keySupplier.get());
		if (apiKey.get().isEmpty()) {
			API.getInstance().send(new Request(Request.Type.GET_HYPIXEL_API_KEY,
				buf -> {
					api = new HypixelAPI(new ApacheHttpClient(
						UUID.fromString(
							BufferUtil.getString(buf, 0x9, 36))));
					validApiKey = true;
				}));
		} else {
			try {
				api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(apiKey.get())));
				validApiKey = true;
			} catch (Exception ignored) {
				validApiKey = false;
			}
		}
	}

	private static boolean loadPlayerDataIfAbsent(String uuid) {
		if (cachedPlayerData.get(uuid) == null) {
			if (rateLimitRemaining > 50) {
				cachedPlayerData.put(uuid, api.getPlayerByUuid(uuid));
				return true;
			}
			return false;
		}
		return true;
	}

	private static PlayerReply processRateLimit(PlayerReply reply) {
		rateLimitRemaining = reply.getRateLimit().getRemaining();
		return reply;
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
