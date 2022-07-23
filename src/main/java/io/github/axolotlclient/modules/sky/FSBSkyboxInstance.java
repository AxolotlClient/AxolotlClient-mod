package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

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

	@Override
	public void renderSkybox(MatrixStack matrices) {
		this.alpha=getAlpha();

		RenderSystem.color4f(1,1,1,1);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		//RenderSystem.se(GameRenderer::getPositionTexShader);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int i = 0; i < 6; ++i) {
			// 0 = bottom
			// 1 = north
			// 2 = south
			// 3 = top
			// 4 = east
			// 5 = west

			if (textures[i] != null) {

				MinecraftClient.getInstance().getTextureManager().bindTexture(textures[i]);
				matrices.push();

				if (i == 1) {
					matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				} else if (i == 2) {
					matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
					matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				} else if (i == 3) {
					matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
					matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				} else if (i == 4) {
					matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
					matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				} else if (i == 5) {
					matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
					matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				}
				Matrix4f matrix4f = matrices.peek().getModel();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(matrix4f, -100, -100, -100).texture(0F, 0F).color(1F, 1F, 1F, alpha).next();
				bufferBuilder.vertex(matrix4f, -100, -100, 100).texture(0F, 1F).color(1F, 1F, 1F, alpha).next();
				bufferBuilder.vertex(matrix4f, 100, -100, 100).texture(1F, 1F).color(1F, 1F, 1F, alpha).next();
				bufferBuilder.vertex(matrix4f, 100, -100, -100).texture(1F, 0F).color(1F, 1F, 1F, alpha).next();
				BufferRenderer.draw(bufferBuilder);

				matrices.pop();
			}
		}
		RenderSystem.disableBlend();
    }
}
