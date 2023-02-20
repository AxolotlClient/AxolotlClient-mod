package io.github.axolotlclient.api.types;

import io.github.axolotlclient.api.util.UUIDHelper;
import lombok.Data;

@Data
public class User {

	public User(String uuid, Status status) {
		this.uuid = uuid;
		this.status = status;
		this.name = UUIDHelper.getUsername(uuid);
	}

	private String name;
	private String uuid;
	private Status status;
}
