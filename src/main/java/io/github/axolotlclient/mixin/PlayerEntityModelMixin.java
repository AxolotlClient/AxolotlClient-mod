package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin {

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/PlayerEntityModel;field_1493:Z"))
    public void translucencyStart(Entity entity, float f, float g, float h, float i, float j, float scale, CallbackInfo ci){
        startTranslucency();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
    public void translucencyStop(Entity entity, float f, float g, float h, float i, float j, float scale, CallbackInfo ci){
        stopTranslucency();
    }

    @Inject(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V", ordinal = 1))
    public void handTranslucentRightStart(CallbackInfo ci){
        startTranslucency();
    }

    @Inject(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V", ordinal = 1, shift = At.Shift.AFTER))
    public void handTranslucentRightStop(CallbackInfo ci){
        stopTranslucency();
    }

    @Inject(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V", ordinal = 1))
    public void handTranslucentLeftStart(CallbackInfo ci){
        startTranslucency();
    }

    @Inject(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V", ordinal = 1, shift = At.Shift.AFTER))
    public void handTranslucentLEftStop(CallbackInfo ci){
        stopTranslucency();
    }

    private void startTranslucency(){
        GlStateManager.pushMatrix();

        GlStateManager.enableCull();
        //GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void stopTranslucency(){
        GlStateManager.disableBlend();
        //GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
    }
}
