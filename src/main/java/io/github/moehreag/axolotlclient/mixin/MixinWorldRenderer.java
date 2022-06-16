package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * https://github.com/AMereBagatelle/FabricSkyBoxes
 **/
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

	@Inject(method = "renderSky",
            at=@At(value = "HEAD"),
            cancellable = true)
    public void sky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean bl, Runnable runnable, CallbackInfo ci){
		runnable.run();
		float total = SkyboxManager.getInstance().getTotalAlpha();
		SkyboxManager.getInstance().renderSkyboxes(matrices);
		if(SkyboxManager.getInstance().hasSkyBoxes()) ci.cancel();
    }

    /*@ModifyArg(method = "method_1380", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glLineWidth(F)V"))
    public float OutlineWidth(float width){
        if(AxolotlClient.CONFIG.enableCustomOutlines.get() && AxolotlClient.CONFIG.outlineWidth.get()>1){
            return 1.0F+ AxolotlClient.CONFIG.outlineWidth.get();
        }
        return width;
    }*/

    @ModifyArgs(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawShapeOutline(Lnet/minecraft/client/util/math/MatrixStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDFFFF)V"))
    public void customOutlineColor(Args args){
        if(AxolotlClient.CONFIG.enableCustomOutlines.get()){
            if(AxolotlClient.CONFIG.outlineChroma.get()){
                args.set(6, AxolotlClient.CONFIG.outlineColor.getChroma().getRed());
				args.set(7, AxolotlClient.CONFIG.outlineColor.getChroma().getGreen());
				args.set(8, AxolotlClient.CONFIG.outlineColor.getChroma().getBlue());
				args.set(9, AxolotlClient.CONFIG.outlineColor.getChroma().getAlpha());
            } else {
                int color = AxolotlClient.CONFIG.outlineColor.get().getAsInt();
                float a = (float)(color >> 24 & 0xFF) / 255.0F;
                float r = (float)(color >> 16 & 0xFF) / 255.0F;
                float g = (float)(color >> 8 & 0xFF) / 255.0F;
                float b = (float)(color & 0xFF) / 255.0F;
	            args.set(6, r);
	            args.set(7, g);
	            args.set(8, b);
	            args.set(9, a);
            }
        }
    }

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZF)V"))
	public void noFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta){
		if(!AxolotlClient.CONFIG.customSky.get() || !SkyboxManager.getInstance().hasSkyBoxes()){
			BackgroundRenderer.applyFog(camera, fogType, viewDistance, thickFog, tickDelta);
		}
	}
}
