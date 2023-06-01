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

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.ColorUtil;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(value = SplashOverlay.class, priority = 1100)
public abstract class SplashOverlayMixin {
	@Mutable
	@Shadow
	@Final
	private static IntSupplier BRAND_ARGB;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void axolotlclient$customBackgroundColor(CallbackInfo ci) {
		if (!QuiltLoader.isModLoaded("dark-loading-screen")) {
			Color color = AxolotlClient.CONFIG.loadingScreenColor.get();
			BRAND_ARGB = () -> ColorUtil.ARGB32.getArgb(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
		}
	}
}
