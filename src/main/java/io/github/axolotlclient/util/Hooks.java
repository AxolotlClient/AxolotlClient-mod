package io.github.axolotlclient.util;

import net.legacyfabric.fabric.api.event.Event;
import net.legacyfabric.fabric.api.event.EventFactory;
import net.minecraft.client.options.KeyBinding;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class Hooks {

    public interface MouseInputCallback {
        void onMouseButton(int button);
    }

    public static final Event<MouseInputCallback> MOUSE_INPUT = EventFactory.createArrayBacked(MouseInputCallback.class, listeners -> (button -> {
        for (MouseInputCallback listener : listeners) {
            listener.onMouseButton(button);
        }
    }));

    public interface ChangeBind {
        void setBoundKey(KeyBinding boundKey);
    }

    public static final Event<ChangeBind> KEYBIND_CHANGE = EventFactory.createArrayBacked(ChangeBind.class, listeners -> ((key) -> {
        for (ChangeBind listener : listeners) {
            listener.setBoundKey(key);
        }
    }));

    public interface OnPress {
        void onPress(KeyBinding binding);
    }

    public static final Event<OnPress> KEYBIND_PRESS = EventFactory.createArrayBacked(OnPress.class, listeners -> ((key) -> {
        for (OnPress listener : listeners) {
            listener.onPress(key);
        }
    }));

    public static final Event<PlayerDirectionCallback> PLAYER_DIRECTION_CHANGE = EventFactory.createArrayBacked(PlayerDirectionCallback.class, listeners -> (
            (prevPitch, prevYaw, pitch, yaw) -> {
                for (PlayerDirectionCallback listener : listeners) {
                    listener.onChange(prevPitch, prevYaw, pitch, yaw);
                }
            }
    ));

    public interface PlayerDirectionCallback {
        void onChange(float prevPitch, float prevYaw, float pitch, float yaw);
    }

}
