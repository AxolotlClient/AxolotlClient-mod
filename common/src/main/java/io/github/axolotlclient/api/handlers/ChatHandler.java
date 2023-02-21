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
import io.github.axolotlclient.api.requests.ChatMessage;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.RequestHandler;
import lombok.Getter;

public class ChatHandler implements RequestHandler {

	@Getter
	private static final ChatHandler Instance = new ChatHandler();

	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("chat");
	}

	@Override
	public void handle(JsonObject object) {
		// TODO implement chat handling
	}

	public void sendMessage(User user, String message){
		// TODO chat messages
		// API.getInstance().send(new ChatMessage((object)->{}));
	}
}
