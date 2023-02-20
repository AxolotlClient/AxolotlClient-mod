package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.util.RequestHandler;

public class FriendRequestHandler implements RequestHandler {

	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("friends") && object.get("data").getAsJsonObject().get("method").getAsString().equals("request");
	}

	@Override
	public void handle(JsonObject object) {
		JsonObject data = object.get("data").getAsJsonObject();
		String fromUUID = data.get("from").getAsString();
		String fromUsername = data.get("username").getAsString();
		API.getInstance().getNotificationProvider().addStatus("api.friendrequest.title", "api.friendrequest.desc");
	}
}
