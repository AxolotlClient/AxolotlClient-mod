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
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.BufferUtil;
import io.github.axolotlclient.api.util.RequestHandler;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ChatHandler implements RequestHandler {

	public static final Consumer<ChatMessage> DEFAULT_MESSAGE_CONSUMER = message -> {
	};
	public static final Consumer<List<ChatMessage>> DEFAULT_MESSAGES_CONSUMER = messages -> {
	};
	public static final NotificationsEnabler DEFAULT = message -> true;
	@Getter
	private static final ChatHandler Instance = new ChatHandler();
	@Setter
	private Consumer<ChatMessage> messageConsumer = DEFAULT_MESSAGE_CONSUMER;
	@Setter
	private Consumer<List<ChatMessage>> messagesConsumer = DEFAULT_MESSAGES_CONSUMER;
	@Setter
	private NotificationsEnabler enableNotifications = DEFAULT;

	@Override
	public boolean isApplicable(int packetType) {
		return packetType == Request.Type.SEND_MESSAGE.getType();
	}

	@Override
	public void handle(ByteBuf buf) {

		AtomicReference<User> u = new AtomicReference<>();
		io.github.axolotlclient.api.requests.User.get(u::set, BufferUtil.getString(buf, 0x09, 16));

		ChatMessage message = new ChatMessage(u.get(), BufferUtil.getString(buf, 0x26, buf.getInt(0x22)),
			ChatMessage.Type.fromCode(buf.getByte(0x21)), buf.getLong(0x19));

		if (enableNotifications.showNotification(message)) {
			API.getInstance().getNotificationProvider().addStatus(API.getInstance().getTranslationProvider().translate("api.chat.newMessageFrom", message.getSender().getName()), message.getContent());
			// TODO
		}
		messageConsumer.accept(message);

		// TODO implement chat handling
	}

	public void sendMessage(Channel channel, String message) {
		// TODO chat messages
		API.getInstance().send(new Request(Request.Type.SEND_MESSAGE, buf -> {
		},
			new Request.Data(channel.getId()).add(
				(byte) Instant.now().getEpochSecond()).add((byte) message.length()).add(message)));
		messageConsumer.accept(new ChatMessage(API.getInstance().getSelf(), message, Instant.now().getEpochSecond()));
	}

	public void getMessagesBefore(Channel channel, long getBefore) {
		// TODO wait for implementation on the server side
		//API.getInstance().send(new Request(Request.Type.GET_MESSAGES, this::handleMessages, (byte) 25));
		//API.getInstance().send(ChannelRequest.getMessagesBefore(this::handleMessages, 25, getBefore, ChannelRequest.Include.USER));
		/*API.getInstance().send(new Request("chat", this::handleMessages,
				new Request.Data("method", "get", "user", user.getUuid()).addElement("before", new JsonPrimitive(getBefore))));*/
	}

	private void handleMessages(ByteBuf object) {
		if (!API.getInstance().requestFailed(object)) {
			List<ChatMessage> list = new ArrayList<>();
			if (object.get("data").getAsJsonObject().get("method").getAsString().equals("messages")) {
				for (JsonElement element : object.get("data").getAsJsonObject().get("messages").getAsJsonArray()) {
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
		} else {
			APIError.display(object);
		}
	}

	public void getMessagesAfter(User user, long getAfter) {
		API.getInstance().send(new Request("chat", this::handleMessages,
			new Request.Data("method", "get", "user", user.getUuid()).addElement("after", new JsonPrimitive(getAfter))));
	}

	public interface NotificationsEnabler {
		boolean showNotification(ChatMessage message);
	}
}
