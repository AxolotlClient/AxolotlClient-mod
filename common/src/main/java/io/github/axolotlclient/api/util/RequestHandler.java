package io.github.axolotlclient.api.util;

import com.google.gson.JsonObject;

public interface RequestHandler {

	boolean isApplicable(JsonObject object);

	void handle(JsonObject object);
}
