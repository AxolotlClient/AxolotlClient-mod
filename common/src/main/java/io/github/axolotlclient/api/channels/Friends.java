package io.github.axolotlclient.api.channels;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.Request;

import java.util.function.Consumer;

public class Friends extends Request {

	public Friends(Consumer<JsonObject> consumer, String method, String uuid) {
		super("friends", consumer, "method", method, "uuid", uuid);
	}
}
