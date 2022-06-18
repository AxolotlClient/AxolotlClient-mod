package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * https://github.com/AMereBagatelle/FabricSkyBoxes
 **/

public class FSBSkyboxInstance extends SkyboxInstance{

    public FSBSkyboxInstance(JsonObject json){
        super(json);
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

        try {
            this.blendMode = parseBlend(props.get("blend").getAsJsonObject().get("type").getAsString());
        } catch (Exception ignored){}
    }

    public void renderSkybox(){
        this.alpha = getAlpha();
        this.distance=MinecraftClient.getInstance().options.viewDistance;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(textures[i]);
            GlStateManager.pushMatrix();

            if (i == 1) {
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }
            if (i == 2) {
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(180, 0, 1, 0);
            }
            if (i == 3) {
                GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(90F, 0, 1, 0);
            }
            if (i == 4) {
                GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(-90, 0, 1, 0);
            }
            if (i == 5) {
                GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(90, 0, 1, 0);
            }
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(-distance*16, -distance*16, -distance*16).texture(0.0, 0.0).color(1F, 1F, 1F, alpha).next();
            bufferBuilder.vertex(-distance*16, -distance*16, distance*16).texture(0.0, 1.0).color(1F, 1F, 1F, alpha).next();
            bufferBuilder.vertex(distance*16, -distance*16, distance*16).texture(1.0, 1.0).color(1F, 1F, 1F, alpha).next();
            bufferBuilder.vertex(distance*16, -distance*16, -distance*16).texture(1.0, 0.0).color(1F, 1F, 1F, alpha).next();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }
}
