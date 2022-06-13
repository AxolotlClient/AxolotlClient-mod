package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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

	@Inject(method = "renderSky",
            at=@At(value = "HEAD"),
            cancellable = true)
    public void sky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean bl, Runnable runnable, CallbackInfo ci){
		if (!bl) {
			CameraSubmersionType cameraSubmersionType = preStep.getSubmersionType();
			if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && !this.method_43788(preStep)) {
				if (this.client.world.getSkyProperties().getSkyType() == SkyProperties.SkyType.END) {
					renderEndSky(matrices);
				} else if (this.client.world.getSkyProperties().getSkyType() == SkyProperties.SkyType.NORMAL) {
					if (AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes()) {


						this.client.getProfiler().push("Custom Skies");
						SkyboxManager.getInstance().renderSkyboxes();

						this.client.getProfiler().pop();

						BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();

						RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
						matrices.push();
						float i = 1.0F - this.world.getRainGradient(tickDelta);
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
						matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
						matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(tickDelta) * 360.0F));
						Matrix4f matrix4f2 = matrices.peek().getPosition();
						float k = 30.0F;
						RenderSystem.setShader(GameRenderer::getPositionTexShader);
						RenderSystem.setShaderTexture(0, SUN);
						bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
						bufferBuilder.vertex(matrix4f2, -k, 100.0F, -k).uv(0.0F, 0.0F).next();
						bufferBuilder.vertex(matrix4f2, k, 100.0F, -k).uv(1.0F, 0.0F).next();
						bufferBuilder.vertex(matrix4f2, k, 100.0F, k).uv(1.0F, 1.0F).next();
						bufferBuilder.vertex(matrix4f2, -k, 100.0F, k).uv(0.0F, 1.0F).next();
						BufferRenderer.drawWithShader(bufferBuilder.end());
						k = 20.0F;
						RenderSystem.setShaderTexture(0, MOON_PHASES);
						int r = this.world.getMoonPhase();
						int s = r % 4;
						int m = r / 4 % 2;
						float t = (float) (s) / 4.0F;
						float o = (float) (m) / 2.0F;
						float p = (float) (s + 1) / 4.0F;
						float q = (float) (m + 1) / 2.0F;
						bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
						bufferBuilder.vertex(matrix4f2, -k, -100.0F, k).uv(p, q).next();
						bufferBuilder.vertex(matrix4f2, k, -100.0F, k).uv(t, q).next();
						bufferBuilder.vertex(matrix4f2, k, -100.0F, -k).uv(t, o).next();
						bufferBuilder.vertex(matrix4f2, -k, -100.0F, -k).uv(p, o).next();
						BufferRenderer.drawWithShader(bufferBuilder.end());
						RenderSystem.disableTexture();
						float u = this.world.getStarBrightness(tickDelta) * i;
						if (u > 0.0F) {
							RenderSystem.setShaderColor(u, u, u, u);
							BackgroundRenderer.clearFog();
							this.starsBuffer.bind();
							this.starsBuffer.setShader(matrices.peek().getPosition(), projectionMatrix, GameRenderer.getPositionShader());
							VertexBuffer.unbind();
							runnable.run();
						}

						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						RenderSystem.disableBlend();
						matrices.pop();
						RenderSystem.disableTexture();
						RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
						double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
						if (d < 0.0) {
							matrices.push();
							matrices.translate(0.0, 12.0, 0.0);
							this.darkSkyBuffer.bind();
							ShaderProgram shaderProgram = RenderSystem.getShader();
							this.darkSkyBuffer.setShader(matrices.peek().getPosition(), projectionMatrix, shaderProgram);
							VertexBuffer.unbind();
							matrices.pop();
						}

						Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
						float f = (float)vec3d.x;
						float g = (float)vec3d.y;
						float h = (float)vec3d.z;

						if (this.world.getSkyProperties().isAlternateSkyColor()) {
							RenderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
						} else {
							RenderSystem.setShaderColor(f, g, h, 1.0F);
						}

						RenderSystem.enableTexture();
						RenderSystem.depthMask(true);

						ci.cancel();
					}
				}
			}
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
                args.set(0, AxolotlClient.CONFIG.outlineColor.getChroma().getRed());
				args.set(1, AxolotlClient.CONFIG.outlineColor.getChroma().getGreen());
				args.set(2, AxolotlClient.CONFIG.outlineColor.getChroma().getBlue());
				args.set(3, AxolotlClient.CONFIG.outlineColor.getChroma().getAlpha());
            } else {
                int color = AxolotlClient.CONFIG.outlineColor.get().getAsInt();
                float a = (float)(color >> 24 & 0xFF) / 255.0F;
                float r = (float)(color >> 16 & 0xFF) / 255.0F;
                float g = (float)(color >> 8 & 0xFF) / 255.0F;
                float b = (float)(color & 0xFF) / 255.0F;
	            args.set(0, r);
	            args.set(1, g);
	            args.set(2, b);
	            args.set(3, a);
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
