/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
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
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.util;

import com.mojang.blaze3d.platform.InputUtil;
import net.minecraft.client.option.KeyBind;
import org.quiltmc.qsl.base.api.event.Event;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class Hooks {

    public interface MouseInputCallback {

        void onMouseButton(long window, int button, int action, int mods);
    }

    public static final Event<MouseInputCallback> MOUSE_INPUT = Event.create(MouseInputCallback.class,
            listeners -> ((window, button, action, mods) -> {
                for (MouseInputCallback listener : listeners) {
                    listener.onMouseButton(window, button, action, mods);
                }
            }));

    public interface ChangeBind {

        void setBoundKey(InputUtil.Key boundKey);
    }

    public static final Event<ChangeBind> KEYBIND_CHANGE = Event.create(ChangeBind.class, listeners -> ((key) -> {
        for (ChangeBind listener : listeners) {
            listener.setBoundKey(key);
        }
    }));

    public interface OnPress {

        void onPress(KeyBind binding);
    }

    public static final Event<OnPress> KEYBIND_PRESS = Event.create(OnPress.class, listeners -> ((key) -> {
        for (OnPress listener : listeners) {
            listener.onPress(key);
        }
    }));

    public static final Event<PlayerDirectionCallback> PLAYER_DIRECTION_CHANGE = Event
            .create(PlayerDirectionCallback.class, listeners -> ((prevPitch, prevYaw, pitch, yaw) -> {
                for (PlayerDirectionCallback listener : listeners) {
                    listener.onChange(prevPitch, prevYaw, pitch, yaw);
                }
            }));

    public interface PlayerDirectionCallback {

        void onChange(float prevPitch, float prevYaw, float pitch, float yaw);
    }
}
