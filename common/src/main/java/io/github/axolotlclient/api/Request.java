package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
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
		private final Map<String, String> elements;

		public Data(String... data) {
			elements = new HashMap<>();
			if (data.length % 2 != 0) {
				throw new IllegalArgumentException("Unequal count of arguments!");
			}
			for (int i = 0; i < data.length - 1; i += 2) {
				elements.put(data[i], data[i + 1]);
			}
		}

		public Data(Map<String, String> elements) {
			this.elements = elements;
		}

		public Data addElement(String name, JsonObject object) {
			return addElement(name, object.toString());
		}

		public Data addElement(String name, String object) {
			elements.put(name, object);
			return this;
		}

		private JsonObject getJson() {
			JsonObject object = new JsonObject();
			elements.keySet().forEach(s -> object.add(s, new JsonPrimitive(elements.get(s))));
			return object;
		}
	}
}
