package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class MCPSkyboxInstance extends SkyboxInstance {

    public MCPSkyboxInstance(JsonObject json){
        super(json);
        this.textures[0] = new Identifier(json.get("source").getAsString());
        try {
            this.fade[0] = convertToTicks(json.get("startFadeIn").getAsInt());
            this.fade[1] = convertToTicks(json.get("endFadeIn").getAsInt());
            this.fade[3] = convertToTicks(json.get("endFadeOut").getAsInt());
        } catch (NullPointerException e){
            this.alwaysOn = true;
        }
        try {
            this.fade[2] = convertToTicks(json.get("startFadeOut").getAsInt());
        } catch (Exception ignored){
            this.fade[2] = Util.getTicksBetween(Util.getTicksBetween(fade[0], fade[1]), fade[3]);
        }
        try {
            this.rotate = json.get("rotate").getAsBoolean();
            if (rotate) {
                this.rotationSpeed = json.get("speed").getAsFloat();
            }
        } catch (Exception e){
            this.rotate = false;
        }
        try {
            String[] axis = json.get("axis").getAsString().split(" ");
            for (int i = 0; i < axis.length; i++) {
                this.rotationStatic[i] = Float.parseFloat(axis[i]);
            }
        } catch (Exception ignored){
        }

        try {
            this.blendMode=parseBlend(json.get("blend").getAsString());
        } catch (Exception ignored){
        }
        showMoon = true;
        showSun = true;
    }

    @Override
    public void renderSkybox() {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {

            MinecraftClient.getInstance().getTextureManager().bindTexture(textures[0]);
            GlStateManager.pushMatrix();

            double u;
            double v;

            if(i==0){
                u=0;
                v=0;

            } else if (i == 1) {
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                u=1/3D;
                v=0.5D;

            } else if (i == 2) {
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(180, 0, 1, 0);
                u=2/3D;
                v=0F;

            } else if (i == 3) {
                GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
                u=1/3D;
                v=0F;

            } else if (i == 4) {
                GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(-90, 0, 1, 0);
                u=2/3D;
                v=0.5D;

            } else {
                GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(90, 0, 1, 0);
                v=0.5D;
                u=0;

            }

            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(-100, -100, -100).texture(u, v).color(1F, 1F, 1F, alpha).next();
            bufferBuilder.vertex(-100, -100, 100).texture(u, v+0.5).color(1F, 1F, 1F, alpha).next();
            bufferBuilder.vertex(100, -100, 100).texture(u+1/3F, v+0.5).color(1F, 1F, 1F, alpha).next();
            bufferBuilder.vertex(100, -100, -100).texture(u+1/3F, v).color(1F, 1F, 1F, alpha).next();

            tessellator.draw();

            GlStateManager.popMatrix();
        }
    }

    protected int convertToTicks(int hourFormat){
        hourFormat*=10;
        hourFormat-=6000;
        if(hourFormat < 0){
            hourFormat+=24000;
        }
        if(hourFormat>=24000){
            hourFormat-=24000;
        }
        return hourFormat;
    }
}
