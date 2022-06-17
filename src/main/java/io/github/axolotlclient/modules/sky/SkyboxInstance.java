package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Objects;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * https://github.com/AMereBagatelle/FabricSkyBoxes
 **/

public abstract class SkyboxInstance {

    JsonObject object;
    float alpha = 1F;
    Identifier[] textures = new Identifier[6];
    int[] fade = new int[4];

    // ! These are the options variables.  Do not mess with these.
    protected float maxAlpha = 1f;
    protected int blendMode;

    public SkyboxInstance(JsonObject json){
        this.object=json;
    }



    public float getAlpha(){

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

        maxPossibleAlpha *= maxAlpha;

        return  maxPossibleAlpha;
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
					AxolotlClient.LOGGER.warn("Unknown blend: " + str);
					return 1;
			}
		}
	}

	protected void setupBlend(float brightness){

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

	public void render(MatrixStack matrices, float brightness){
		setupBlend(brightness);
		renderSkybox(matrices);
		clearBlend(brightness);
	}

	public abstract void renderSkybox(MatrixStack matrices);
}
