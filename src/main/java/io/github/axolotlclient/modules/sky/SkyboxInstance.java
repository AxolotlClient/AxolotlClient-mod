package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
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

    protected int blendMode = 1;

    // ! These are the options variables.  Do not mess with these.
    protected float maxAlpha = 1f;
    int distance;

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
        GlStateManager.disableAlphaTest();

        switch (blendMode)
        {
            case 0:

                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                break;

            case 1:

                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 1);
                break;

            case 2:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(775, 0);
                break;

            case 3:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(774, 771);
                break;

            case 4:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
                break;

            case 5:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(0, 769);
                break;

            case 6:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 769);
                break;

            case 7:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(774, 768);
                break;

            case 8:
                GlStateManager.disableBlend();
        }
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);

        GlStateManager.enableTexture();
    }

    protected void clearBlend(float brightness){
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
    }

    public void render(float brightness){
        setupBlend(brightness);
        renderSkybox();
        clearBlend(brightness);
    }

    public abstract void renderSkybox();
}
