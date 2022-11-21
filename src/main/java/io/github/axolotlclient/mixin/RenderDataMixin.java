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

package io.github.axolotlclient.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Camera.class)
public abstract class RenderDataMixin {

    @Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;yaw:F"))
    private static float freelook$getYaw(PlayerEntity entity) {
        return Freelook.getInstance().yaw(entity.yaw);
    }

    @Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;pitch:F"))
    private static float freelook$getPitch(PlayerEntity entity) {
        return Freelook.getInstance().pitch(entity.pitch);
    }

}
