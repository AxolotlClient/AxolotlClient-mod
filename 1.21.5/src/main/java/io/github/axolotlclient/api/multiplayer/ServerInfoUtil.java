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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.authlib.GameProfile;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.util.UUIDHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;

public class ServerInfoUtil {
	public static Status.Activity.ServerInfo getServerInfo(String levelName, ServerStatus status) {
		if (status == null) {
			return new Status.Activity.ServerInfo(levelName, null, null, null, null);
		}
		return new Status.Activity.ServerInfo(levelName, status.description().getString(),
			new Status.Activity.ServerInfo.Favicon(status.favicon().map(ServerStatus.Favicon::iconBytes).orElse(null)),
			status.players().map(p ->
				new Status.Activity.ServerInfo.Players(p.max(), p.online(),
					p.sample().stream().map(prof -> new Status.Activity.ServerInfo.Players.Player(prof.getName(), UUIDHelper.toUndashed(prof.getId()))).toList())
			).orElse(null),
			status.version().map(v -> new Status.Activity.ServerInfo.Version(v.name(), v.protocol())).orElse(null));
	}

	public static ServerStatus getServerStatus(Status.Activity.ServerInfo info) {
		return new ServerStatus(Component.nullToEmpty(info.description()),
			Optional.ofNullable(info.players()).map(p -> new ServerStatus.Players(p.max(),
				p.online(),
				p.sample().stream().map(prof -> new GameProfile(UUIDHelper.fromUndashed(prof.uuid()), prof.name())).toList())),
			Optional.ofNullable(info.version()).map(v -> new ServerStatus.Version(v.name(), v.protocol())),
			Optional.ofNullable(info.icon()).map(f -> new ServerStatus.Favicon(f.iconBytes())),
			false);
	}

	public static ServerData getServerData(String username, Status.Activity.E4mcMetadata metadata) {
		ServerStatus serverMetadata = metadata.serverInfo() != null ? getServerStatus(metadata.serverInfo()) : null;
		var data = new ServerData(username, metadata.domain(), ServerData.Type.OTHER);
		if (serverMetadata != null) {
			serverMetadata.favicon().ifPresent(icon -> data.setIconBytes(icon.iconBytes()));
			data.motd = Component.literal(metadata.serverInfo().levelName());
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
