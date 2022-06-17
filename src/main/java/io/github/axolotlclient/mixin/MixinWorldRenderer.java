package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow @Final private static Identifier SUN;

    @Shadow @Final private static Identifier MOON_PHASES;

    @Shadow private ClientWorld world;

    @Shadow @Final private MinecraftClient client;

	@Shadow @Nullable private VertexBuffer starsBuffer;

	@Shadow @Nullable private VertexBuffer darkSkyBuffer;

	@Shadow protected abstract void renderEndSky(MatrixStack matrices);

	@Shadow protected abstract boolean method_43788(Camera camera);

	/*@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/render/Camera;)V"))
	public void idkRenderMySkies(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci){
		if(AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes()){
			MinecraftClient.getInstance().getProfiler().swap("Custom Skies");
			//BackgroundRenderer.clearFog();
			SkyboxManager.getInstance().renderSkyboxes(matrices);
			//MinecraftClient.getInstance().getProfiler().pop();
		}
	}*/

	@Inject(method = "renderSky",
            at=@At(value = "HEAD"),
            cancellable = true)
    public void sky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean bl, Runnable runnable, CallbackInfo ci){
		//runnable.run();
		if(AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes()){
			MinecraftClient.getInstance().getProfiler().swap("Custom Skies");
			BackgroundRenderer.clearFog();
			SkyboxManager.getInstance().renderSkyboxes(matrices);
			//MinecraftClient.getInstance().getProfiler().pop();
			ci.cancel();
		}
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
	            int color = AxolotlClient.CONFIG.outlineColor.getChroma().getAsInt();
	            float a = (float)(color >> 24 & 0xFF) / 255.0F;
	            float r = (float)(color >> 16 & 0xFF) / 255.0F;
	            float g = (float)(color >> 8 & 0xFF) / 255.0F;
	            float b = (float)(color & 0xFF) / 255.0F;
	            args.set(6, r);
	            args.set(7, g);
	            args.set(8, b);
	            args.set(9, a);
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
