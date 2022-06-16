package io.github.moehreag.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

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
    }

	@Override
	public void renderSkybox(MatrixStack matrices) {
		this.alpha=getAlpha();
		this.distance= MinecraftClient.getInstance().options.getViewDistance().get();

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int i = 0; i < 6; ++i) {

			if (textures[0] != null) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(textures[0]);
				matrices.push();

				if (i == 1) {
					matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(90));

				} else if (i == 2) {
					matrices.multiply(Vec3f.NEGATIVE_X.getRadialQuaternion(90));
					matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(180));

				} else if (i == 3) {
					matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(180));

				} else if (i == 4) {
					matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(90));
					matrices.multiply(Vec3f.NEGATIVE_Y.getRadialQuaternion(90));

				} else {
					matrices.multiply(Vec3f.NEGATIVE_Z.getRadialQuaternion(90));
					matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(90));
				}
				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(-distance * 16, -distance * 16, -distance * 16).uv(0F, 0F).color(1F, 1F, 1F, alpha).next();
				bufferBuilder.vertex(-distance * 16, -distance * 16, distance * 16).uv(0F, 1F).color(1F, 1F, 1F, alpha).next();
				bufferBuilder.vertex(distance * 16, -distance * 16, distance * 16).uv(1F, 1F).color(1F, 1F, 1F, alpha).next();
				bufferBuilder.vertex(distance * 16, -distance * 16, -distance * 16).uv(1F, 0F).color(1F, 1F, 1F, alpha).next();
				tessellator.draw();
				matrices.pop();
			}
			RenderSystem.disableBlend();
		}
    }
}
