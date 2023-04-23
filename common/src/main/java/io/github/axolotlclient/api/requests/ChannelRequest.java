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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChannelRequest extends Request {

	public ChannelRequest(Consumer<JsonObject> handler, Data data) {
		super("channel", handler, data);
	}

	public static ChannelRequest getById(Consumer<Channel> handler, String id, Include include) {
		return new ChannelRequest(object -> handler.accept(parseChannelResponse(object)), new Data("method", "get", "id", id, "include", include.getIdentifier()));
	}

	private static Channel parseChannelResponse(JsonObject object) {
		if (API.getInstance().requestFailed(object)) {
			APIError.display(object);
			return null;
		}
		return parseChannel(object.get("data").getAsJsonObject().get("channel").getAsJsonObject());
	}

	private static Channel parseChannel(JsonObject channel) {
		String id = channel.get("id").getAsString();
		String type = channel.get("type").getAsString();
		JsonArray u = channel.get("users").getAsJsonArray();
		List<User> users = new ArrayList<>();
		u.forEach(e -> {
			JsonObject s = e.getAsJsonObject().get("status").getAsJsonObject();
			Instant startedAt;
			if (s.has("startedAt")) {
				startedAt = Instant.ofEpochSecond(s.get("startedAt").getAsLong());
			} else {
				startedAt = Instant.ofEpochSecond(0);
			}
			Status status = new Status(s.get("online").getAsBoolean(), s.get("title").getAsString(),
				s.get("description").getAsString(), s.get("icon").getAsString(), startedAt);
			users.add(new User(e.getAsJsonObject().get("uuid").getAsString(), status));
		});
		List<ChatMessage> messages = new ArrayList<>();
		for (JsonElement element : channel.get("messages").getAsJsonArray()) {
			JsonObject data = element.getAsJsonObject();
			JsonObject s = data.get("from").getAsJsonObject().get("status").getAsJsonObject();
			Instant startedAt;
			if (s.has("startedAt")) {
				startedAt = Instant.ofEpochSecond(s.get("startedAt").getAsLong());
			} else {
				startedAt = Instant.ofEpochSecond(0);
			}
			Status status = new Status(s.get("online").getAsBoolean(), s.get("title").getAsString(),
				s.get("description").getAsString(), s.get("icon").getAsString(), startedAt);
			User from = new User(data.get("from").getAsJsonObject().get("uuid").getAsString(), status);
			messages.add(new ChatMessage(from, data.get("content").getAsString(), data.get("timestamp").getAsLong()));
		}
		if (type.equals("dm")) {

			return new Channel.DM(id, users.toArray(new User[0]), messages.toArray(new ChatMessage[0]));
		} else if (type.equals("group")) {
			return new Channel.Group(id, users.toArray(new User[0]), channel.get("name").getAsString(), messages.toArray(new ChatMessage[0]));
		}

		throw new UnsupportedOperationException("Unknown message channel type: " + type);
	}

	public static ChannelRequest getChannelList(Consumer<List<Channel>> handler, String uuid, SortBy sort, Include include) {
		return new ChannelRequest(object -> handler.accept(parseChannels(object)), new Data("method", "get", "user", uuid, "sortBy",
			sort.getIdentifier(), "include", include.getIdentifier()));
	}

	private static List<Channel> parseChannels(JsonObject object) {
		if (API.getInstance().requestFailed(object)) {
			APIError.display(object);
			return Collections.emptyList();
		}
		List<Channel> channelList = new ArrayList<>();
		JsonArray channels = object.get("data").getAsJsonObject().get("channels").getAsJsonArray();
		channels.forEach(e -> channelList.add(parseChannel(e.getAsJsonObject())));
		return channelList;
	}

	public static ChannelRequest getChannelForAllUsers(Consumer<List<Channel>> handler, String[] users, SortBy sort, Include include) {
		JsonArray u = new JsonArray();
		Arrays.stream(users).map(JsonPrimitive::new).forEach(u::add);
		return new ChannelRequest(object -> handler.accept(parseChannels(object)), new Data("method", "get")
			.addElement("users", u)
			.addElement("sortBy", sort.getIdentifier())
			.addElement("include", include.getIdentifier()));
	}

	public static ChannelRequest getDM(Consumer<Channel> handler, String uuid, Include include) {
		return new ChannelRequest(object -> handler.accept(parseChannelResponse(object)), new Data("method", "getDM", "user", uuid, "include", include.getIdentifier()));
	}

	public static ChannelRequest getLatestMessages(Consumer<JsonObject> handler, int limit, String... include) {
		JsonArray array = new JsonArray();
		Arrays.stream(include).map(JsonPrimitive::new).forEach(array::add);
		return new ChannelRequest(handler, new Data("method", "messages")
			.addElement("limit", new JsonPrimitive(limit)).addElement("include", array));
	}

	public static ChannelRequest getMessagesBefore(Consumer<JsonObject> handler, int limit, long before, Include include) {
		return new ChannelRequest(handler, new Data("method", "messages")
			.addElement("limit", new JsonPrimitive(limit))
			.addElement("before", new JsonPrimitive(before))
			.addElement("include", include.getIdentifier()));
	}

	public static ChannelRequest getMessagesAfter(Consumer<JsonObject> handler, int limit, long after, Include include) {
		return new ChannelRequest(handler, new Data("method", "messages")
			.addElement("limit", new JsonPrimitive(limit))
			.addElement("after", new JsonPrimitive(after))
			.addElement("include", include.getIdentifier()));
	}

	public static ChannelRequest createDM(Consumer<JsonObject> handler, String withUUID) {
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(withUUID));
		array.add(new JsonPrimitive(API.getInstance().getUuid()));
		return new ChannelRequest(handler, new Data("method", "create", "type", "dm", "user", withUUID));
	}

	public static ChannelRequest createGroup(Consumer<JsonObject> handler, String... uuids) {
		JsonArray array = new JsonArray();
		Arrays.stream(uuids).map(JsonPrimitive::new).forEach(array::add);
		array.add(new JsonPrimitive(API.getInstance().getUuid()));
		return new ChannelRequest(handler, new Data("method", "create", "type", "group").addElement("users", array));
	}

	@RequiredArgsConstructor
	public enum SortBy {
		ALPHABETICAL("alphabetical"),
		LAST_MESSAGE("lastMessage");

		@Getter
		private final String identifier;

		@Override
		public String toString() {
			return getIdentifier();
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum Include {

		USER("user"),
		USER_STATUS("user.status"),
		MESSAGES("messages");

		private final String identifier;

		@Override
		public String toString() {
			return getIdentifier();
		}
	}
}
