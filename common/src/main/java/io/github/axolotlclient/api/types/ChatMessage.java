package io.github.axolotlclient.api.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatMessage {

	private final String content;
	private long timestamp;
}
