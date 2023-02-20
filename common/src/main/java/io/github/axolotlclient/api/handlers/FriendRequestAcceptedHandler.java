package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.util.RequestHandler;

public class FriendRequestAcceptedHandler implements RequestHandler {
	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("friends") && object.get("data").getAsJsonObject().get("method").getAsString().equals("accept");
	}

	@Override
	public void handle(JsonObject object) {
		String fromUUID = object.get("data").getAsJsonObject().get("from").getAsString();
		API.getInstance().getNotificationProvider().addStatus("api.success.friendRequest", "api.success.friendRequest", fromUUID);
	}
}
