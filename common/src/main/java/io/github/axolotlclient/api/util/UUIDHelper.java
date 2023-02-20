package io.github.axolotlclient.api.util;

import com.google.gson.JsonElement;
import io.github.axolotlclient.util.NetworkUtil;

import java.io.IOException;

public class UUIDHelper {

	public static String getUsername(String uuid){
		try {
			JsonElement e = NetworkUtil.getRequest("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid, NetworkUtil.createHttpClient("API"));
			return e.getAsJsonObject().get("name").getAsString();
		} catch (IOException ignored) {

		}
		return uuid;
	}

	public static String getUuid(String username) {
		try {
			JsonElement response = NetworkUtil.getRequest("https://api.mojang.com/users/profiles/minecraft/" + username, NetworkUtil.createHttpClient("API"));
			return response.getAsJsonObject().get("id").getAsString();
		} catch (IOException ignored) {

		}
		return username;
	}
}
