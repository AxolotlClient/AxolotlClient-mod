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
		var data = new ServerInfo(username, metadata.domain(), false);
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
				data.playerCountLabel = Text.literal(Integer.toString(player.online())).append(Text.literal("/").formatted(Formatting.DARK_GRAY)).append(Integer.toString(player.max())).formatted(Formatting.GRAY);
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
