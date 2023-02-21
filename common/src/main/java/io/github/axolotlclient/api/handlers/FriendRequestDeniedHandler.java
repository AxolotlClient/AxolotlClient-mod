package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.api.util.UUIDHelper;

public class FriendRequestDeniedHandler implements RequestHandler {
	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("friends") && object.get("data").getAsJsonObject().get("method").getAsString().equals("decline");
	}

	@Override
	public void handle(JsonObject object) {
		String fromUUID = object.get("data").getAsJsonObject().get("from").getAsString();
		API.getInstance().getNotificationProvider().addStatus("api.friends", "api.friends.request.declined", UUIDHelper.getUsername(fromUUID));
	}
}
