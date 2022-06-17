package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class MCPSkyboxInstance extends SkyboxInstance {

    public MCPSkyboxInstance(JsonObject json){
        super(json);
        this.textures[0] = new Identifier(json.get("source").getAsString());
        this.fade[0] = json.get("startFadeIn").getAsInt();
        this.fade[1] = json.get("endFadeIn").getAsInt();
        this.fade[2] = json.get("startFadeOut").getAsInt();
        this.fade[3] = json.get("endFadeOut").getAsInt();
    }

    @Override
    public void renderSkybox(MatrixStack matrices) {
        this.alpha=getAlpha();

	    RenderSystem.setShaderColor(1,1,1,1);
		//RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
	    RenderSystem.setShaderTexture(0, textures[0]);

        for (int i = 0; i < 6; ++i) {

            if(textures[0]!=null) {

                matrices.push();

                float u;
                float v;
				
                if(i==0){
                    u=0;
                    v=0;

                } else if (i == 1) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                    u=1/3F;
                    v=0.5F;

                } else if (i == 2) {
	                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
	                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                    u=2/3F;
                    v=0F;

                } else if (i == 3) {
	                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                    u=1/3F;
                    v=0F;

                } else if (i == 4) {
	                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
	                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
                    u=2/3F;
                    v=0.5F;

                } else {
	                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90));
	                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                    v=0.5F;
                    u=0;
                }

	            Matrix4f matrix4f = matrices.peek().getPosition();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(matrix4f, -100, -100, -100).uv(u, v).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(matrix4f, -100, -100, 100).uv(u, v+0.5F).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(matrix4f, 100, -100, 100).uv(u+1/3F, v+0.5F).color(1F, 1F, 1F, alpha).next();
                bufferBuilder.vertex(matrix4f, 100, -100, -100).uv(u+1/3F, v).color(1F, 1F, 1F, alpha).next();

                tessellator.draw();
                matrices.pop();
            }
        }
        RenderSystem.disableBlend();
    }
}
