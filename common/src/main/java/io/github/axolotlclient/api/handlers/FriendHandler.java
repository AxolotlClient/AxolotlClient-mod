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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.Keyword;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.api.util.UUIDHelper;
import lombok.Getter;

public class FriendHandler implements RequestHandler {

	@Getter
	private final static FriendHandler Instance = new FriendHandler();
	private final API api;

	protected FriendHandler() {
		this.api = API.getInstance();
	}

	public void addFriend(String uuid) {
		api.send(new Request(Request.Type.CREATE_FRIEND_REQUEST, object -> {
			if (!API.getInstance().requestFailed(object)) {
				api.getNotificationProvider()
					.addStatus("api.success.requestSent", "api.success.requestSent.desc", UUIDHelper.getUsername(uuid));
			} else {
				APIError.display(object);
			}
		}, "add", uuid));
	}

	public void removeFriend(User user) {
		api.send(new Request(Request.Type.REMOVE_FRIEND, object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.removeFriend", "api.success.removeFriend.desc", user.getName());
			}
		}, API.getInstance().sanitizeUUID(user.getUuid())));
	}

	public void blockUser(String uuid) {
		/*api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.blockUser", "api.success.blockUser.desc", UUIDHelper.getUsername(uuid));
			}
		}, "block", uuid));*/
	}

	public void unblockUser(String uuid) {
		/*api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.unblockUser", "api.success.unblockUser.desc", UUIDHelper.getUsername(uuid));
			}
		}, "unblock", uuid));*/
	}

	public void getFriends(Consumer<List<User>> responseConsumer) {
		api.send(new Request(Request.Type.FRIENDS_LIST, object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				List<User> list = new ArrayList<>();
				for (int i = 0x0E; i <= object.getInt(0x0A); i += 16) {
					getFriendInfo(list::add, getString(object, i, 16));
				}
				responseConsumer.accept(list);
			}
		}));
	}

	public void getFriendInfo(Consumer<User> responseConsumer, String uuid) {
		api.send(new Request(Request.Type.GET_FRIEND, buf -> {

			Instant startTime = Instant.ofEpochSecond(buf.getLong(0x09));

			responseConsumer.accept(new User(uuid,
				new Status(buf.getBoolean(0x0D),
					Keyword.get(getString(buf, 0x0E, 64).trim()),
					Keyword.get(getString(buf, 0x4E, 64).trim()),
					Keyword.get(getString(buf, 0x8E, 32).trim()), startTime)));
		}, uuid));
	}

	public void getFriendRequests(BiConsumer<List<User>, List<User>> responseConsumer) {
		api.send(new Request(Request.Type.GET_FRIEND_REQUESTS, object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				List<User> in = new ArrayList<>();
				List<User> out = new ArrayList<>();
				int i = 0x0E;
				while (i < object.getInt(0x0A)) {
					getFriendInfo(in::add, getString(object, i, 16));
					i += 16;
				}
				int offset = i;
				i += 4;
				while (i < object.getInt(offset)) {
					getFriendInfo(out::add, getString(object, i, 16));
					i += 16;
				}

				responseConsumer.accept(in, out);
			}
		}));
	}

	public void getBlocked(Consumer<List<User>> responseConsumer) {
		/*api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				JsonArray blocked = object.get("data").getAsJsonObject().get("blocked").getAsJsonArray();

				List<User> bl = new ArrayList<>();
				blocked.forEach(e -> bl.add(new User(e.getAsJsonObject().get("uuid").getAsString(), Status.UNKNOWN)));
				responseConsumer.accept(bl);
			}
		}, "getBlocked"));*/
		responseConsumer.accept(Collections.emptyList());
	}

	public boolean isBlocked(String uuid) {
		AtomicBoolean bool = new AtomicBoolean(false);
		getBlocked(list -> bool.set(list.stream().map(User::getUuid).anyMatch(uuid::equals)));
		return bool.get();
	}

	public void acceptFriendRequest(User from) {
		api.send(new Request(Request.Type.FRIEND_REQUEST_REACTION, object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.acceptFriend", "api.success.acceptFriend.desc", from.getName());
			}
		}, new Request.Data(from.getUuid()).add((byte) 1)));
	}

	public void denyFriendRequest(User from) {
		api.send(new Request(Request.Type.FRIEND_REQUEST_REACTION, object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				api.getNotificationProvider().addStatus("api.success.denyFriend", "api.success.denyFriend.desc", from.getName());
			}
		}, new Request.Data(from.getUuid()).add((byte) 0)));
	}
}
