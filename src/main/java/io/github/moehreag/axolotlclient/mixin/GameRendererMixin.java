package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.CrosshairHud;
import io.github.moehreag.axolotlclient.modules.motionblur.MotionBlur;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import io.github.moehreag.axolotlclient.modules.zoom.Zoom;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.class_321;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

	@Shadow public abstract void tick();

	@Inject(method = "renderFog", at = @At("HEAD"), cancellable = true)
    public void noFog(int i, float tickDelta, CallbackInfo ci){

        if(MinecraftClient.getInstance().world.dimension.canPlayersSleep() && AxolotlClient.CONFIG.customSky.get() && SkyboxManager.getInstance().hasSkyBoxes()) {
            this.viewDistance = (float) (this.viewDistance * 2 + MinecraftClient.getInstance().player.getPos().y);
            Entity entity = this.client.getCameraEntity();

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
                GlStateManager.fogStart(f - 0.01F);
                GlStateManager.fogEnd(f);

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

    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void setZoom(float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir){
        Zoom.manageZoom();
        if(Zoom.isZoomed()||Zoom.isFadingOut()){
            cir.setReturnValue(Zoom.getFov(cir.getReturnValue()));
        } else if(!AxolotlClient.CONFIG.dynamicFOV.get()) {
            Entity entity = this.client.getCameraEntity();
            float f = changingFov ? client.options.fov:70F;
            if (entity instanceof LivingEntity && ((LivingEntity)entity).getHealth() <= 0.0F) {
                float g = (float)((LivingEntity)entity).deathTime + tickDelta;
                f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
            }

            Block block = class_321.method_9371(this.client.world, entity, tickDelta);
            if (block.getMaterial() == Material.WATER) {
                f = f * 60.0F / 70.0F;
            }
            cir.setReturnValue(f);
        }
    }

    @Redirect(method = "updateLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;gamma:F", opcode = Opcodes.GETFIELD))
    public float setGamma(GameOptions instance){
        if(AxolotlClient.CONFIG.fullBright.get()) return  15F;
        return instance.gamma;
    }

    @Inject(method = "renderDebugCrosshair", at = @At("HEAD"), cancellable = true)
    public void customCrosshairF3(float tickDelta, CallbackInfo ci){
        CrosshairHud hud = (CrosshairHud) HudManager.getINSTANCE().get(CrosshairHud.ID);
        if(hud.isEnabled() && this.client.options.debugEnabled
                && !this.client.options.hudHidden
                && hud.showInF3.get()) {
            ci.cancel();
        }

    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;viewport(IIII)V",
            shift = Shift.BEFORE))
    public void worldMotionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        motionBlur(tickDelta, startTime, tick, null);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void motionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci){
        if((ci == null) == AxolotlClient.CONFIG.motionBlurInGuis.get()) {
            return;
        }

        this.client.getProfiler().push("Motion Blur");

        if(AxolotlClient.CONFIG.motionBlurEnabled.get()) {
            MotionBlur blur = (MotionBlur) AxolotlClient.modules.get(MotionBlur.ID);
            blur.onUpdate();
            blur.shader.render(tickDelta);
        }

        this.client.getProfiler().pop();
    }
}
