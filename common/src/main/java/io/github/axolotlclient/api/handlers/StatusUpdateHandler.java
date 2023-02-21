package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.requests.StatusUpdate;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.RequestHandler;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StatusUpdateHandler implements RequestHandler {
	@Override
	public boolean isApplicable(JsonObject object) {
		return object.get("type").getAsString().equals("statusUpdate");
	}

	@Override
	public void handle(JsonObject object) {
		String uuid = object.get("data").getAsJsonObject().get("uuid").getAsString();
		AtomicReference<User> user = new AtomicReference<>();
		FriendHandler.getInstance().getFriends(list -> user.set(list.stream().filter(u -> u.getUuid().equals(uuid)).collect(Collectors.toList()).get(0)));
		StatusUpdate.Type type = Arrays.stream(StatusUpdate.Type.values()).filter(u -> u.getIdentifier().equals(object.get("data").getAsJsonObject().get("updateType")
				.getAsString())).collect(Collectors.toList()).get(0);
		if(type == StatusUpdate.Type.ONLINE) {
			API.getInstance().getNotificationProvider()
					.addStatus("api.friends", "api.friends.statusChange.online",
							user.get().getName());
		} else if (type == StatusUpdate.Type.OFFLINE){
			API.getInstance().getNotificationProvider()
					.addStatus("api.friends", "api.friends.statusChange.offline",
							user.get().getName());
		} else if (type == StatusUpdate.Type.IN_GAME || type == StatusUpdate.Type.IN_GAME_UNKNOWN){
			API.getInstance().getNotificationProvider()
					.addStatus("api.friends", "api.friends.statusChange.inGame",
							user.get().getName(), user.get().getStatus().getTitle());
		}
	}
}
