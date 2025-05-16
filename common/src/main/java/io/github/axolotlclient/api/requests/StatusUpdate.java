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

package io.github.axolotlclient.api.requests;

import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.translation.TranslationProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class StatusUpdate {

	private static Request createStatusUpdate(String titleString, String descriptionString) {
		Status status = API.getInstance().getSelf().getStatus();
		String description;
		if (descriptionString.contains("{")) {
			try {
				var json = GsonHelper.fromJson(descriptionString);
				description = json.has("value") ? json.get("value").getAsString() : "";
			} catch (Throwable t) {
				description = descriptionString;
			}
		} else {
			description = descriptionString;
		}
		if (status.getActivity() != null) {
			Status.Activity prev = status.getActivity();
			if (prev.title().equals(titleString) && prev.description().equals(description) && prev.rawDescription().equals(descriptionString)) {
				return null;
			} else {
				status.setActivity(new Status.Activity(titleString, description, descriptionString, Instant.now()));
			}
		} else {
			status.setActivity(new Status.Activity(titleString, description, descriptionString, Instant.now()));
		}
		return Request.Route.ACCOUNT_ACTIVITY.builder().field("title", titleString).field("description", descriptionString)
			.field("started", status.getActivity().started().toString()).build();
	}

	public static Request online(MenuId menuId) {
		return createStatusUpdate("api.status.title.online", "api.status.description.menu." + menuId.getIdentifier());
	}

	public static Request inGame(SupportedServer server, String gameType, String gameMode, String map) {
		TranslationProvider tr = API.getInstance().getTranslationProvider();
		boolean gm = !gameMode.isEmpty();
		boolean mp = !map.isEmpty();
		String description;
		if (gm && mp) {
			description = tr.translate("api.status.description.in_game.game_mode_map", server.name, gameType, gameMode, map);
		} else if (gm) {
			description = tr.translate("api.status.description.in_game.game_mode_map", server.name, gameType, gameMode, map);
		} else if (mp) {
			description = tr.translate("api.status.description.in_game.map", server.name, gameType, map);
		} else {
			description = tr.translate("api.status.description.in_game", server.name, gameType);
		}
		return createStatusUpdate("api.status.title.detailed.in_game", GsonHelper.GSON.toJson(Map.of("value", description, "server_name", server.name, "server_ip", server.ip)));
	}

	public static Request inGameUnknown(String description) {
		return createStatusUpdate("api.status.title.in_game_unknown", description);
	}

	public static final String SPECIAL_STATUS_PREFIX = "api.status.title.detailed";

	public static final String WORLD_HOST_STATUS_TITLE = "api.status.title.detailed.world_host";

	public static Request worldHostStatusUpdate(String description) {
		return createStatusUpdate(WORLD_HOST_STATUS_TITLE, description);
	}

	public static final String E4MC_STATUS_TITLE = "api.status.title.detailed.e4mc";

	public static Request e4mcStatusUpdate(String description) {
		return createStatusUpdate(E4MC_STATUS_TITLE, description);
	}

	public static Request inGameServer(String name, String ip) {
		if (API.getInstance().getApiOptions().allowFriendsServerJoin.get()) {
			return createStatusUpdate("api.status.title.detailed.unknown_server", GsonHelper.GSON.toJson(Map.of("value", name, "server_ip", ip)));
		}
		return createStatusUpdate("api.status.title.detailed.unknown_server", name);
	}

	@Getter
	@RequiredArgsConstructor
	public enum MenuId {
		IN_MENU("in_menu"),
		MAIN_MENU("main_menu"),
		SERVER_LIST("server_list"),
		SETTINGS("settings");
		private final String identifier;
	}

	@RequiredArgsConstructor
	@Getter
	public enum SupportedServer {
		HYPIXEL("Hypixel", Pattern.compile("^(?:mc\\.)?hypixel\\.net$"), "mc.hypixel.net"),
		MCC_ISLAND("MCC Island", Pattern.compile("^play\\.mccisland\\.net$"), "play.mccisland.net");
		private final String name;
		private final Pattern address;
		private final String ip;
	}
}
