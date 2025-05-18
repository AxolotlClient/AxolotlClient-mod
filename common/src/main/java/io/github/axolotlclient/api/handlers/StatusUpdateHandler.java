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

package io.github.axolotlclient.api.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Response;
import io.github.axolotlclient.api.requests.UserRequest;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.SocketMessageHandler;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.util.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

public class StatusUpdateHandler implements SocketMessageHandler {

	private static final Map<String, Consumer<User>> updateListeners = new HashMap<>();

	public static void addUpdateListener(String id, Consumer<User> listener) {
		updateListeners.put(id, listener);
	}

	public static void removeUpdateListener(String id) {
		updateListeners.remove(id);
	}

	@Override
	public boolean isApplicable(String target) {
		return "activity_update".equals(target) && API.getInstance().getApiOptions().statusUpdateNotifs.get();
	}

	@AllArgsConstructor
	@Getter
	@Accessors(fluent = true)
	@ToString
	@EqualsAndHashCode
	private static class StatusUpdateMessage {
		private final String user;
		private final Status.Activity activity;
	}

	@Override
	public void handle(Response response) {
		var status = GsonHelper.GSON.fromJson(response.getPlainBody(), StatusUpdateMessage.class);

		Status.Activity activity = status.activity();
		notification("api.friends.activity.update", translate(activity.title()) + ": " + translate(activity.description()), UUIDHelper.tryGetUsernameAsync(status.user()).join());
		UserRequest.get(status.user()).thenAccept(u -> {
			User user = u.orElseThrow();
			user.getStatus().setOnline(true);
			user.getStatus().setActivity(activity);
			updateListeners.values().forEach(c -> c.accept(user));
		});
	}
}
