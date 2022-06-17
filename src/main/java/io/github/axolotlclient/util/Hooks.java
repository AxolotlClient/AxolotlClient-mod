package io.github.axolotlclient.util;

import org.quiltmc.qsl.base.api.event.Event;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class Hooks {

    public static final Event<MouseInputCallback> MOUSE_INPUT = Event.create(MouseInputCallback.class, listeners -> ((window, button, action, mods) -> {
        for (MouseInputCallback listener : listeners) {
            listener.onMouseButton(window, button, action, mods);
        }
    }));

	public static final Event<KeyBindingCallback.ChangeBind> KEYBIND_CHANGE = Event.create(KeyBindingCallback.ChangeBind.class, listeners -> ((key) -> {
		for (KeyBindingCallback.ChangeBind listener : listeners) {
			listener.setBoundKey(key);
		}
	}));

    public static final Event<KeyBindingCallback.OnPress> KEYBIND_PRESS = Event.create(KeyBindingCallback.OnPress.class, listeners -> ((key) -> {
        for (KeyBindingCallback.OnPress listener : listeners) {
            listener.onPress(key);
        }
    }));
}
