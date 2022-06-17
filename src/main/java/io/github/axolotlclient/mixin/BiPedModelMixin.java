package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.model.ModelPart;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiPedModel.class)
public abstract class BiPedModelMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V", ordinal = 6))
    public void translucentHatOne(ModelPart instance, float scale){
        GlStateManager.pushMatrix();

        GlStateManager.enableCull();
        //GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        instance.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        //GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();

        GlStateManager.popMatrix();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V", ordinal = 13))
    public void translucentHatTwo(ModelPart instance, float scale){
        GlStateManager.pushMatrix();

        GlStateManager.enableCull();
        //GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        instance.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        //GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();

        GlStateManager.popMatrix();
    }
}
