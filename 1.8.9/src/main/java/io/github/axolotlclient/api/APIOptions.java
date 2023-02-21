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

package io.github.axolotlclient.api;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlClientConfig.options.KeyBindOption;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;

public class APIOptions extends Options {

	@Getter
	private static final Options Instance = new APIOptions();

	@Override
	public void init() {
		super.init();

		openSidebar = new KeyBindOption("api.friends.sidebar.open", new KeyBinding("api.friends.sidebar.open", Keyboard.KEY_O, "category.axolotlclient"), keyBind ->
				MinecraftClient.getInstance().setScreen(new FriendsSidebar(MinecraftClient.getInstance().currentScreen)));
		category.add(openSidebar);
		category.add(new GenericOption("viewFriends", "clickToOpen", (mX, mY) -> MinecraftClient.getInstance().setScreen(new FriendsScreen(MinecraftClient.getInstance().currentScreen))));
		AxolotlClient.CONFIG.addCategory(category);
	}
}
