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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.RequestHandler;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatHandler implements RequestHandler {

	@Setter
	private Consumer<ChatMessage> messageConsumer = m -> {};
	@Setter
	private Consumer<List<ChatMessage>> messagesConsumer = m -> {};

	@Getter
	private static final ChatHandler Instance = new ChatHandler();

	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("chat");
	}

	@Override
	public void handle(JsonObject object) {
		// TODO implement chat handling
		handleMessage(object, true);
	}

	private void handleMessage(JsonObject object, boolean notify){
		JsonObject data = object.get("data").getAsJsonObject();
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
		ChatMessage message = new ChatMessage(from, data.get("content").getAsString(), data.get("timestamp").getAsLong());
		if(notify){
			API.getInstance().getNotificationProvider().addStatus(API.getInstance().getTranslationProvider().translate("api.chat.newMessageFrom", message.getSender().getName()), message.getContent());
			// TODO
		}
		messageConsumer.accept(message);
	}

	private void handleMessages(JsonObject object){
		List<ChatMessage> list = new ArrayList<>();
		for(JsonElement element : object.get("data").getAsJsonObject().get("messages").getAsJsonArray()){
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
			list.add(new ChatMessage(from, data.get("content").getAsString(), data.get("timestamp").getAsLong()));
		}
		messagesConsumer.accept(list);
	}

	public void sendMessage(User user, String message){
		// TODO chat messages
		API.getInstance().send(new Request("chat", object -> handleMessage(object, false), "method", "add", "content", message, "to", user.getUuid()));
	}

	public void getMessagesBefore(User user, long getBefore){
		API.getInstance().send(new Request("chat", this::handleMessages,
				new Request.Data("method", "get", "user", user.getUuid()).addElement("before", new JsonPrimitive(getBefore))));
	}

	public void getMessagesAfter(User user, long getAfter){
		API.getInstance().send(new Request("chat", this::handleMessages,
				new Request.Data("method", "get", "user", user.getUuid()).addElement("after", new JsonPrimitive(getAfter))));
	}
}
