/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.ToggleSprintHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    /**
     * @author DragonEggBedrockBreaking
     * @license MPL-2.0
     * @param sprintKey the sprint key that the user has bound
     * @return whether or not the user should try to sprint
     */
    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBind;isPressed()Z"
            )
    )
    private boolean alwaysPressed(KeyBind sprintKey) {
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getInstance().get(ToggleSprintHud.ID);
        return hud.getSprintToggled().get() || sprintKey.isPressed();
    }
}
