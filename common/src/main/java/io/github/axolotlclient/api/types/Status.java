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

package io.github.axolotlclient.api.types;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.util.GsonHelper;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Status {

	public static final Status UNKNOWN = new Status("offline", null, Activity.UNKNOWN);

	private String type;
	@Nullable
	private final Instant lastOnline;
	@Nullable
	private Activity activity;

	public boolean isOnline() {
		return "online".equals(type);
	}

	public String getDescription() {
		return activity == null || activity.description.isEmpty() ? "" :
			API.getInstance().getTranslationProvider()
				.translate(activity.description);
	}

	public String getTitle() {
		if (!isOnline()) {
			return API.getInstance().getTranslationProvider().translate("api.status.title.offline");
		}
		return activity == null || activity.title.isEmpty() ? API.getInstance().getTranslationProvider().translate("api.status.title.online") :
			API.getInstance().getTranslationProvider()
				.translate(activity.title);
	}

	public String getLastOnline() {
		return lastOnline == null ? null :
			API.getInstance().getTranslationProvider()
				.translate("api.status.last_online", lastOnline.atZone(ZoneId.systemDefault()).format(AxolotlClientCommon.getInstance().formatter));
	}

	public void setOnline(boolean online) {
		if (online) {
			type = "online";
		} else {
			type = "offline";
		}
	}

	@AllArgsConstructor
	@Getter
	@Accessors(fluent = true)
	@ToString
	@EqualsAndHashCode
	public static final class Activity {
		private static final Activity UNKNOWN = new Activity("", "", null, Instant.EPOCH);
		private final String title;
		private final String description;
		private final Metadata metadata;
		@EqualsAndHashCode.Exclude
		private final Instant started;

		public Activity(String title, String description) {
			this(title, description, (Metadata) null);
		}

		public Activity(String title, String description, Metadata metadata) {
			this(title, description, metadata, Instant.now());
		}

		public Activity(String title, String description, MetadataAttributes attributes) {
			this(title, description, attributes != null ? new Metadata(attributes) : null);
		}

		public boolean hasMetadata() {
			return metadata != null;
		}

		public boolean hasMetadata(String id) {
			return hasMetadata() && metadata.type.equals(id);
		}

		public interface MetadataAttributes {
			String typeId();
		}

		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static class Metadata {
			public final String type;
			public final MetadataAttributes attributes;

			public Metadata(MetadataAttributes attributes) {
				this(attributes.typeId(), attributes);
			}

			public static class MetadataTypeAdapter extends TypeAdapter<Metadata> {

				@Override
				public void write(JsonWriter out, Metadata value) throws IOException {
					if (value == null) {
						out.nullValue();
						return;
					}
					out.beginObject();
					out.name("type").value(value.type);
					out.name("attributes").jsonValue(GsonHelper.GSON.toJson(value.attributes));
					out.endObject();
				}

				@Override
				public Metadata read(JsonReader in) throws IOException {
					if (in.peek() == JsonToken.NULL) {
						return null;
					}
					JsonObject metadataObj = GsonHelper.GSON.fromJson(in, JsonObject.class);
					String type = metadataObj.get("type").getAsString();
					MetadataAttributes attributes = switch (type) {
						case WorldHostMetadata.ID -> GsonHelper.GSON.fromJson(metadataObj.get("attributes"), WorldHostMetadata.class);
						case E4mcMetadata.ID -> GsonHelper.GSON.fromJson(metadataObj.get("attributes"), E4mcMetadata.class);
						case ExternalServerMetadata.ID -> GsonHelper.GSON.fromJson(metadataObj.get("attributes"), ExternalServerMetadata.class);
						default -> throw new IllegalArgumentException("Unsupported attributes id: "+type);
					};
					return new Metadata(type, attributes);
				}
			}
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class WorldHostMetadata implements MetadataAttributes {
			public static final String ID = "world_host";
			private final String connectionId;
			private final String externalIp;
			private final ServerInfo serverInfo;

			@Override
			public String typeId() {
				return ID;
			}

			public E4mcMetadata asE4mcMetadata() {
				return new E4mcMetadata(externalIp, serverInfo);
			}
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class E4mcMetadata implements MetadataAttributes {
			public static final String ID = "e4mc";
			private final String domain;
			private final ServerInfo serverInfo;

			@Override
			public String typeId() {
				return ID;
			}
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class ExternalServerMetadata implements MetadataAttributes {
			public static final String ID = "external_server";
			private final String serverName;
			private final String address;

			@Override
			public String typeId() {
				return ID;
			}
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static class ServerInfo {
			private final String levelName;
			private final String description;
			private final Favicon icon;
			private final Players players;
			private final Version version;

			@AllArgsConstructor
			@Getter
			@Accessors(fluent = true)
			@EqualsAndHashCode
			public static class Favicon {
				private static final String PREFIX = "data:image/png;base64,";
				private final byte[] iconBytes;


				@Override
				public String toString() {
					return PREFIX + new String(Base64.getEncoder().encode(iconBytes), StandardCharsets.UTF_8);
				}

				public static Favicon fromString(String base64String) {
					return new Favicon(Base64.getDecoder().decode(base64String.substring(PREFIX.length()).replaceAll("\n", "").getBytes(StandardCharsets.UTF_8)));
				}

				public static class FaviconTypeAdapter extends TypeAdapter<Favicon> {

					@Override
					public void write(JsonWriter out, Favicon value) throws IOException {
						out.value(value.toString());
					}

					@Override
					public Favicon read(JsonReader in) throws IOException {
						return fromString(in.nextString());
					}
				}
			}

			@AllArgsConstructor
			@Getter
			@Accessors(fluent = true)
			@ToString
			@EqualsAndHashCode
			public static class Players {
				private final int max;
				private final int online;
				private final List<Player> sample;

				@AllArgsConstructor
				@Getter
				@Accessors(fluent = true)
				@ToString
				@EqualsAndHashCode
				public static class Player {
					private final String name;
					private final String uuid;
				}

			}

			@AllArgsConstructor
			@Getter
			@Accessors(fluent = true)
			@ToString
			@EqualsAndHashCode
			public static class Version {
				private final String name;
				private final int protocol;
			}

		}
	}
}
