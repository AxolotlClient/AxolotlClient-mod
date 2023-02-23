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

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.requests.Friends;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.api.util.UUIDHelper;

public class FriendRequestHandler implements RequestHandler {

	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("friends") && object.get("data").getAsJsonObject().get("method").getAsString().equals("request");
	}

	@Override
	public void handle(JsonObject object) {
		JsonObject data = object.get("data").getAsJsonObject();
		String fromUUID = data.get("from").getAsString();
		if(API.getInstance().getApiOptions().friendRequestsEnabled.get()) {
			API.getInstance().getNotificationProvider().addStatus("api.friends", "api.friends.request", UUIDHelper.getUsername(fromUUID));
		} else {
			API.getInstance().send(new Friends(o -> {
				if (API.getInstance().requestFailed(o)) {
					APIError.display(o);
				}
			}, "decline", fromUUID));
		}
	}
}