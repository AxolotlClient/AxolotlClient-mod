package io.github.axolotlclient.api.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.requests.Friends;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.util.RequestHandler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FriendHandler implements RequestHandler {

	private final API api;

	@Getter
	private final static FriendHandler Instance = new FriendHandler();

	protected FriendHandler() {
		this.api = API.getInstance();
	}

	public void addFriend(String uuid) {
		api.send(new Friends(object -> {
			if (!API.getInstance().requestFailed(object)) {
				System.out.println(object.get("data").getAsJsonObject().get("message").getAsString());
			} else {
				APIError.display(object);
			}
		}, "add", uuid));
	}

	public void removeFriend(User user){
		api.send(new Friends(object -> {
			if(API.getInstance().requestFailed(object)){
				APIError.display(object);
			} else {

			}
		}, "remove", API.getInstance().sanitizeUUID(user.getUuid())));
	}
	public void removeFriend(UUID uuid) {
		api.send(new Friends(object -> {
			if(API.getInstance().requestFailed(object)){
				APIError.display(object);
			} else {

			}
		}, "remove", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void blockUser(UUID uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			}
		}, "block", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void unblockUser(UUID uuid) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			}
		}, "unblock", API.getInstance().sanitizeUUID(uuid.toString())));
	}

	public void getFriends(Consumer<List<User>> responseConsumer) {
		/*api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				List<User> list = new ArrayList<>();
				JsonArray friends = object.get("data").getAsJsonObject().get("friends").getAsJsonArray();
				friends.forEach(e -> {
					JsonObject s = e.getAsJsonObject().get("status").getAsJsonObject();
					Status status = new Status(s.get("online").getAsBoolean(), s.get("title").getAsString(), s.get("description").getAsString(), s.get("text").getAsString(), s.get("icon").getAsString());
					list.add(new User(e.getAsJsonObject().get("uuid").getAsString(), status));
				});
				responseConsumer.accept(list);
			}
		}, "get"));*/
	}

	public void getFriendRequests(BiConsumer<List<User>, List<User>> responseConsumer) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				JsonArray incoming = object.get("data").getAsJsonObject().get("incoming").getAsJsonArray();
				JsonArray outgoing = object.get("data").getAsJsonObject().get("outgoing").getAsJsonArray();

				List<User> in = new ArrayList<>();
				List<User> out = new ArrayList<>();
				incoming.forEach(e -> in.add(new User(e.getAsJsonObject().get("uuid").getAsString(), null)));
				outgoing.forEach(e -> out.add(new User(e.getAsJsonObject().get("uuid").getAsString(), null)));
				responseConsumer.accept(in, out);
			}
		}, "getRequests"));
	}

	public void getBlocked(Consumer<List<User>> responseConsumer) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			} else {
				JsonArray blocked = object.get("data").getAsJsonObject().get("data").getAsJsonObject().get("blocked").getAsJsonArray();

				List<User> bl = new ArrayList<>();
				blocked.forEach(e -> bl.add(new User(e.getAsJsonObject().get("uuid").getAsString(), null)));
				responseConsumer.accept(bl);
			}
		}, "getBlocked"));
	}

	public void acceptFriendRequest(UUID from) {
		api.send(new Friends(object -> {
			if (API.getInstance().requestFailed(object)) {
				APIError.display(object);
			}
		}, "accept", API.getInstance().sanitizeUUID(from.toString())));
	}

	@Override
	public boolean isApplicable(JsonObject object) {
		return object.has("type") &&
				object.get("type").getAsString().equals("friends") &&
				object.has("data") &&
				(object.get("data").getAsJsonObject().has("from") ||
						object.get("data").getAsJsonObject().has("success"));
	}

	@Override
	public void handle(JsonObject object) {
		String method = object.get("data").getAsJsonObject().get("method").getAsString();
		if(object.get("data").getAsJsonObject().has("from")) {
			String from = object.get("data").getAsJsonObject().get("from").getAsString();
			if(method.equals("add")) {
				API.getInstance().getNotificationProvider().addStatus("api.success.friendAccept", "api.success.friendAccept.desc.name", from);
			} else if(method.equals("decline")) {
				API.getInstance().getNotificationProvider().addStatus("api.success.friendDeclined", "api.success.friendDeclined.desc.name", from);
				// When does this response get send??
			}
		} else if(object.get("data").getAsJsonObject().has("success")) {
			boolean success = object.get("data").getAsJsonObject().get("success").getAsBoolean();
			if(method.equals("add")) {
				if(success) {
					API.getInstance().getNotificationProvider().addStatus("api.success.friendAccept", "api.success.friendAccept.desc");
				} else {

				}
			} else if(method.equals("decline")){
				if(success){

				} else {

				}
			}
		}
	}
}
