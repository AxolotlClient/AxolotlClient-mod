package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

    @Inject(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private static void lowFire(MinecraftClient client, MatrixStack matrices, CallbackInfo ci){
        if(AxolotlClient.CONFIG.lowFire.get()) {
            matrices.translate(0, -0.2F, 0);
        }
    }
}
