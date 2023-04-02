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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.requests.StatusUpdate;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.RequestHandler;

public class StatusUpdateHandler implements RequestHandler {
	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("statusUpdate") && API.getInstance().getApiOptions().statusUpdateNotifs.get();
	}

	@Override
	public void handle(JsonObject object) {
		String uuid = object.get("data").getAsJsonObject().get("uuid").getAsString();
		AtomicReference<User> user = new AtomicReference<>();
		FriendHandler.getInstance().getFriends(list -> user.set(list.stream().filter(u -> u.getUuid().equals(uuid)).collect(Collectors.toList()).get(0)));
		StatusUpdate.Type type = Arrays.stream(StatusUpdate.Type.values()).filter(u -> u.getIdentifier().equals(object.get("data").getAsJsonObject().get("updateType")
			.getAsString())).collect(Collectors.toList()).get(0);
		if (type == StatusUpdate.Type.ONLINE) {
			API.getInstance().getNotificationProvider()
				.addStatus("api.friends", "api.friends.statusChange.online",
					user.get().getName());
		} else if (type == StatusUpdate.Type.OFFLINE) {
			API.getInstance().getNotificationProvider()
				.addStatus("api.friends", "api.friends.statusChange.offline",
					user.get().getName());
		} else if (type == StatusUpdate.Type.IN_GAME || type == StatusUpdate.Type.IN_GAME_UNKNOWN) {
			API.getInstance().getNotificationProvider()
				.addStatus("api.friends", "api.friends.statusChange.inGame",
					user.get().getName(), user.get().getStatus().getTitle());
		}
	}
}
