package io.github.axolotlclient.api.types;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Status {

	private boolean online;
	private String title;
	private String description;
	private String text;
	private String icon;
}
