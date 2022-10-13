package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.axolotlclient.mixin.WorldRendererAccessor;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL14;

import java.util.Locale;
import java.util.Objects;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * <a href="https://github.com/AMereBagatelle/FabricSkyBoxes">Github Link.</a>
 * @license MIT
 **/

// TODO fix rotation & blending issues with shooting stars, implement more missing features like worlds, weather, biomes...

public abstract class SkyboxInstance {

    JsonObject object;
    float alpha = 1F;
    Identifier[] textures = new Identifier[6];
    int[] fade = new int[4];

	protected int blendMode = 1;

    // ! These are the options variables.  Do not mess with these.
	protected boolean alwaysOn;
	protected float maxAlpha = 1f;
	protected boolean manualBlend = false;
	protected int blendSrcFactor = 1;
	protected int blendDstFactor = 1;
	protected int blendEquation;
	protected boolean rotate = false;
	protected float rotationSpeed = 1F;
	protected float[] rotationStatic = new float[]{0, 0, 0};
	protected float[] rotationAxis = new float[]{0, 0, 0};

	protected boolean showSun = true;
	protected boolean showMoon = true;
	protected boolean showStars = true;

	protected final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
	protected final Identifier SUN = new Identifier("textures/environment/sun.png");

    public SkyboxInstance(JsonObject json){
        this.object=json;
    }



    public float getAlpha(){

		if(alwaysOn){
			return 1F;
		}

        int currentTime = (int) Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000; // modulo so that it's bound to 24000
        int durationIn = Util.getTicksBetween(fade[0], fade[1]);
        int durationOut = Util.getTicksBetween(fade[2], fade[3]);

        int startFadeIn = fade[0] % 24000;
        int endFadeIn = fade[1] % 24000;

        if (endFadeIn < startFadeIn) {
            endFadeIn += 24000;
        }

        int startFadeOut = fade[2] % 24000;
        int endFadeOut = fade[3] % 24000;

        if (startFadeOut < endFadeIn) {
            startFadeOut += 24000;
        }

        if (endFadeOut < startFadeOut) {
            endFadeOut += 24000;
        }

        int tempInTime = currentTime;

        if (tempInTime < startFadeIn) {
            tempInTime += 24000;
        }

        int tempFullTime = currentTime;

        if (tempFullTime < endFadeIn) {
            tempFullTime += 24000;
        }

        int tempOutTime = currentTime;

        if (tempOutTime < startFadeOut) {
            tempOutTime += 24000;
        }

        float maxPossibleAlpha;

        if (startFadeIn < tempInTime && endFadeIn >= tempInTime) {
            maxPossibleAlpha = 1f - (((float) (endFadeIn - tempInTime)) / durationIn); // fading in

        } else if (endFadeIn < tempFullTime && startFadeOut >= tempFullTime) {
            maxPossibleAlpha = 1f; // fully faded in

        } else if (startFadeOut < tempOutTime && endFadeOut >= tempOutTime) {
            maxPossibleAlpha = (float) (endFadeOut - tempOutTime) / durationOut; // fading out

        } else {
            maxPossibleAlpha = 0f; // default not showing
        }

        return alpha = MathHelper.clamp(maxPossibleAlpha*maxAlpha, 0, 1);
    }

	protected int parseBlend(String str){
		if (str == null) {
			return 1;
		} else {

			switch (str.toLowerCase(Locale.ENGLISH).trim()) {
				case "alpha":
					return 0;
				case "add":
					return 1;
				case "subtract":
					return 2;
				case "multiply":
					return 3;
				case "dodge":
					return 4;
				case "burn":
					return 5;
				case "screen":
					return 6;
				case "overlay":
					return 7;
				case "replace":
					return 8;
				default:
					Logger.warn("Unknown blend: " + str);
					return 1;
			}
		}
	}

