package io.github.moehreag.axolotlclient.modules.sky;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.sky.texture.SkyboxTexture;
import io.github.moehreag.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Texture;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SkyboxInstance {

    JsonObject object;
    double alpha = 0.1;
    Identifier[] textures = new Identifier[6];

    // ! These are the options variables.  Do not mess with these.
    //protected Fade fade = Fade.ZERO;
    protected float maxAlpha = 1f;
    protected float transitionSpeed = 1;
    protected boolean changeFog = false;
    //protected RGBA fogColors = RGBA.ZERO;
    protected boolean renderSunSkyColorTint = true;
    protected boolean shouldRotate = false;
    protected List<String> weather = new ArrayList<>();
    protected List<Identifier> biomes = new ArrayList<>();
    //protected Decorations decorations = Decorations.DEFAULT;
    /**
     * Stores identifiers of <b>worlds</b>, not dimension types.
     */
    protected List<Identifier> worlds = new ArrayList<>();
    //protected List<MinMaxEntry> yRanges = Lists.newArrayList();
    //protected List<MinMaxEntry> zRanges = Lists.newArrayList();
    //protected List<MinMaxEntry> xRanges = Lists.newArrayList();

    public SkyboxInstance(JsonObject json){
        this.object = json;
        JsonObject props = json.get("properties").getAsJsonObject();
        JsonObject textures = json.get("textures").getAsJsonObject();
        this.textures[0] = new Identifier(textures.get("bottom").getAsString());
        this.textures[1] = new Identifier(textures.get("north").getAsString());
        this.textures[2] = new Identifier(textures.get("south").getAsString());
        this.textures[3] = new Identifier(textures.get("top").getAsString());
        this.textures[4] = new Identifier(textures.get("east").getAsString());
        this.textures[5] = new Identifier(textures.get("west").getAsString());
    }

    /*public final float getAlpha() {
        if (!fade.isAlwaysOn()) {
            int currentTime = (int) Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000; // modulo so that it's bound to 24000
            int durationIn = Utils.getTicksBetween(this.fade.getStartFadeIn(), this.fade.getEndFadeIn());
            int durationOut = Utils.getTicksBetween(this.fade.getStartFadeOut(), this.fade.getEndFadeOut());

            int startFadeIn = this.fade.getStartFadeIn() % 24000;
            int endFadeIn = this.fade.getEndFadeIn() % 24000;

            if (endFadeIn < startFadeIn) {
                endFadeIn += 24000;
            }

            int startFadeOut = this.fade.getStartFadeOut() % 24000;
            int endFadeOut = this.fade.getEndFadeOut() % 24000;

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
            if (checkBiomes() && checkXRanges() && checkYRanges() && checkZRanges() && checkWeather() && checkEffect()) { // check if environment is invalid
                if (alpha >= maxPossibleAlpha) {
                    alpha = maxPossibleAlpha;
                } else {
                    alpha += transitionSpeed;
                    if (alpha > maxPossibleAlpha) alpha = maxPossibleAlpha;
                }
            } else {
                if (alpha > 0f) {
                    alpha -= transitionSpeed;
                    if (alpha < 0f) alpha = 0f;
                } else {
                    alpha = 0f;
                }
            }
        } else {
            alpha = 1f;
        }

        if (alpha > SkyboxManager.MINIMUM_ALPHA) {
            if (changeFog) {
                SkyboxManager.shouldChangeFog = true;
                SkyboxManager.fogRed = this.fogColors.getRed();
                SkyboxManager.fogBlue = this.fogColors.getBlue();
                SkyboxManager.fogGreen = this.fogColors.getGreen();
            }
            if (!renderSunSkyColorTint) {
                SkyboxManager.renderSunriseAndSet = false;
            }
        }

        // sanity checks
        if (alpha < 0f) alpha = 0f;
        if (alpha > 1f) alpha = 1f;

        return alpha;
    }*/

    public void renderSkybox(){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {
            //MinecraftClient.getInstance().getTextureManager().bindTexture(Axolotlclient.sky_textures[i]);
            Texture texture = Util.getTextures().get(textures[i]);
            if(texture==null){
                texture = new SkyboxTexture(textures[i], i);
                MinecraftClient.getInstance().getTextureManager().loadTexture(textures[i], texture);
            }
            GlStateManager.bindTexture(texture.getGlId());
            GlStateManager.pushMatrix();
            if (i == 1) {
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 2) {
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 3) {
                GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 4) {
                GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (i == 5) {
                GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(-100.0, -100.0, 100.0).texture(0.0, 1.0).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(100.0, -100.0, 100.0).texture(1.0, 1.0).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(100.0, -100.0, -100.0).texture(1.0, 0.0).color(255, 255, 255, 255).next();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }
}
