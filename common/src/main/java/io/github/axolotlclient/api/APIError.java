package io.github.axolotlclient.api;

import com.google.gson.JsonObject;

public class APIError {

	public static String fromCode(String errorCode){
		return errorCode;
	}

	public static String fromResponse(JsonObject object){
		return fromCode(object.get("data").getAsJsonObject().get("message").getAsString());
	}
}
