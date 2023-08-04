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

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;

/**
 * Defines a generic request that can be sent to the backend API.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(doNotUseGetters = true)
public class Request {

	private static final byte[] PACKET_MAGIC = "AXO".getBytes(StandardCharsets.UTF_8);
	private static final int PROTOCOL_VERSION = 0x01;

	@EqualsAndHashCode.Include
	private final Type type;
	@EqualsAndHashCode.Include
	@Getter(AccessLevel.PACKAGE)
	private final int id;
	private final Data data;

	@Getter
	private final Consumer<ByteBuf> handler;

	public Request(Type type, Consumer<ByteBuf> handler, Data data) {
		this.type = type;
		id = generateId();
		this.data = data;
		this.handler = handler;
	}

	public Request(Type type, Consumer<ByteBuf> handler) {
		this(type, handler, new Data());
	}

	public Request(Type type, Consumer<ByteBuf> handler, String... data) {
		this(type, handler, new Data(data));
	}

	public Request(Type type, Consumer<ByteBuf> handler, byte... data) {
		this(type, handler, new Data(data));
	}

	private int generateId() {
		int id = 0;
		while (id == 0) {
			id = ThreadLocalRandom.current().nextInt();
		}
		return id;
	}

	public ByteBuf getData() {
		return Unpooled.buffer()
			.setBytes(0x00, PACKET_MAGIC)
			.setByte(0x03, type.getType())
			.setByte(0x04, PROTOCOL_VERSION)
			.setInt(0x05, id)
			.setBytes(0x06, data.getData());
	}

	@Getter
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

		public Data add(String... a) {
			for (String s : a) {
				add(s);
			}
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

		@Override
		public String toString() {
			return "[" + elements.stream().map(Map.Entry::getValue).map(String::new).collect(Collectors.joining(", ")) + "]";
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
		CREATE_CHANNEL(0x0C),
		GET_OR_CREATE_CHANNEL(0x0D),
		GET_MESSAGES(0x0E),
		GET_CHANNEL_LIST(0x0F),
		SEND_MESSAGE(0x10),
		GET_CHANNEL_BY_ID(0x11),
		GET_PUBLIC_KEY(0x12),
		GET_HYPIXEL_API_KEY(0x13),
		ERROR(0xFF);

		private final int type;
	}
}
