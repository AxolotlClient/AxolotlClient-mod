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

package io.github.axolotlclient.api.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class User extends Request {
	protected User(Consumer<JsonObject> handler, JsonArray uuids) {
		super("user", handler, new Data("method", "get").addElement("users", uuids));
	}

	public static Request getUsers(Consumer<Map<String, Boolean>> result, String... uuids){

		JsonArray array = new JsonArray();
		for(String s: uuids){
			array.add(new JsonPrimitive(s));
		}
		Consumer<JsonObject> consumer = object -> {
			if(!API.getInstance().requestFailed(object)) {
				Map<String, Boolean> res = new HashMap<>();
				JsonArray users = object.get("data").getAsJsonObject().get("users").getAsJsonArray();
				users.forEach(element ->
						res.put(element.getAsJsonObject().get("uuid").getAsString(),
						element.getAsJsonObject().get("online").getAsBoolean()));
				result.accept(res);
			}
		};
		return new User(consumer, array);
	}

	public static boolean getOnline(String uuid){
		AtomicBoolean result = new AtomicBoolean();
		API.getInstance().send(getUsers(map -> result.set(map.get(uuid)), uuid));
		return result.get();
	}
}
