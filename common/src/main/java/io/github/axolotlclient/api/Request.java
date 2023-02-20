package io.github.axolotlclient.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
		private final JsonObject elements = new JsonObject();

		public Data(String... data) {
			if (data.length % 2 != 0) {
				throw new IllegalArgumentException("Unequal count of arguments!");
			}
			for (int i = 0; i < data.length - 1; i += 2) {
				elements.addProperty(data[i], data[i + 1]);
			}
		}

		public Data addElement(String name, JsonElement object) {
			elements.add(name, object);
			return this;
		}

		public Data addElement(String name, String object) {
			return addElement(name, new JsonPrimitive(object));
		}

		public Data removeElement(String name){
			elements.remove(name);
			return this;
		}

		private JsonObject getJson() {
			return elements;
		}
	}
}
