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

import lombok.experimental.Accessors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ClientTickTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

    @Accessor
    ClientTickTracker getTicker();

    @Accessor
    String getServerAddress();

    @Accessor
    int getServerPort();
}
