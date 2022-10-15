package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {


    @Inject(method = "withAlpha", at = @At("HEAD"), cancellable = true)
    private static void customBackgroundColor(int color, int alpha, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(AxolotlClient.CONFIG.loadingScreenColor.get().withAlpha(alpha).getAsInt());
    }

    @SuppressWarnings("mapping")
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V"))
    public void customBackgroundColor$2(Args args){
        Color color = AxolotlClient.CONFIG.loadingScreenColor.get();
        args.set(0, (float) color.getRed()/255);
        args.set(1, (float) color.getGreen()/255);
        args.set(2, (float) color.getBlue()/255);
    }
}
