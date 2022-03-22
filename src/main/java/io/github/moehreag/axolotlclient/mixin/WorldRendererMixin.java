package io.github.moehreag.axolotlclient.mixin;


import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Texture;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "method_9891", at=@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;disableTexture()V"), cancellable = true)
    public void sky(float f, int ih, CallbackInfo ci){

        GlStateManager.disableFog();
        GlStateManager.depthMask(false);
        MinecraftClient.getInstance().getTextureManager().bindTexture(Axolotlclient.sky);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for(int i = 0; i < 6; ++i) {
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
            bufferBuilder.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(-100.0, -100.0, 100.0).texture(0.0, 1.0).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(100.0, -100.0, 100.0).texture(1.0, 1.0).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(100.0, -100.0, -100.0).texture(1.0, 0.0).color(40, 40, 40, 255).next();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(true);

        ci.cancel();
    }

}
