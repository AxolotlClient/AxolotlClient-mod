package io.github.axolotlclient.api.types;

import java.util.Arrays;

import io.github.axolotlclient.api.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Channel {

	protected final String name;
	private final String id;
	private final String type;
	private final User[] users;
	private final ChatMessage[] messages;

	public abstract boolean isDM();

	public static class Group extends Channel {

		public Group(String id, User[] users, String name, ChatMessage[] messages) {
			super(id, "group", name, users, messages);
		}

		public boolean isDM() {
			return false;
		}
	}

	public static class DM extends Channel {

		@Getter
		private final User receiver;

		public DM(String id, User[] users, ChatMessage[] messages) {
			super(id, "dm", Arrays.stream(users).filter(user -> !user.getUuid()
				.equals(API.getInstance().getUuid())).map(User::getName).findFirst().orElse(""), users, messages);
			receiver = Arrays.stream(users).filter(user -> !user.getUuid()
				.equals(API.getInstance().getUuid())).findFirst().orElse(null);
		}

		public boolean isDM() {
			return true;
		}
	}
}
