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

package io.github.axolotlclient.bridge.mixin.key;

import io.github.axolotlclient.bridge.AxoGameOptions;
import io.github.axolotlclient.bridge.AxoPerspective;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin implements AxoGameOptions {
	@Shadow
	public KeyBinding sprintKey;

	@Shadow
	public KeyBinding sneakKey;

	@Shadow
	public KeyBinding attackKey;

	@Shadow
	public KeyBinding useKey;

	@Shadow
	public int perspective;

	@Override
	public AxoKeybinding br$getSprintKeybind() {
		return sprintKey;
	}

	@Override
	public AxoKeybinding br$getSneakKeybind() {
		return sneakKey;
	}

	@Override
	public AxoKeybinding br$getAttackKey() {
		return attackKey;
	}

	@Override
	public AxoKeybinding br$getUseKey() {
		return useKey;
	}

	@Override
	public AxoPerspective br$getCameraType() {
		return AxoPerspective.values()[perspective];
	}

	@Override
	public void br$setCameraType(AxoPerspective perspective) {
		this.perspective = perspective.ordinal();
	}
}
