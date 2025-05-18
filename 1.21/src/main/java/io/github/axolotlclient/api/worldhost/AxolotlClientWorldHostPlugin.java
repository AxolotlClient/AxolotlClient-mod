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

package io.github.axolotlclient.api.worldhost;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UndashedUuid;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.handlers.StatusUpdateHandler;
import io.github.axolotlclient.api.multiplayer.ServerInfoUtil;
import io.github.axolotlclient.api.requests.FriendRequest;
import io.github.axolotlclient.api.requests.UserRequest;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.gaming32.worldhost.FriendsListUpdate;
import io.github.gaming32.worldhost.WorldHost;
import io.github.gaming32.worldhost.gui.screen.PlayerInfoScreen;
import io.github.gaming32.worldhost.plugin.*;
import io.github.gaming32.worldhost.plugin.vanilla.GameProfileBasedProfilable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class AxolotlClientWorldHostPlugin implements WorldHostPlugin {

	static AxolotlClientWorldHostPlugin Instance;
	private final FriendAdder friendAdder = new AxolotlClientFriendAdder();

	public AxolotlClientWorldHostPlugin() {
		Instance = this;
		API.addStartupListener(() -> WorldHost.reconnect(false, true));
		StatusUpdateHandler.addUpdateListener("world_host_plugin", user -> {
			if (user.getStatus().isOnline() && user.getStatus().getActivity() != null) {
				if (user.getStatus().getActivity().hasMetadata()) {
					AxolotlClientOnlineFriend friend = AxolotlClientOnlineFriend.of(user);
					WorldHost.ONLINE_FRIENDS.put(friend.uuid(), friend);
					WorldHost.ONLINE_FRIEND_UPDATES.forEach(FriendsListUpdate::friendsListUpdate);
				}
			}
		});
	}

	Status.Activity.WorldHostMetadata getWhStatusDescription() {
		var server = MinecraftClient.getInstance().getServer();
		var status = server.getServerMetadata();
		String connectionId = server.isRemote() ? WorldHost.connectionIdToString(WorldHost.CONNECTION_ID) : null;
		String externalIp = server.isRemote() && API.getInstance().getApiOptions().allowFriendsServerJoin.get() ? WorldHost.getExternalIp() : null;
		return new Status.Activity.WorldHostMetadata(connectionId, externalIp,
			ServerInfoUtil.getServerInfo(server.getSaveProperties().getWorldName(), status));
	}

	@Override
	public void listFriends(Consumer<FriendListFriend> friendConsumer) {
		FriendRequest.getInstance().getFriends().thenAccept(list -> list.stream().map(AxolotlClientFriendListFriend::new).forEach(friendConsumer));
	}

	@Override
	public Optional<FriendAdder> friendAdder() {
		return Optional.of(friendAdder);
	}

	@Override
	public void pingFriends(Collection<OnlineFriend> friends) {
		friends.stream().filter(AxolotlClientOnlineFriend.class::isInstance).forEach(friend -> {
			Status.Activity.ServerInfo info = switch (((AxolotlClientOnlineFriend) friend).metadata.attributes()) {
				case Status.Activity.WorldHostMetadata wh -> wh.serverInfo();
				case Status.Activity.E4mcMetadata e4 -> e4.serverInfo();
				case Status.Activity.ExternalServerMetadata ex ->
					new Status.Activity.ServerInfo(ex.serverName(), ex.serverName(), null, null, null);
				default ->
					throw new IllegalStateException("Unexpected value: " + ((AxolotlClientOnlineFriend) friend).metadata.attributes());
			};
			WorldHost.ONLINE_FRIEND_PINGS.put(friend.uuid(), ServerInfoUtil.getServerStatus(info));
		});
	}

	@Override
	public void refreshOnlineFriends() {
		FriendRequest.getInstance().getFriends().thenAccept(list -> {
			list.stream().filter(u -> u.getStatus().getTitle().equals("api.status.title.world_host")).map(AxolotlClientFriendListFriend::new).forEach(friend ->
				WorldHost.ONLINE_FRIENDS.put(friend.profile.getId(), AxolotlClientOnlineFriend.of(friend.friend)));
			WorldHost.ONLINE_FRIEND_UPDATES.forEach(FriendsListUpdate::friendsListUpdate);
		});
	}

	private record AxolotlClientOnlineFriend(User user, GameProfile profile,
											 Status.Activity.Metadata metadata) implements OnlineFriend, GameProfileBasedProfilable {
		private static AxolotlClientOnlineFriend of(User user) {
			if (user.getStatus().isOnline() && user.getStatus().getActivity() != null) {
				if (user.getStatus().getActivity().hasMetadata()) {
					Status.Activity.Metadata data = user.getStatus().getActivity().metadata();
					return new AxolotlClientOnlineFriend(user, new GameProfile(UndashedUuid.fromStringLenient(user.getUuid()), user.getName()), data);
				}
			}
			throw new IllegalArgumentException();
		}

		@Override
		public UUID uuid() {
			return profile.getId();
		}

		@Override
		public void joinWorld(Screen screen) {
			switch (metadata.attributes()) {
				case Status.Activity.WorldHostMetadata wh -> {
					Long id = WorldHost.tryParseConnectionId(wh.connectionId());
					if (id != null) {
						WorldHost.join(id, screen);
					}
				}
				case Status.Activity.E4mcMetadata e4 ->
					connectToServer(screen, e4.domain(), e4.serverInfo().levelName());
				case Status.Activity.ExternalServerMetadata ex ->
					connectToServer(screen, ex.address(), ex.serverName());
				default -> throw new IllegalStateException("Unexpected value: " + metadata.attributes());
			}
		}

		private void connectToServer(Screen parent, String address, String name) {
			if (address != null) {
				ConnectScreen.connect(parent, MinecraftClient.getInstance(), ServerAddress.parse(address), new ServerInfo(name, address, ServerInfo.ServerType.OTHER), false, null);
			}
		}

		@Override
		public GameProfile defaultProfile() {
			return profile;
		}

		@Override
		public Joinability joinability() {
			switch (metadata.attributes()) {
				case Status.Activity.WorldHostMetadata a -> {
					if (a.connectionId() != null) {
						return Joinability.Joinable.INSTANCE;
					}
				}
				case Status.Activity.E4mcMetadata e -> {
					if (e.domain() != null) {
						return Joinability.Joinable.INSTANCE;
					}
				}
				case Status.Activity.ExternalServerMetadata ex -> {
					if (ex.address() != null) {
						return Joinability.Joinable.INSTANCE;
					}
					return new Joinability.Unjoinable(Text.translatable("api.worldhost.joinability.not_joinable"));
				}
				default -> throw new IllegalStateException("Unexpected value: " + metadata.attributes());
			}
			return new Joinability.Unjoinable(Text.translatable("api.worldhost.joinability.not_published"));
		}
	}

	private record AxolotlClientFriendListFriend(User friend,
												 GameProfile profile) implements FriendListFriend, GameProfileBasedProfilable {
		private AxolotlClientFriendListFriend(User friend) {
			this(friend, new GameProfile(UndashedUuid.fromStringLenient(friend.getUuid()), friend.getName()));
		}

		@Override
		public void removeFriend(Runnable runnable) {
			FriendRequest.getInstance().removeFriend(friend);
		}

		@Override
		public void showFriendInfo(Screen screen) {
			MinecraftClient.getInstance().setScreen(new PlayerInfoScreen(screen, profile));
		}

		@Override
		public GameProfile defaultProfile() {
			return profile;
		}

		@Override
		public void addFriend(boolean notify, Runnable refresher) {
			FriendRequest.getInstance().addFriend(friend.getUuid());
			refresher.run();
		}

		@Override
		public Optional<Text> tag() {
			return Optional.of(Text.literal("AxolotlClient"));
		}
	}

	private static class AxolotlClientFriendAdder implements FriendAdder {
		@Override
		public Text label() {
			return Text.literal("AxolotlClient");
		}

		@Override
		public void searchFriends(String s, int i, Consumer<FriendListFriend> consumer) {
			if (s.isEmpty()) {
				return;
			}
			UUIDHelper.ensureUuidOpt(s).join().ifPresent(uuid -> UserRequest.get(uuid).thenAccept(o -> o.map(AxolotlClientFriendListFriend::new).ifPresent(consumer)));
		}

		@Override
		public boolean delayLookup(String s) {
			return true;
		}

		@Override
		public int maxValidNameLength() {
			return 36;
		}
	}
}
