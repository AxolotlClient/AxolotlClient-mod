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

import java.util.*;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import io.github.axolotlclient.util.GsonHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;

public record E4mcStatusDescription(String levelName, String domain, ServerStatus serverMetadata) {

	public String write() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("value", levelName);
		if (domain != null) {
			fields.put("domain", domain);
		}
		if (serverMetadata != null) {
			fields.put("server_metadata", ServerStatus.CODEC.encodeStart(JsonOps.INSTANCE, serverMetadata).getOrThrow());
		}
		return GsonHelper.GSON.toJson(fields);
	}

	public static E4mcStatusDescription read(String json) {
		var obj = GsonHelper.GSON.fromJson(json, JsonObject.class);
		var name = obj.get("value").getAsString();
		var domain = obj.has("domain") ? obj.get("domain").getAsString() : null;
		var serverMetadata = obj.has("server_metadata") ? ServerStatus.CODEC.parse(JsonOps.INSTANCE, obj.get("server_metadata")).getOrThrow() : null;
		return new E4mcStatusDescription(name, domain, serverMetadata);
	}

	public ServerData getServerData(String username) {
		var data = new ServerData(username, domain, ServerData.Type.OTHER);
		if (serverMetadata != null) {
			serverMetadata.favicon().ifPresent(icon -> data.setIconBytes(icon.iconBytes()));
			data.motd = Component.literal(levelName());
			serverMetadata.version().ifPresentOrElse(ver -> {
				data.version = Component.literal(ver.name());
				data.protocol = ver.protocol();
			}, () -> {
				data.version = Component.translatable("multiplayer.status.old");
				data.protocol = 0;
			});
			serverMetadata.players().ifPresentOrElse(player -> {
				data.status = ServerStatusPinger.formatPlayerCount(player.online(), player.max());
				data.players = player;
				if (!player.sample().isEmpty()) {
					List<Component> list = new ArrayList<>(player.sample().size());

					for (GameProfile gameProfile : player.sample()) {
						list.add(Component.literal(gameProfile.getName()));
					}

					if (player.sample().size() < player.online()) {
						list.add(Component.translatable("multiplayer.status.and_more", player.online() - player.sample().size()));
					}

					data.playerList = list;
				} else {
					data.playerList = List.of();
				}
			}, () -> data.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY));
		}
		return data;
	}
}
