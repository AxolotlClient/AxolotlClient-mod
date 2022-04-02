package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import io.github.moehreag.axolotlclient.modules.zoom.Zoom;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_321;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.FloatBuffer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow protected abstract FloatBuffer updateFogColorBuffer(float red, float green, float blue, float alpha);

    @Shadow private MinecraftClient client;

    @Shadow private float viewDistance;

    @Shadow private float fogRed;

    @Shadow private float fogGreen;

    @Shadow private float fogBlue;

    @Shadow private boolean thickFog;

    @Inject(method = "renderFog", at = @At("HEAD"), cancellable = true)
    public void noFog(int i, float tickDelta, CallbackInfo ci){

        if(MinecraftClient.getInstance().world.dimension.canPlayersSleep() && Axolotlclient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes()) {
            this.viewDistance = (float) (this.viewDistance * 2 + MinecraftClient.getInstance().player.getPos().y);
            Entity entity = this.client.getCameraEntity();
            boolean bl = false;
            if (entity instanceof PlayerEntity) {
                bl = ((PlayerEntity) entity).abilities.creativeMode;
            }

            GL11.glFog(2918, this.updateFogColorBuffer(this.fogRed, this.fogGreen, this.fogBlue, 1.0F));
            GL11.glNormal3f(0.0F, -1.0F, 0.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Block block = class_321.method_9371(this.client.world, entity, tickDelta);
            if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffect.BLINDNESS)) {
                float f = 5.0F;
                int j = ((LivingEntity) entity).getEffectInstance(StatusEffect.BLINDNESS).getDuration();
                if (j < 20) {
                    f = 5.0F + (this.viewDistance - 5.0F) * (1.0F - (float) j / 20.0F);
                }

                GlStateManager.fogMode(9729);
                if (i == -1) {
                    GlStateManager.fogStart(0.0F);
                    GlStateManager.fogEnd(f * 0.8F);
                } else {
                    GlStateManager.fogStart(f * 0.25F);
                    GlStateManager.fogEnd(f);
                }

                if (GLContext.getCapabilities().GL_NV_fog_distance) {
                    GL11.glFogi(34138, 34139);
                }
            } else if (this.thickFog) {
                GlStateManager.fogMode(2048);
                GlStateManager.fogDensity(0.1F);
            } else if (block.getMaterial() == Material.WATER) {
                GlStateManager.fogMode(2048);
                if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffect.WATER_BREATHING)) {
                    GlStateManager.fogDensity(0.01F);
                } else {
                    GlStateManager.fogDensity(0.1F - (float) EnchantmentHelper.method_8449(entity) * 0.03F);
                }
            } else if (block.getMaterial() == Material.LAVA) {
                GlStateManager.fogMode(2048);
                GlStateManager.fogDensity(2.0F);
            } else {
                float f = this.viewDistance;
                GlStateManager.fogMode(9729);
                if (i == -1) {
                    GlStateManager.fogStart(f - 0.01F);
                    GlStateManager.fogEnd(f);
                } else {
                    GlStateManager.fogStart(f - 0.01F);
                    GlStateManager.fogEnd(f);
                }

                if (this.client.world.dimension.isFogThick((int) entity.x, (int) entity.z)) {
                    GlStateManager.fogStart(f * 0.05F);
                    GlStateManager.fogEnd(Math.min(f, 192.0F) * 0.5F);
                }
            }

            GlStateManager.enableColorMaterial();
            GlStateManager.enableFog();
            GlStateManager.colorMaterial(1028, 4608);
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci){
        Zoom.decreaseFov();
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    public void setZoom(float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir){
        Zoom.manageZoom();
        if(Zoom.isZoomed()||Zoom.isFadingOut()){
            cir.setReturnValue(Zoom.getFov(cir.getReturnValue()));
            //cir.cancel();
        }

    }
}
