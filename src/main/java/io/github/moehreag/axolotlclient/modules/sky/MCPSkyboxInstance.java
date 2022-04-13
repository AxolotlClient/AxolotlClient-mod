package io.github.moehreag.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.util.ThreadExecuter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MCPSkyboxInstance extends SkyboxInstance {

    private final Texture[] texture = new Texture[6];

    public MCPSkyboxInstance(JsonObject json){
        super(json);
        this.textures[0] = new Identifier(json.get("source").getAsString());
        this.fade[0] = json.get("startFadeIn").getAsInt();
        this.fade[1] = json.get("endFadeIn").getAsInt();
        this.fade[2] = json.get("startFadeOut").getAsInt();
        this.fade[3] = json.get("endFadeOut").getAsInt();
        loadSkybox();
    }

    @Override
    public void renderSkybox() {
        this.alpha=getAlpha();
        this.distance=MinecraftClient.getInstance().options.viewDistance;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {

            if(texture[i]!=null) {
                GlStateManager.bindTexture(texture[i].getGlId());
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
                bufferBuilder.vertex(-distance * 16, -distance * 16, -distance * 16).texture(0.0, 0.0).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(-distance * 16, -distance * 16, distance * 16).texture(0.0, 1.0).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(distance * 16, -distance * 16, distance * 16).texture(1.0, 1.0).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(distance * 16, -distance * 16, -distance * 16).texture(1.0, 0.0).color(1F, 1F, 1F, alpha).next();
                tessellator.draw();
                GlStateManager.popMatrix();
            }
        }
    }

    public void loadSkybox(){
        for (int i = 0; i < 6; i++) {
            if (texture[i] == null) {
                try {
                    BufferedImage image = TextureUtil.create(MinecraftClient.getInstance().getResourceManager().getResource(textures[0]).getInputStream());
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int x;
                    int y;
                    if (i == 0 || i == 5) x = 0;
                    else if (i == 3 || i == 1) x = width / 3;
                    else x = (width / 3) * 2;

                    if (i == 0 || i == 3 || i == 2) y = 0;
                    else y = height / 2;

                    texture[i] = new NativeImageBackedTexture(rotateToOrientation(image.getSubimage(x, y, width / 3, height / 2), i));

                } catch (Exception ignored) {
                }
            }
        }
    }

    public BufferedImage rotateToOrientation(BufferedImage image, int side){

        int widthOfImage = image.getWidth();
        int heightOfImage = image.getHeight();
        int typeOfImage = image.getType();

        BufferedImage rotated = new BufferedImage(widthOfImage, heightOfImage, typeOfImage);

        Graphics2D g2D = rotated.createGraphics();



        switch (side){
            case 5:
                g2D.rotate(Math.toRadians(270), widthOfImage / (float)2, heightOfImage / (float)2);
                break;
            case 4:
                g2D.rotate(Math.toRadians(90), widthOfImage / (float)2, heightOfImage / (float)2);
                break;
            case 2:
                g2D.rotate(Math.toRadians(180), widthOfImage / (float)2, heightOfImage / (float)2);
                break;
            default:
                return image;
        }
        g2D.drawImage(image, null, 0, 0);
        return rotated;
    }
}
