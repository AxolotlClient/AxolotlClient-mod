package io.github.axolotlclient.util.events.impl;

import lombok.Data;

@Data
public class MouseInputEvent {

	private final long window;
	private final int button, action, mods;
}
