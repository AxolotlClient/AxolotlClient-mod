/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.axolotlclient.bridge.AxoMinecraftClient;

public class CommonUtil {
	public static String toRoman(int number) {
		if (number > 0) {
			return "I".repeat(number).replace("IIIII", "V").replace("IIII", "IV")
				.replace("VV", "X").replace("VIV", "IX").replace("XXXXX", "L").replace("XXXX", "XL")
				.replace("LL", "C").replace("LXL", "XC").replace("CCCCC", "D").replace("CCCC", "CD")
				.replace("DD", "M").replace("DCD", "CM");
		}
		return "";
	}

	public static <T> T make(Supplier<T> factory) {
		return factory.get();
	}

	public static <T> T make(T object, Consumer<T> initializer) {
		initializer.accept(object);
		return object;
	}

	public static String getCurrentServerAddress() {
		final var minecraft = AxoMinecraftClient.getInstance();

		if (minecraft.br$isLocalServer()) {
			return null;
		}

		return minecraft.br$getServerAddress();
	}

	public static String lastgame;
	public static String game;

	public static String getGame() {
		List<String> sidebar = AxoMinecraftClient.getInstance().br$getSidebar();

		final var address = AxoMinecraftClient.getInstance().br$getServerAddress();

		if (sidebar.isEmpty()) {
			game = "";
		} else if (address != null && address.toLowerCase().contains(sidebar.get(0).toLowerCase())) {
			if (sidebar.get(sidebar.size() - 1).toLowerCase(Locale.ROOT).contains(address.toLowerCase(Locale.ROOT))
				|| sidebar.get(sidebar.size() - 1).contains("Playtime")) {
				game = "In Lobby";
			} else {
				if (sidebar.get(sidebar.size() - 1).contains("--------")) {
					game = "Playing Bridge Practice";
				} else {
					game = "Playing " + sidebar.get(sidebar.size() - 1);
				}
			}
		} else {
			game = "Playing " + sidebar.get(0);
		}

		if (!Objects.equals(lastgame, game) && game.isEmpty())
			game = lastgame;
		else
			lastgame = game;

		if (game == null) {
			game = "";
		}

		return game;
	}
}
