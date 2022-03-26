package io.github.moehreag.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.modules.sky.texture.SkyboxTexture;
import io.github.moehreag.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Texture;
import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * https://github.com/AMereBagatelle/FabricSkyBoxes
 **/

public class SkyboxInstance {

    JsonObject object;
    float alpha = 251;
    Identifier[] textures = new Identifier[6];
    int[] fade = new int[4];

    // ! These are the options variables.  Do not mess with these.
    protected float maxAlpha = 1f;

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
        this.fade[0] = props.get("fade").getAsJsonObject().get("startFadeIn").getAsInt();
        this.fade[1] = props.get("fade").getAsJsonObject().get("endFadeIn").getAsInt();
        this.fade[2] = props.get("fade").getAsJsonObject().get("startFadeOut").getAsInt();
        this.fade[3] = props.get("fade").getAsJsonObject().get("endFadeOut").getAsInt();
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

        return  Math.round(maxPossibleAlpha * 255 * 100000)/100000F;
    }

    public void renderSkybox(){
        this.alpha = getAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {
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
