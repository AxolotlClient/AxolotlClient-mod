package io.github.axolotlclient.api.handlers;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.requests.Friends;

import java.util.UUID;

public abstract class FriendHandler {

	private final API api;

	protected FriendHandler() {
		this.api = API.getInstance();
	}

	public void addFriend(UUID uuid) {
		api.send(new Friends(object -> {
			if (!object.get("type").getAsString().equals("error")) {
				System.out.println(object.get("data").getAsJsonObject().get("message").getAsString());
			}
		}, "add", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void removeFriend(UUID uuid) {
		api.send(new Friends(object -> {

		}, "remove", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void blockUser(UUID uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {

			}
		}, "get", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void unblockUser(UUID uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {

			}
		}, "get", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void getFriend(UUID uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {

			}
		}, "get", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void acceptFriendRequest(UUID uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {

			}
		}, "accept", API.getInstance().sanitizeUUID(uuid.toString())));
	}

}
