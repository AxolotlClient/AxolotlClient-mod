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
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ChannelRequest {

	public static Request getById(Consumer<Channel> handler, String id) {
		return new Request(Request.Type.GET_CHANNEL, object -> handler.accept(parseChannelResponse(object)), id);
	}

	private static Channel parseChannelResponse(ByteBuf object) {
		if (API.getInstance().requestFailed(object)) {
			APIError.display(object);
			return null;
		}
		return parseChannel(object);
	}

	private static Channel parseChannel(ByteBuf channel) {
		String id = BufferUtil.getString(channel, 0x09, 5);
		String name = BufferUtil.getString(channel, 0x0E, 64).trim();

		List<User> users = new ArrayList<>();
		int i = 0x4E;
		while (i < channel.getInt(0x53)) {
			String uuid = BufferUtil.getString(channel, i, 16);
			io.github.axolotlclient.api.requests.User.get(users::add, uuid);
			i += 16;
		}
		List<ChatMessage> messages = new ArrayList<>();
		int offset = i + 8;
		while (i < channel.getInt(offset)) {
			messages.add(parseMessage(channel.slice(i, 0x1D + channel.getInt(i + 0x19))));
			i += 0x1D + channel.getInt(i + 0x19);
		}


		if (users.size() == 1) {
			return new Channel.DM(id, users.toArray(new User[0]), messages.toArray(new ChatMessage[0]));
		} else if (users.size() > 1) {
			return new Channel.Group(id, users.toArray(new User[0]), name, messages.toArray(new ChatMessage[0]));
		}

		throw new UnsupportedOperationException("Unknown message channel type: " + channel.toString(StandardCharsets.UTF_8));
	}

	private static ChatMessage parseMessage(ByteBuf buf) {
		AtomicReference<User> u = new AtomicReference<>();
		io.github.axolotlclient.api.requests.User.get(u::set, BufferUtil.getString(buf, 0x00, 16));

		return new ChatMessage(u.get(), BufferUtil.getString(buf, 0x1D, buf.getInt(0x19)),
			ChatMessage.Type.fromCode(buf.getByte(0x18)), buf.getLong(0x10));
	}

	public static Request getChannelList(Consumer<List<Channel>> handler) {
		return new Request(Request.Type.GET_CHANNEL_LIST, object -> handler.accept(parseChannels(object)));
	}

	private static List<Channel> parseChannels(ByteBuf object) {
		if (API.getInstance().requestFailed(object)) {
			APIError.display(object);
			return Collections.emptyList();
		}
		List<Channel> channelList = new ArrayList<>();

		int i = object.getInt(0x0D);
		while (i < object.getInt(0x09)) {
			API.getInstance().send(getById(channelList::add, BufferUtil.getString(object, i, 5)));
			i += 5;
		}

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
