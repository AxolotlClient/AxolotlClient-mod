package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow private Slot focusedSlot;
    private Slot cachedSlot;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
    public void resetScrollOnSlotChange(int mouseX, int mouseY, float tickDelta, CallbackInfo ci){

        if(ScrollableTooltips.Instance.enabled.get() && cachedSlot != focusedSlot){
            cachedSlot = focusedSlot;
            ScrollableTooltips.Instance.resetScroll();
        }

    }

}
