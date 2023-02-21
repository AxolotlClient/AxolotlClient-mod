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

package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.requests.Friends;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.api.util.UUIDHelper;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FriendHandler implements RequestHandler {

	private final API api;

	@Getter
	private final static FriendHandler Instance = new FriendHandler();

	protected FriendHandler() {
		this.api = API.getInstance();
	}

	public void addFriend(String uuid) {
		api.send(new Friends(object -> {
			if (!API.getInstance().requestFailed(object)) {
				api.getNotificationProvider().addStatus("api.success.requestSent", "api.success.requestSent.desc", UUIDHelper.getUsername(uuid));
			} else {
				APIError.display(object);
			}
		}, "add", uuid));
	}

	public void removeFriend(User user) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.removeFriend", "api.success.removeFriend.desc", user.getName());
			}
		}, "remove", API.getInstance().sanitizeUUID(user.getUuid())));
	}

	public void blockUser(String uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.blockUser", "api.success.blockUser.desc", UUIDHelper.getUsername(uuid));
			}
		}, "block", uuid));
	}

	public void unblockUser(String uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.unblockUser", "api.success.unblockUser.desc", UUIDHelper.getUsername(uuid));
			}
		}, "unblock", uuid));
	}

	public void getFriends(Consumer<List<User>> responseConsumer) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				List<User> list = new ArrayList<>();
				JsonArray friends = object.get("data").getAsJsonObject().get("friends").getAsJsonArray();
				friends.forEach(e -> {
					JsonObject s = e.getAsJsonObject().get("status").getAsJsonObject();
					Instant startedAt;
					if (s.has("startedAt")) {
						startedAt = Instant.ofEpochSecond(s.get("startedAt").getAsLong());
					} else {
						startedAt = Instant.ofEpochSecond(0);
					}
					Status status = new Status(s.get("online").getAsBoolean(), s.get("title").getAsString(),
							s.get("description").getAsString(), s.get("icon").getAsString(), startedAt);
					list.add(new User(e.getAsJsonObject().get("uuid").getAsString(), status));
				});
				responseConsumer.accept(list);
			}
		}, "get"));
	}

	public void getFriendRequests(BiConsumer<List<User>, List<User>> responseConsumer) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				JsonArray incoming = object.get("data").getAsJsonObject().get("incoming").getAsJsonArray();
				JsonArray outgoing = object.get("data").getAsJsonObject().get("outgoing").getAsJsonArray();

				List<User> in = new ArrayList<>();
				List<User> out = new ArrayList<>();
				incoming.forEach(e -> in.add(new User(e.getAsJsonObject().get("from").getAsString(), Status.UNKNOWN)));
				outgoing.forEach(e -> out.add(new User(e.getAsJsonObject().get("from").getAsString(), Status.UNKNOWN)));
				responseConsumer.accept(in, out);
			}
		}, "getRequests"));
	}

	public void getBlocked(Consumer<List<User>> responseConsumer) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				JsonArray blocked = object.get("data").getAsJsonObject().get("blocked").getAsJsonArray();

				List<User> bl = new ArrayList<>();
				blocked.forEach(e -> bl.add(new User(e.getAsJsonObject().get("uuid").getAsString(), Status.UNKNOWN)));
				responseConsumer.accept(bl);
			}
		}, "getBlocked"));
	}

	public void acceptFriendRequest(User from) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.acceptFriend", "api.success.acceptFriend.desc", from.getName());
			}
		}, "accept", from.getUuid()));
	}

	public void denyFriendRequest(User uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.denyFriend", "api.success.denyFriend.desc", uuid.getName());
			}
		}, "decline", uuid.getUuid()));
	}

	@Override
	public boolean isApplicable(JsonObject object) {
		return object.has("type") &&
				object.get("type").getAsString().equals("friends") &&
				object.has("data") &&
				(object.get("data").getAsJsonObject().has("from"));
	}

	@Override
	public void handle(JsonObject object) {
		String method = object.get("data").getAsJsonObject().get("method").getAsString();
		if (object.get("data").getAsJsonObject().has("from")) {
			String from = object.get("data").getAsJsonObject().get("from").getAsString();
			if (method.equals("add")) {
				API.getInstance().getNotificationProvider().addStatus("api.success.friendAccept", "api.success.friendAccept.desc.name", from);
			} else if (method.equals("decline")) {
				API.getInstance().getNotificationProvider().addStatus("api.success.friendDeclined", "api.success.friendDeclined.desc.name", from);
			}
		}
	}
}
