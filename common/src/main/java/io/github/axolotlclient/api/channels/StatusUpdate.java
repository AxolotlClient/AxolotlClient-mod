package io.github.axolotlclient.api.channels;

import io.github.axolotlclient.api.Request;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class StatusUpdate extends Request {

	public StatusUpdate(Type updateType) {
		super("statusUpdate", object -> {
			// not yet implemented, the response is unclear
		}, "updateType", updateType.getIdentifier());
	}

	@RequiredArgsConstructor
	public enum Type {
		ONLINE("online"),
		OFFLINE("offline"),
		IN_GAME("inGame"),
		IN_GAME_UNKNOWN("inGameUnknown");

		@Getter
		private final String identifier;
	}
}
