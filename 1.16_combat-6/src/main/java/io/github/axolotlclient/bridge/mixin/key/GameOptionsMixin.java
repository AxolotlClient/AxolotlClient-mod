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
import net.minecraft.client.options.Perspective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin implements AxoGameOptions {
	@Shadow
	@Final
	public KeyBinding keyAttack;

	@Shadow
	@Final
	public KeyBinding keySneak;

	@Shadow
	@Final
	public KeyBinding keySprint;

	@Shadow
	@Final
	public KeyBinding keyUse;

	@Shadow
	public abstract Perspective getPerspective();

	@Shadow
	public abstract void method_31043(Perspective par1);

	@Override
	public AxoKeybinding br$getSprintKeybind() {
		return keySprint;
	}

	@Override
	public AxoKeybinding br$getSneakKeybind() {
		return keySneak;
	}

	@Override
	public AxoKeybinding br$getAttackKey() {
		return keyAttack;
	}

	@Override
	public AxoKeybinding br$getUseKey() {
		return keyUse;
	}

	@Override
	public AxoPerspective br$getCameraType() {
		return switch (getPerspective()) {
			case FIRST_PERSON -> AxoPerspective.FIRST_PERSON;
			case THIRD_PERSON_BACK -> AxoPerspective.THIRD_PERSON_BACK;
			case THIRD_PERSON_FRONT -> AxoPerspective.THIRD_PERSON_FRONT;
		};
	}

	@Override
	public void br$setCameraType(AxoPerspective perspective) {
		method_31043(switch (perspective) {
			case FIRST_PERSON -> Perspective.FIRST_PERSON;
			case THIRD_PERSON_BACK -> Perspective.THIRD_PERSON_BACK;
			case THIRD_PERSON_FRONT -> Perspective.THIRD_PERSON_FRONT;
		});
	}
}
