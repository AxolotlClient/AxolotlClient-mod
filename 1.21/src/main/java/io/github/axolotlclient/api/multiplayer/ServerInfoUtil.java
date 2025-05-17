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
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ServerInfoUtil {
	public static Status.Activity.ServerInfo getServerInfo(String levelName, ServerMetadata status) {
		if (status == null) {
			return new Status.Activity.ServerInfo(levelName, null, null, null, null);
		}
		return new Status.Activity.ServerInfo(levelName, status.description().getString(),
			new Status.Activity.ServerInfo.Favicon(status.favicon().map(ServerMetadata.Favicon::iconBytes).orElse(null)),
			status.players().map(p ->
				new Status.Activity.ServerInfo.Players(p.max(), p.online(),
					p.sample().stream().map(prof -> new Status.Activity.ServerInfo.Players.Player(prof.getName(), UUIDHelper.toUndashed(prof.getId()))).toList())
			).orElse(null),
			status.version().map(v -> new Status.Activity.ServerInfo.Version(v.name(), v.protocol())).orElse(null));
	}

	public static ServerMetadata getServerStatus(Status.Activity.ServerInfo info) {
		return new ServerMetadata(Text.of(info.levelName()),
			Optional.ofNullable(info.players()).map(p -> new ServerMetadata.Players(p.max(),
				p.online(),
				p.sample().stream().map(prof -> new GameProfile(UUIDHelper.fromUndashed(prof.uuid()), prof.name())).toList())),
			Optional.ofNullable(info.version()).map(v -> new ServerMetadata.Version(v.name(), v.protocol())),
			Optional.ofNullable(info.icon()).map(f -> new ServerMetadata.Favicon(f.iconBytes())),
			false);
	}

	public static ServerInfo getServerData(String username, Status.Activity.E4mcMetadata metadata) {
		ServerMetadata serverMetadata = metadata.serverInfo() != null ? getServerStatus(metadata.serverInfo()) : null;
		var data = new ServerInfo(username, metadata.domain(), ServerInfo.ServerType.OTHER);
		if (serverMetadata != null) {
			serverMetadata.favicon().ifPresent(icon -> data.setFavicon(icon.iconBytes()));
			data.label = Text.literal(metadata.serverInfo().levelName());
			serverMetadata.version().ifPresentOrElse(ver -> {
				data.version = Text.literal(ver.name());
				data.protocolVersion = ver.protocol();
			}, () -> {
				data.version = Text.translatable("multiplayer.status.old");
				data.protocolVersion = 0;
			});
			serverMetadata.players().ifPresentOrElse(player -> {
				data.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(player.online(), player.max());
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
}
