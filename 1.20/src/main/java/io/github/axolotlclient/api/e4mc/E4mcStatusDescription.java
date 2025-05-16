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

package io.github.axolotlclient.api.e4mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import io.github.axolotlclient.util.GsonHelper;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record E4mcStatusDescription(String levelName, String domain, ServerMetadata serverMetadata) {

	public String write() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("value", levelName);
		if (domain != null && serverMetadata != null) {
			fields.put("domain", domain);
			fields.put("server_metadata", ServerMetadata.CODEC.encodeStart(JsonOps.INSTANCE, serverMetadata).getOrThrow(false, s -> {
			}));
		}
		return GsonHelper.GSON.toJson(fields);
	}

	public static E4mcStatusDescription read(String json) {
		var obj = GsonHelper.GSON.fromJson(json, JsonObject.class);
		var name = obj.get("value").getAsString();
		var domain = obj.has("domain") ? obj.get("domain").getAsString() : null;
		var serverMetadata = obj.has("server_metadata") ? ServerMetadata.CODEC.parse(JsonOps.INSTANCE, obj.get("server_metadata")).getOrThrow(false, s -> {
		}) : null;
		return new E4mcStatusDescription(name, domain, serverMetadata);
	}

	public ServerInfo getServerData(String username) {
		var data = new ServerInfo(username, domain, false);
		if (serverMetadata != null) {
			serverMetadata.favicon().ifPresent(icon -> data.setFavicon(icon.iconBytes()));
			var descriptionString = serverMetadata.description().getString();
			if (descriptionString.startsWith(username)) {
				data.label = Text.of(descriptionString.substring(username.length() + 3));
			} else {
				data.label = serverMetadata.description();
			}
			serverMetadata.version().ifPresentOrElse(ver -> {
				data.version = Text.literal(ver.name());
				data.protocolVersion = ver.protocol();
			}, () -> {
				data.version = Text.translatable("multiplayer.status.old");
				data.protocolVersion = 0;
			});
			serverMetadata.players().ifPresentOrElse(player -> {
				data.playerCountLabel = createPlayerCountText(player.online(), player.max());
				data.players = player;
				if (!player.sample().isEmpty()) {
					List<Text> list = new ArrayList<>(player.sample().size());

					for (GameProfile gameProfile : player.sample()) {
						list.add(Text.literal(gameProfile.getName()));
					}

					if (player.sample().size() < player.online()) {
						list.add(Text.translatable("multiplayer.status.and_more", player.online() - player.sample().size()));
					}

					data.playerListSummary = list;
				} else {
					data.playerListSummary = List.of();
				}
			}, () -> data.label = Text.translatable("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY));
		}
		return data;
	}

	static Text createPlayerCountText(int current, int max) {
		return Text.literal(Integer.toString(current))
			.append(Text.literal("/").formatted(Formatting.DARK_GRAY))
			.append(Integer.toString(max))
			.formatted(Formatting.GRAY);
	}
}
