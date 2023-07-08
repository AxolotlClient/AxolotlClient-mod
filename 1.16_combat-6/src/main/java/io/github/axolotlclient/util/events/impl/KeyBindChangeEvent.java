package io.github.axolotlclient.util.events.impl;

import lombok.Data;
import net.minecraft.client.util.InputUtil;

@Data
public class KeyBindChangeEvent {

	private final InputUtil.Key boundKey;

}
