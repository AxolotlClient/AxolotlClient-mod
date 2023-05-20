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

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.util.BufferUtil;

import java.time.Instant;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class User {

	private static final WeakHashMap<String, io.github.axolotlclient.api.types.User> userCache = new WeakHashMap<>();
	private static final WeakHashMap<String, Boolean> onlineCache = new WeakHashMap<>();

	public static boolean getOnline(String uuid) {
		return onlineCache.computeIfAbsent(uuid, u -> {
			AtomicBoolean result = new AtomicBoolean();
			API.getInstance().send(new Request(Request.Type.USER, buf ->
				result.set(buf.getBoolean(0x09)), u));
			return result.get();
		});
	}

	public static void get(Consumer<io.github.axolotlclient.api.types.User> responseConsumer, String uuid) {
		if (userCache.containsKey(uuid)) {
			responseConsumer.accept(userCache.get(uuid));
		}
		API.getInstance().send(new Request(Request.Type.GET_FRIEND, buf -> {

			Instant startTime = Instant.ofEpochSecond(buf.getLong(0x09));

			io.github.axolotlclient.api.types.User user = new io.github.axolotlclient.api.types.User(uuid,
				new Status(buf.getBoolean(0x09),
					BufferUtil.getString(buf, 0x0A, 64).trim(),
					BufferUtil.getString(buf, 0x4A, 64).trim(),
					BufferUtil.getString(buf, 0x8A, 32).trim(), startTime));
			userCache.put(uuid, user);
			responseConsumer.accept(user);
		}, uuid));
	}
}
