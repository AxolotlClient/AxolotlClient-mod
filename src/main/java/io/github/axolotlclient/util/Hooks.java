/*
 * This File is part of AxolotlClient (mod)
 * Copyright (C) 2021-present moehreag + Contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
        void setBoundKey(int boundKey);
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
