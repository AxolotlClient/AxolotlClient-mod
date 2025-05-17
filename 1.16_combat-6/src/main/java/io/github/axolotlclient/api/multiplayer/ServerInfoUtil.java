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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.mojang.authlib.GameProfile;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.util.UUIDHelper;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ServerInfoUtil {
	public static Status.Activity.ServerInfo getServerInfo(String levelName, ServerMetadata status) {
		if (status == null) {
			return new Status.Activity.ServerInfo(levelName, null, null, null, null);
		}
		return new Status.Activity.ServerInfo(levelName, status.getDescription().getString(),
			Optional.ofNullable(status.getFavicon()).map(Status.Activity.ServerInfo.Favicon::fromString).orElse(null),
			Optional.ofNullable(status.getPlayers()).map(p ->
				new Status.Activity.ServerInfo.Players(p.getPlayerLimit(), p.getOnlinePlayerCount(),
					Arrays.stream(p.getSample()).map(prof -> new Status.Activity.ServerInfo.Players.Player(prof.getName(), UUIDHelper.toUndashed(prof.getId()))).toList())
			).orElse(null),
			Optional.ofNullable(status.getVersion()).map(v -> new Status.Activity.ServerInfo.Version(v.getGameVersion(), v.getProtocolVersion())).orElse(null));
	}

	public static ServerMetadata getServerStatus(Status.Activity.ServerInfo info) {
		var metadata = new ServerMetadata();
		metadata.setDescription(Text.of(info.levelName()));
		Optional.ofNullable(info.players()).map(p -> {
			var players = new ServerMetadata.Players(p.max(),
				p.online());
			players.setSample(p.sample().stream().map(prof -> new GameProfile(UUIDHelper.fromUndashed(prof.uuid()), prof.name())).toArray(GameProfile[]::new));
			return players;
		}).ifPresent(metadata::setPlayers);
		Optional.ofNullable(info.icon()).map(Status.Activity.ServerInfo.Favicon::toString).ifPresent(metadata::setFavicon);
		Optional.ofNullable(info.version()).map(v -> new ServerMetadata.Version(v.name(), v.protocol())).ifPresent(metadata::setVersion);
		return metadata;
	}

	public static ServerInfo getServerData(String username, Status.Activity.E4mcMetadata metadata) {
		ServerMetadata serverMetadata = metadata.serverInfo() != null ? getServerStatus(metadata.serverInfo()) : null;
		var data = new ServerInfo(username, metadata.domain(), false);
		if (serverMetadata != null) {
			if (serverMetadata.getFavicon().startsWith("data:image/png;base64,")) {
				data.setIcon(serverMetadata.getFavicon().substring("data:image/png;base64,".length()));
			} else {
				data.setIcon(null);
			}
			data.label = new LiteralText(metadata.serverInfo().levelName());
			Optional.ofNullable(serverMetadata.getVersion()).ifPresentOrElse(ver -> {
				data.version = new LiteralText(ver.getGameVersion());
				data.protocolVersion = ver.getProtocolVersion();
			}, () -> {
				data.version = new TranslatableText("multiplayer.status.old");
				data.protocolVersion = 0;
			});
			Optional.ofNullable(serverMetadata.getPlayers()).ifPresentOrElse(player -> {
				data.playerCountLabel = new LiteralText(Integer.toString(player.getOnlinePlayerCount())).append(new LiteralText("/").formatted(Formatting.DARK_GRAY)).append(Integer.toString(player.getPlayerLimit())).formatted(Formatting.GRAY);
				if (player.getSample().length > 0) {
					List<Text> list = new ArrayList<>(player.getSample().length);

					for (GameProfile gameProfile : player.getSample()) {
						list.add(new LiteralText(gameProfile.getName()));
					}

					if (player.getSample().length < player.getOnlinePlayerCount()) {
						list.add(new TranslatableText("multiplayer.status.and_more", player.getOnlinePlayerCount() - player.getSample().length));
					}

					data.playerListSummary = list;
				} else {
					data.playerListSummary = List.of();
				}
			}, () -> data.label = new TranslatableText("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY));
		}
		return data;
	}
}