	protected void setupBlend(float brightness){

		if(manualBlend){
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(blendSrcFactor, blendDstFactor);
			GL14.glBlendEquation(blendEquation);
			RenderSystem.enableTexture();
			return;
		}

		switch (blendMode) {
			case 0 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(770, 771);
			}
			case 1 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(770, 1);
			}
			case 2 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(775, 0);
			}
			case 3 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(774, 771);
			}
			case 4 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(1, 1);
			}
			case 5 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(0, 769);
			}
			case 6 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(1, 769);
			}
			case 7 -> {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(774, 768);
			}
			case 8 -> RenderSystem.disableBlend();
		}
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, brightness);

		RenderSystem.enableTexture();
	}

	protected void clearBlend(float brightness){
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(770, 1);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, brightness);
	}

	protected void setupRotate(MatrixStack matrices, float delta, float brightness){
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic[0]));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic[1]));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic[2]));
		if(rotate) {
			//GlStateManager.rotatef(0, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, brightness);
			//GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationAxis[0]));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationAxis[1]));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationAxis[2]));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MinecraftClient.getInstance().world.getSkyAngle(delta) * 360F * -rotationSpeed));
			matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(rotationAxis[0]));
			matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotationAxis[1]));
			matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(rotationAxis[2]));
		}
	}

	protected void clearRotate(MatrixStack matrices){
		matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(rotationStatic[0]));
		matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotationStatic[1]));
		matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(rotationStatic[2]));
	}

	public void render(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Runnable runnable){
		float brightness = MinecraftClient.getInstance().world.getRainGradient(tickDelta);
		matrices.push();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		setupBlend(brightness);
		setupRotate(matrices, tickDelta, brightness);
		renderSkybox(matrices);
		clearBlend(brightness);
		clearRotate(matrices);
		matrices.pop();
		renderDecorations(matrices, projectionMatrix, tickDelta, runnable);
		RenderSystem.enableTexture();
	}

	protected void renderDecorations(MatrixStack matrices, Matrix4f projectionMatrix, float delta, Runnable runnable){
		WorldRendererAccessor worldRendererAccessor = (WorldRendererAccessor)MinecraftClient.getInstance().worldRenderer;
		RenderSystem.enableTexture();
		RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);

		matrices.push();
		float i = 1.0F - MinecraftClient.getInstance().world.getRainGradient(delta);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MinecraftClient.getInstance().world.getSkyAngle(delta) * 360.0F));
		Matrix4f matrix4f2 = matrices.peek().getPosition();
		float k = 30.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();

		if(showSun) {
			RenderSystem.setShaderTexture(0, SUN);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(matrix4f2, -k, 100.0F, -k).uv(0.0F, 0.0F).next();
			bufferBuilder.vertex(matrix4f2, k, 100.0F, -k).uv(1.0F, 0.0F).next();
			bufferBuilder.vertex(matrix4f2, k, 100.0F, k).uv(1.0F, 1.0F).next();
			bufferBuilder.vertex(matrix4f2, -k, 100.0F, k).uv(0.0F, 1.0F).next();
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
		if(showMoon) {
			k = 20.0F;
			RenderSystem.setShaderTexture(0, MOON_PHASES);
			int r = MinecraftClient.getInstance().world.getMoonPhase();
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

		}
		if(showStars){
			RenderSystem.disableTexture();
			float u = MinecraftClient.getInstance().world.getStarBrightness(delta) * i;
			if (u > 0.0F) {
				RenderSystem.setShaderColor(u, u, u, u);
				BackgroundRenderer.clearFog();
				worldRendererAccessor.getStarsBuffer().bind();
				worldRendererAccessor.getStarsBuffer().setShader(matrices.peek().getPosition(), projectionMatrix, GameRenderer.getPositionShader());
				VertexBuffer.unbind();
				runnable.run();
			}

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableBlend();
			matrices.pop();
		}

		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
	}

	public abstract void renderSkybox(MatrixStack matrices);
}
