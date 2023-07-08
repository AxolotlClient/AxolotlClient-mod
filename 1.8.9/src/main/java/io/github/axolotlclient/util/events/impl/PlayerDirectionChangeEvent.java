package io.github.axolotlclient.util.events.impl;

import lombok.Data;

@Data
public class PlayerDirectionChangeEvent {

	private final float prevPitch, prevYaw, pitch, yaw;

}
