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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Defines a generic request that can be sent to the backend API.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Request {

	private static final byte[] PACKET_MAGIC = "AXO".getBytes(StandardCharsets.UTF_8);
	private static final int PROTOCOL_VERSION = 0x01;

	@EqualsAndHashCode.Include
	private final int type;
	private final int id;
	private final Data data;

	private final Consumer<ByteBuf> handler;

	public Request(Type type, Consumer<ByteBuf> handler, Data data) {
		this.type = type.getType();
		id = generateId();
		this.data = data;
		this.handler = handler;
	}

	private int generateId() {
		int id = 0;
		while (id == 0) {
			id = ThreadLocalRandom.current().nextInt();
		}
		return id;
	}

	public Request(Type type, Consumer<ByteBuf> handler, String... data) {
		this(type, handler, new Data(data));
	}

	public Request(Type type, Consumer<ByteBuf> handler, byte... data) {
		this(type, handler, new Data(data));
	}

	public ByteBuf getData() {
		return Unpooled.buffer()
			.setBytes(0x00, PACKET_MAGIC)
			.setByte(0x03, type)
			.setByte(0x04, PROTOCOL_VERSION)
			.setInt(0x05, id)
			.setBytes(0x06, data.getData());
	}

	@Getter
	@ToString
	public static class Data {
		private final List<Map.Entry<Integer, byte[]>> elements = new ArrayList<>();

		public Data(String... data) {
			for (String s : data) {
				add(s);
			}
		}

		public Data(byte b) {
			add(b);
		}

		public Data(byte[] data) {
			add(data);
		}

		public Data add(String e) {
			add(e.getBytes(StandardCharsets.UTF_8));
			return this;
		}

		public Data add(byte b) {
			return add(new byte[]{b});
		}

		public Data add(byte[] data) {
			int size = elements.size();
			int index = size + (size > 0 ? elements.get(size - 1).getValue().length : 0);
			elements.add(new AbstractMap.SimpleImmutableEntry<>(index, data));
			return this;
		}

		private ByteBuf getData() {
			ByteBuf buf = Unpooled.buffer();
			for (Map.Entry<Integer, byte[]> e : elements) {
				buf.setBytes(e.getKey(), e.getValue());
			}
			return buf;
		}
	}

	/**
	 * Defines human-readable names for all request types.
	 */
	@RequiredArgsConstructor
	@Getter
	public enum Type {
		HANDSHAKE(0x01),
		GLOBAL_DATA(0x02),
		FRIENDS_LIST(0x03),
		GET_FRIEND(0x04),
		USER(0x05),
		CREATE_FRIEND_REQUEST(0x06),
		FRIEND_REQUEST_REACTION(0x07),
		GET_FRIEND_REQUESTS(0x08),
		REMOVE_FRIEND(0x09),
		INCOMING_FRIEND_REQUEST(0x0A),
		STATUS_UPDATE(0x0B),
		CREATE_CHAT(0x0C),
		GET_CHANNEL(0x0D),
		GET_MESSAGES(0x0E),
		GET_CHANNEL_LIST(0x0F),
		SEND_MESSAGE(0x10),
		ERROR(0xFF);

		private final int type;
	}
}
