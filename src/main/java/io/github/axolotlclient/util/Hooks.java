package io.github.axolotlclient.util;

import net.legacyfabric.fabric.api.event.Event;
import net.legacyfabric.fabric.api.event.EventFactory;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class Hooks {

    public static final Event<MouseInputCallback> MOUSE_INPUT = EventFactory.createArrayBacked(MouseInputCallback.class, listeners -> ((window, button, action, mods) -> {
        for (MouseInputCallback listener : listeners) {
            listener.onMouseButton(window, button, action, mods);
        }
    }));

    public static final Event<KeyBindingCallback.OnPress> KEYBIND_PRESS = EventFactory.createArrayBacked(KeyBindingCallback.OnPress.class, listeners -> ((key) -> {
        for (KeyBindingCallback.OnPress listener : listeners) {
            listener.onPress(key);
        }
    }));
}
