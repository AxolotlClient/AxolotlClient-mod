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

package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class APIError {

	public static String fromResponse(JsonObject object) {
		return fromCode(object.get("data").getAsJsonObject().get("message").getAsString());
	}

	public static String fromCode(String errorCode) {
		try {
			ErrorCodes code = ErrorCodes.valueOf(errorCode.split(":")[0]);
			return API.getInstance().getTranslationProvider().translate(code.getTranslationKey());
		} catch (IllegalArgumentException e) {
			API.getInstance().getLogger().error("Error code " + errorCode + " not found! Report this IMMEDIATELY!");
			return errorCode;
		}
	}

	public static void display(JsonObject object) {
		API.getInstance().getLogger().debug("APIError: " + object);
		API.getInstance().getNotificationProvider().addStatus("api.error.requestGeneric", fromResponse(object));
	}

	@RequiredArgsConstructor
	private enum ErrorCodes {
		USER_NOT_FOUND("api.error.userNotFound"),
		FRIEND_REQUEST_NOT_FOUND("api.error.friendRequestNotFound"),
		USER_BLOCKED("api.error.userBlocked"),
		USER_ALREADY_BLOCKED("api.error.userAlreadyBlocked"),
		USER_ALREADY_FRIENDS("api.error.userAlreadyFriends"),
		MALFORMED_PACKET("api.error.packetMalformed");
		@Getter
		private final String translationKey;
	}
}
