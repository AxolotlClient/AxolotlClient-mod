package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.util.RequestHandler;

public interface FriendRequestHandler extends RequestHandler {

	@Override
	default boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("friends") && object.get("data").getAsJsonObject().get("method").getAsString().equals("request");
	}

	@Override
	default void handle(JsonObject object) {
		JsonObject data = object.get("data").getAsJsonObject();
		String fromUUID = data.get("from").getAsString();
		String fromUsername = data.get("username").getAsString();
		handleIncomingRequest(fromUUID, fromUsername);
	}

	void handleIncomingRequest(String fromUUID, String fromUsername);
}
