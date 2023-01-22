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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.slot.Slot;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow
    private Slot focusedSlot;

    @Shadow
    protected abstract boolean handleHotbarKeyPressed(int keyCode);

    private Slot cachedSlot;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
    public void axolotlclient$resetScrollOnSlotChange(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        if (ScrollableTooltips.getInstance().enabled.get() && cachedSlot != focusedSlot) {
            cachedSlot = focusedSlot;
            ScrollableTooltips.getInstance().resetScroll();
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void axolotlclient$mouseClickedHead(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton - 100 == MinecraftClient.getInstance().options.keyInventory.getCode()) {
            MinecraftClient.getInstance().closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void axolotlclient$mouseClickedTail(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        handleHotbarKeyPressed(mouseButton - 100);
    }
}
