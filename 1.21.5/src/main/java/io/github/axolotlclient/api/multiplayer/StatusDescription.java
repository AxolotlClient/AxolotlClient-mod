package io.github.axolotlclient.api.multiplayer;

import java.util.Map;
import java.util.Objects;

import io.github.axolotlclient.util.GsonHelper;

public class StatusDescription {
	private final String serverIp;
	private final String serverName;

	public StatusDescription(String serverIp, String serverName) {
		this.serverIp = serverIp;
		this.serverName = serverName;
	}

	@SuppressWarnings("unchecked")
	public static StatusDescription read(String json) {
		try {
			var map = (Map<String, String>) GsonHelper.read(json);
			return new StatusDescription(map.get("server_ip"), map.get("server_name"));
		} catch (Exception e) {
			return null;
		}
	}

	public String serverIp() {
		return serverIp;
	}

	public String serverName() {
		return serverName;
	}
}
