package io.github.axolotlclient.api.handlers;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.channels.Friends;

import java.util.UUID;

public abstract class FriendHandler {

	private final API api;

	protected FriendHandler() {
		this.api = API.getInstance();
	}

	public void addFriend(UUID uuid){
		api.send(new Friends(object -> {
			if(!object.get("type").getAsString().equals("error")){
				System.out.println(object.get("data").getAsJsonObject().get("message").getAsString());
			}
		},"add", uuid.toString().replace("-", "")));
	}

}
