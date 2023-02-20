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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class Request {

	private final String id = randomKey(6);
	private final String type;
	private final Consumer<JsonObject> handler;
	private final Data data;

	public Request(String type, Consumer<JsonObject> handler, String... data) {
		this.type = type;
		this.data = new Data(data);
		this.handler = handler;
	}

	public static String randomKey(int length) {
		final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < length; i++) {
			key.append(chars.charAt((int) Math.floor(Math.random() * chars.length())));
		}
		return key.toString();
	}

	public String getJson() {
		JsonObject object = new JsonObject();
		object.add("id", new JsonPrimitive(id));
		object.add("type", new JsonPrimitive(type));
		object.add("data", data.getJson());
		object.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
		return object.toString();
	}

	@Getter
	public static class Data {
		private final JsonObject elements = new JsonObject();

		public Data(String... data) {
			if (data.length % 2 != 0) {
				throw new IllegalArgumentException("Unequal count of arguments!");
			}
			for (int i = 0; i < data.length - 1; i += 2) {
				elements.addProperty(data[i], data[i + 1]);
			}
		}

		public Data addElement(String name, JsonElement object) {
			elements.add(name, object);
			return this;
		}

		public Data addElement(String name, String object) {
			return addElement(name, new JsonPrimitive(object));
		}

		public Data removeElement(String name) {
			elements.remove(name);
			return this;
		}

		private JsonObject getJson() {
			return elements;
		}
	}
}
