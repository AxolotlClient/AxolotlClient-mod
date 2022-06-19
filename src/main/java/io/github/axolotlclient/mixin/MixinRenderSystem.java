package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class MixinRenderSystem {

	@Shadow(remap = false) @Final
	private static float[] shaderColor;

	@Inject(method = "_setShaderColor", at = @At(value = "HEAD"), cancellable = true, remap = false)
	private static void reduceBlue(float red, float green, float blue, float alpha, CallbackInfo ci){
		if(AxolotlClient.CONFIG.nightMode.get()){
			shaderColor[0] = red;
			shaderColor[1] = green;
			shaderColor[2] = blue/2;
			shaderColor[3] = alpha;
			ci.cancel();
		}
	}
}
