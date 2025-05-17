/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.Relation;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.util.GsonHelper;

@SuppressWarnings("UnstableApiUsage")
public class UserRequest {

	private static final Cache<String, Optional<User>> userCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
		.maximumSize(400).build();
	private static final RateLimiter limiter = RateLimiter.create(2);
	private static final Cache<String, Boolean> onlineCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(10, TimeUnit.SECONDS)
		.expireAfterWrite(2, TimeUnit.MINUTES).build();
	private static final Set<String> onlineRequests = Collections.synchronizedSet(new TreeSet<>());

	public static boolean getOnline(String uuid) {

		if (uuid == null || !API.getInstance().isAuthenticated()) {
			return false;
		}

		String sanitized = API.getInstance().sanitizeUUID(uuid);

		if (API.getInstance().getSelf() != null && sanitized.equals(API.getInstance().getSelf().getUuid())) {
			return true;
		}

		if (!onlineCache.asMap().containsKey(sanitized)) {
			if (!onlineRequests.contains(sanitized)) {
				onlineRequests.add(sanitized);
				CompletableFuture.runAsync(() -> {
					limiter.acquire();
					get(sanitized).thenApply(u -> u.isPresent() && u.get().getStatus().isOnline()).thenAccept(b -> onlineCache.put(sanitized, b))
						.thenRun(() -> onlineRequests.remove(sanitized));
				});
			}
			return false;
		}
		return onlineCache.asMap().get(sanitized);
	}

	private static class UserResponse {
		public String uuid;
		public String username;
		public String relation;
		public String registered;
		public Status status;
		public List<String> previousUsernames;

		public User toUser() {
			Instant registered = this.registered != null ? Instant.parse(this.registered) : null;
			List<User.OldUsername> previousNames = previousUsernames != null ? previousUsernames.stream().map(s -> new User.OldUsername(s, true)).toList() : null;
			return new User(uuid, username, Relation.get(relation), registered, this.status, previousNames);
		}
	}

	public static CompletableFuture<Optional<User>> get(String dUuid) {
		final String uuid = API.getInstance().sanitizeUUID(dUuid);
		if (userCache.asMap().containsKey(uuid)) {
			return CompletableFuture.completedFuture(userCache.asMap().get(uuid));
		}
		return API.getInstance().get(Request.Route.USER.builder().path(uuid).build()).thenApply(response -> {
			if (response.isError()) {
				return null;
			}
			UserResponse res = GsonHelper.GSON.fromJson(response.getPlainBody(), UserResponse.class);
			return res.toUser();
		}).thenApply(u -> {
			Optional<User> opt = Optional.ofNullable(u);
			userCache.put(uuid, opt);
			return opt;
		});
	}

	@SuppressWarnings("unchecked")
	public static CompletableFuture<Optional<List<String>>> getUploadedImages(String userUuid) {
		return API.getInstance().get(Request.Route.USER.builder().path(userUuid).path("images").build())
			.thenApply(r -> {
				if (!r.isError()) {
					List<Long> list = (List<Long>) r.getBody();
					return Optional.of(list.stream().map(Long::toUnsignedString).toList());
				}
				return Optional.empty();
			});
	}

}
