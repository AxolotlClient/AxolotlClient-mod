package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class MCPSkyboxInstance extends SkyboxInstance {

    public MCPSkyboxInstance(JsonObject json){
        super(json);
        this.textures[0] = new Identifier(json.get("source").getAsString());
        this.fade[0] = json.get("startFadeIn").getAsInt();
        this.fade[1] = json.get("endFadeIn").getAsInt();
        this.fade[2] = json.get("startFadeOut").getAsInt();
        this.fade[3] = json.get("endFadeOut").getAsInt();
        try {
            this.blendMode=parseBlend(json.get("blend").getAsString());
        } catch (Exception ignored){}
    }

    @Override
    public void renderSkybox() {
        this.alpha=getAlpha();
        this.distance=MinecraftClient.getInstance().options.viewDistance;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {

            if(textures[0]!=null) {
                MinecraftClient.getInstance().getTextureManager().bindTexture(textures[0]);
                GlStateManager.pushMatrix();

                float u;
                float v;


                if(i==0){
                    u=0;
                    v=0;

                } else if (i == 1) {
                    GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                    u=1/3F;
                    v=0.5F;

                } else if (i == 2) {
                    GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotatef(180, 0, 1, 0);
                    u=2/3F;
                    v=0F;

                } else if (i == 3) {
                    GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
                    u=1/3F;
                    v=0F;

                } else if (i == 4) {
                    GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotatef(-90, 0, 1, 0);
                    u=2/3F;
                    v=0.5F;

                } else {
                    GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotatef(90, 0, 1, 0);
                    v=0.5F;
                    u=0;

                }

                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(-distance * 16, -distance * 16, -distance * 16).texture(u, v).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(-distance * 16, -distance * 16, distance * 16).texture(u, v+0.5).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(distance * 16, -distance * 16, distance * 16).texture(u+1/3F, v+0.5).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(distance * 16, -distance * 16, -distance * 16).texture(u+1/3F, v).color(1F, 1F, 1F, alpha).next();

                tessellator.draw();
                GlStateManager.popMatrix();
            }
        }
    }
}
