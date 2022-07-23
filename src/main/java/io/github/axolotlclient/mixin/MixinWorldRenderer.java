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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
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

    @Shadow @Final private VertexFormat skyVertexFormat;

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableTexture()V", ordinal = 0), cancellable = true)
	public void renderCustomSky(MatrixStack matrixStack, float tickDelta, CallbackInfo ci){
		if(AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes() && !FabricLoader.getInstance().isModLoaded("fabricskyboxes")) {
            RenderSystem.disableTexture();
            Vec3d vec3d = this.world.method_23777(this.client.gameRenderer.getCamera().getBlockPos(), tickDelta);
            float g = (float)vec3d.x;
            float h = (float)vec3d.y;
            float i = (float)vec3d.z;
            BackgroundRenderer.setFogBlack();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.depthMask(false);
            RenderSystem.enableFog();
            RenderSystem.color3f(g, h, i);
            this.lightSkyBuffer.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.lightSkyBuffer.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();

			this.client.getProfiler().push("Custom Skies");
			SkyboxManager.getInstance().renderSkyboxes(matrixStack, world.getRainGradient(tickDelta));

			this.client.getProfiler().pop();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float[] fs = this.world.getSkyProperties().getSkyColor(this.world.getSkyAngle(tickDelta), tickDelta);
            if (fs != null) {
                RenderSystem.disableTexture();
                RenderSystem.shadeModel(7425);
                matrixStack.push();
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                float j = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(j));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                float k = fs[0];
                float l = fs[1];
                float m = fs[2];
                Matrix4f matrix4f = matrixStack.peek().getModel();
                bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(k, l, m, fs[3]).next();
                int n = 16;

                for(int o = 0; o <= 16; ++o) {
                    float p = (float)o * (float) (Math.PI * 2) / 16.0F;
                    float q = MathHelper.sin(p);
                    float r = MathHelper.cos(p);
                    bufferBuilder.vertex(matrix4f, q * 120.0F, r * 120.0F, -r * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                }

                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                matrixStack.pop();
                RenderSystem.shadeModel(7424);
            }

            RenderSystem.enableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            matrixStack.push();
            float j = 1.0F - this.world.getRainGradient(tickDelta);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, j);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(tickDelta) * 360.0F));
            Matrix4f matrix4f2 = matrixStack.peek().getModel();
            float l = 30.0F;
            MinecraftClient.getInstance().getTextureManager().bindTexture(SUN);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -l, 100.0F, -l).texture(0.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, l, 100.0F, -l).texture(1.0F, 0.0F).next();
            bufferBuilder.vertex(matrix4f2, l, 100.0F, l).texture(1.0F, 1.0F).next();
            bufferBuilder.vertex(matrix4f2, -l, 100.0F, l).texture(0.0F, 1.0F).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            l = 20.0F;
            MinecraftClient.getInstance().getTextureManager().bindTexture(MOON_PHASES);
            int s = this.world.getMoonPhase();
            int t = s % 4;
            int n = s / 4 % 2;
            float u = (float)(t) / 4.0F;
            float p = (float)(n) / 2.0F;
            float q = (float)(t + 1) / 4.0F;
            float r = (float)(n + 1) / 2.0F;
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix4f2, -l, -100.0F, l).texture(q, r).next();
            bufferBuilder.vertex(matrix4f2, l, -100.0F, l).texture(u, r).next();
            bufferBuilder.vertex(matrix4f2, l, -100.0F, -l).texture(u, p).next();
            bufferBuilder.vertex(matrix4f2, -l, -100.0F, -l).texture(q, p).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.disableTexture();
            float v = this.world.method_23787(tickDelta) * j;
            if (v > 0.0F) {
                RenderSystem.color4f(v, v, v, v);
                this.starsBuffer.bind();
                this.skyVertexFormat.startDrawing(0L);
                this.starsBuffer.draw(matrixStack.peek().getModel(), 7);
                VertexBuffer.unbind();
                this.skyVertexFormat.endDrawing();
            }

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();
            matrixStack.pop();
            RenderSystem.disableTexture();
            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
            double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight();
            if (d < 0.0) {
                matrixStack.push();
                matrixStack.translate(0.0, 12.0, 0.0);
                this.darkSkyBuffer.bind();
                this.skyVertexFormat.startDrawing(0L);
                this.darkSkyBuffer.draw(matrixStack.peek().getModel(), 7);
                VertexBuffer.unbind();
                this.skyVertexFormat.endDrawing();
                matrixStack.pop();
            }

            if (this.world.getSkyProperties().isAlternateSkyColor()) {
                RenderSystem.color3f(g * 0.2F + 0.04F, h * 0.2F + 0.04F, i * 0.6F + 0.1F);
            } else {
                RenderSystem.color3f(g, h, i);
            }

            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.disableFog();
			ci.cancel();
		}
	}

    @ModifyArgs(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawShapeOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDFFFF)V"))
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
