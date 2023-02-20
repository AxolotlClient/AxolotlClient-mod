package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class APIError {

	public static String fromResponse(JsonObject object) {
		return fromCode(object.get("data").getAsJsonObject().get("message").getAsString());
	}

	public static String fromCode(String errorCode) {
		try {
			ErrorCodes code = ErrorCodes.valueOf(errorCode.split(":")[0]);
			return API.getInstance().getTranslationProvider().translate(code.getTranslationKey());
		} catch (IllegalArgumentException e) {
			API.getInstance().getLogger().error("Error code " + errorCode + " not found! Report this IMMEDIATELY!");
			return errorCode;
		}
	}

	@RequiredArgsConstructor
	private enum ErrorCodes {
		USER_NOT_FOUND("api.error.userNotFound"),
		FRIEND_REQUEST_NOT_FOUND("api.error.friendRequestNotFound"),
		USER_BLOCKED("api.error.userBlocked"),
		USER_ALREADY_BLOCKED("api.error.userAlreadyBlocked"),
		//USER_ALREADY_EXISTS("api.error.userAlreadyExists"), // ??????
		USER_ALREADY_FRIENDS("api.error.userAlreadyFriends");
		@Getter
		private final String translationKey;
	}
}
