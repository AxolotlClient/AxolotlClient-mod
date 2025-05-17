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
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.ServerStatus;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;

public class ServerInfoUtil {
	public static Status.Activity.ServerInfo getServerInfo(String levelName, ServerStatus status) {
		if (status == null) {
			return new Status.Activity.ServerInfo(levelName, null, null, null, null);
		}
		return new Status.Activity.ServerInfo(levelName, status.getDescription().getString(),
			Optional.ofNullable(status.getFavicon()).map(Status.Activity.ServerInfo.Favicon::fromString).orElse(null),
			Optional.ofNullable(status.getPlayers()).map(p ->
				new Status.Activity.ServerInfo.Players(p.getMax(), p.getOnline(),
					Arrays.stream(p.get()).map(prof -> new Status.Activity.ServerInfo.Players.Player(prof.getName(), UUIDHelper.toUndashed(prof.getId()))).toList())
			).orElse(null),
			Optional.ofNullable(status.getVersion()).map(v -> new Status.Activity.ServerInfo.Version(v.getName(), v.getProtocol())).orElse(null));
	}

	public static ServerStatus getServerStatus(Status.Activity.ServerInfo info) {
		var metadata = new ServerStatus();
		metadata.setDescription(new LiteralText(info.levelName()));
		Optional.ofNullable(info.players()).map(p -> {
			var players = new ServerStatus.Players(p.max(),
				p.online());
			players.set(p.sample().stream().map(prof -> new GameProfile(UUIDHelper.fromUndashed(prof.uuid()), prof.name())).toArray(GameProfile[]::new));
			return players;
		}).ifPresent(metadata::setPlayers);
		Optional.ofNullable(info.icon()).map(Status.Activity.ServerInfo.Favicon::toString).ifPresent(metadata::setFavicon);
		Optional.ofNullable(info.version()).map(v -> new ServerStatus.Version(v.name(), v.protocol())).ifPresent(metadata::setVersion);
		return metadata;
	}

	public static ServerListEntry getServerData(String username, Status.Activity.E4mcMetadata metadata) {
		ServerStatus serverMetadata = metadata.serverInfo() != null ? getServerStatus(metadata.serverInfo()) : null;
		var data = new ServerListEntry(username, metadata.domain(), false);
		if (serverMetadata != null) {
			if (serverMetadata.getFavicon().startsWith("data:image/png;base64,")) {
				data.setIcon(serverMetadata.getFavicon().substring("data:image/png;base64,".length()));
			} else {
				data.setIcon(null);
			}
			data.description = metadata.serverInfo().levelName();
			Optional.ofNullable(serverMetadata.getVersion()).ifPresentOrElse(ver -> {
				data.version = ver.getName();
				data.protocol = ver.getProtocol();
			}, () -> {
				data.version = I18n.translate("multiplayer.status.old");
				data.protocol = 0;
			});
			Optional.ofNullable(serverMetadata.getPlayers()).ifPresentOrElse(player -> {
				data.playerListString = Formatting.GRAY
					+ ""
					+ player.getOnline()
					+ Formatting.DARK_GRAY
					+ "/"
					+ Formatting.GRAY
					+ player.getMax();
				if (player.get().length > 0) {
					List<String> list = new ArrayList<>(player.get().length);

					for (GameProfile gameProfile : player.get()) {
						list.add(gameProfile.getName());
					}

					if (player.get().length < player.getOnline()) {
						list.add(I18n.translate("multiplayer.status.and_more", player.getOnline() - player.get().length));
					}

					data.playerListString = StringUtils.join(list, "\n");
				} else {
					data.playerListString = "";
				}
			}, () -> data.description = I18n.translate("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY));
		}
		return data;
	}
}
