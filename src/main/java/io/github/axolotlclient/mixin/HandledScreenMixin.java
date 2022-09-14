package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
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

    @Shadow protected abstract boolean handleHotbarKeyPressed(int keyCode);

    private Slot cachedSlot;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
    public void resetScrollOnSlotChange(int mouseX, int mouseY, float tickDelta, CallbackInfo ci){

        if(ScrollableTooltips.getInstance().enabled.get() && cachedSlot != focusedSlot){
            cachedSlot = focusedSlot;
            ScrollableTooltips.getInstance().resetScroll();
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClickedHead(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton - 100 == MinecraftClient.getInstance().options.keyInventory.getCode()) {
            MinecraftClient.getInstance().closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void mouseClickedTail(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        handleHotbarKeyPressed(mouseButton - 100);
    }


}
