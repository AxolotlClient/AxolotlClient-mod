package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.sky.SkyboxManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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

	@Shadow @Nullable private VertexBuffer lightSkyBuffer;

	@Inject(method = "renderSky*", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableTexture()V", ordinal = 0), cancellable = true)
	private void renderCustomSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean bl, Runnable runnable, CallbackInfo ci){
		if(AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes() && !FabricLoader.getInstance().isModLoaded("fabricskyboxes")) {
			RenderSystem.disableTexture();
			Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
			float f = (float) vec3d.x;
			float g = (float) vec3d.y;
			float h = (float) vec3d.z;
			BackgroundRenderer.setFogBlack();
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			RenderSystem.depthMask(false);
			RenderSystem.setShaderColor(f, g, h, 1.0F);
			Shader shaderProgram = RenderSystem.getShader();
			this.lightSkyBuffer.bind();
			this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
			VertexBuffer.unbind();

			this.client.getProfiler().push("Custom Skies");
			SkyboxManager.getInstance().renderSkyboxes(matrices, world.getRainGradient(tickDelta));

			this.client.getProfiler().pop();

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);
			if (fs != null) {
				RenderSystem.setShader(GameRenderer::getPositionColorShader);
				RenderSystem.disableTexture();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				matrices.push();
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				float i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(i));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				float j = fs[0];
				float k = fs[1];
				float l = fs[2];
				Matrix4f matrix4f = matrices.peek().getPositionMatrix();
				bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]).next();
				int m = 16;

				for (int n = 0; n <= 16; ++n) {
					float o = (float) n * (float) (Math.PI * 2) / 16.0F;
					float p = MathHelper.sin(o);
					float q = MathHelper.cos(o);
					bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
				}

				BufferRenderer.drawWithShader(bufferBuilder.end());
				matrices.pop();
			}

			RenderSystem.enableTexture();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);


			if(AxolotlClient.CONFIG.showSunMoon.get()) {
				matrices.push();
				float i = 1.0F - this.world.getRainGradient(tickDelta);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(tickDelta) * 360.0F));
				Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
				float k = 30.0F;
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, SUN);
				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
				bufferBuilder.vertex(matrix4f2, -k, 100.0F, -k).texture(0.0F, 0.0F).next();
				bufferBuilder.vertex(matrix4f2, k, 100.0F, -k).texture(1.0F, 0.0F).next();
				bufferBuilder.vertex(matrix4f2, k, 100.0F, k).texture(1.0F, 1.0F).next();
				bufferBuilder.vertex(matrix4f2, -k, 100.0F, k).texture(0.0F, 1.0F).next();
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
				bufferBuilder.vertex(matrix4f2, -k, -100.0F, k).texture(p, q).next();
				bufferBuilder.vertex(matrix4f2, k, -100.0F, k).texture(t, q).next();
				bufferBuilder.vertex(matrix4f2, k, -100.0F, -k).texture(t, o).next();
				bufferBuilder.vertex(matrix4f2, -k, -100.0F, -k).texture(p, o).next();
				BufferRenderer.drawWithShader(bufferBuilder.end());


				RenderSystem.disableTexture();
				float u = this.world.getStarBrightness(tickDelta) * i;
				if (u > 0.0F) {
					RenderSystem.setShaderColor(u, u, u, u);
					BackgroundRenderer.clearFog();
					this.starsBuffer.bind();
					this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionShader());
					VertexBuffer.unbind();
					runnable.run();
				}

				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.disableBlend();
				matrices.pop();
			}
			RenderSystem.disableTexture();
			RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
			double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
			if (d < 0.0) {
				matrices.push();
				matrices.translate(0.0, 12.0, 0.0);
				this.darkSkyBuffer.bind();
				this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
				VertexBuffer.unbind();
				matrices.pop();
			}

			if (this.world.getDimensionEffects().isAlternateSkyColor()) {
				RenderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
			} else {
				RenderSystem.setShaderColor(f, g, h, 1.0F);
			}

			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
			ci.cancel();
		}
	}

    @ModifyArgs(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawCuboidShapeOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDFFFF)V"))
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
}
