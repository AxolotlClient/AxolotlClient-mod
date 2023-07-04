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

package io.github.axolotlclient.util.events;

import io.github.axolotlclient.util.events.impl.ReceiveChatMessageEvent;
import io.github.axolotlclient.util.events.impl.ScoreboardRenderEvent;
import io.github.axolotlclient.util.events.impl.WorldLoadEvent;
import net.legacyfabric.fabric.api.event.Event;
import net.legacyfabric.fabric.api.event.EventFactory;
import net.minecraft.client.option.KeyBinding;

import java.util.Arrays;

public class Events {

	public static final Event<MouseInputCallback> MOUSE_INPUT = EventFactory.createArrayBacked(MouseInputCallback.class,
		listeners -> (button -> {
			for (MouseInputCallback listener : listeners) {
				listener.onMouseButton(button);
			}
		}));
	public static final Event<ChangeBind> KEYBIND_CHANGE = EventFactory.createArrayBacked(ChangeBind.class,
		listeners -> ((key) -> {
			for (ChangeBind listener : listeners) {
				listener.setBoundKey(key);
			}
		}));
	public static final Event<OnPress> KEYBIND_PRESS = EventFactory.createArrayBacked(OnPress.class,
		listeners -> ((key) -> {
			for (OnPress listener : listeners) {
				listener.onPress(key);
			}
		}));
	public static final Event<PlayerDirectionCallback> PLAYER_DIRECTION_CHANGE = EventFactory
		.createArrayBacked(PlayerDirectionCallback.class, listeners -> ((prevPitch, prevYaw, pitch, yaw) -> {
			for (PlayerDirectionCallback listener : listeners) {
				listener.onChange(prevPitch, prevYaw, pitch, yaw);
			}
		}));

	public static final Event<EventCallback<ScoreboardRenderEvent>> SCOREBOARD_RENDER_EVENT = createEvent();
	public static final Event<EventCallback<ReceiveChatMessageEvent>> RECEIVE_CHAT_MESSAGE_EVENT = createEvent();
	public static final Event<EventCallback<WorldLoadEvent>> WORLD_LOAD_EVENT = createEvent();

	private static <T> Event<EventCallback<T>> createEvent(){
		return EventFactory
			.createArrayBacked(EventCallback.class, listeners -> (event) ->
				Arrays.stream(listeners).forEach(l -> l.invoke(event)));
	}

	public interface EventCallback<T> {
		void invoke(T parameters);
	}

	// TODO migrate all of these to the system above

	public interface MouseInputCallback {

		void onMouseButton(int button);
	}

	public interface ChangeBind {

		void setBoundKey(int boundKey);
	}

	public interface OnPress {

		void onPress(KeyBinding binding);
	}

	public interface PlayerDirectionCallback {

		void onChange(float prevPitch, float prevYaw, float pitch, float yaw);
	}
}
