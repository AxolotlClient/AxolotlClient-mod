package io.github.axolotlclient.util.events.impl;

import com.mojang.blaze3d.platform.InputUtil;
import lombok.Data;

@Data
public class KeyBindChangeEvent {

	private final InputUtil.Key boundKey;

}
