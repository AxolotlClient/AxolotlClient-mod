package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class MixinRenderSystem {

    @SuppressWarnings("deprecated")
	@Inject(method = "color4f", at = @At(value = "HEAD"), cancellable = true, remap = false)
	private static void reduceBlue(float red, float green, float blue, float alpha, CallbackInfo ci){
		if(AxolotlClient.CONFIG.nightMode.get()){
            GlStateManager.color4f(red, green, blue/2, alpha);
			ci.cancel();
		}
	}
}
