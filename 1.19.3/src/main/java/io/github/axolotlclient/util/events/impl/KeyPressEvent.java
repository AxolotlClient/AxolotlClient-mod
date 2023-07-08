package io.github.axolotlclient.util.events.impl;

import lombok.Data;
import net.minecraft.client.option.KeyBind;
@Data
public class KeyPressEvent {

	private final KeyBind key;
}
