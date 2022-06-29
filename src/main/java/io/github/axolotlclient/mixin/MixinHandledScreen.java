package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen {

    @Shadow @Nullable protected Slot focusedSlot;
    private Slot cachedSlot;

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    public void resetScrollOnChange(MatrixStack matrices, int x, int y, CallbackInfo ci){

        if(ScrollableTooltips.Instance.enabled.get() && cachedSlot != focusedSlot){
            cachedSlot = focusedSlot;
            ScrollableTooltips.Instance.resetScroll();
        }

    }

}
