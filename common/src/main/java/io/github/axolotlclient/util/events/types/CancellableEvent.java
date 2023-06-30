package io.github.axolotlclient.util.events.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class CancellableEvent {

	@Getter @Setter
	private boolean cancelled;

}
