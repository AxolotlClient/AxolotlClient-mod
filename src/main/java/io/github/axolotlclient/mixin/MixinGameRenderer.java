package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.motionblur.MotionBlur;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Final
    @Shadow private MinecraftClient client;

	/*@Inject(method = "renderFog", at = @At("HEAD"), cancellable = true)
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
    }*/

    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void setZoom(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir){
	    Zoom.update();
	    double returnValue = cir.getReturnValue();

		if(!AxolotlClient.CONFIG.dynamicFOV.get()) {
            Entity entity = this.client.getCameraEntity();
            double f = changingFov ? client.options.fov :70F;
            if (entity instanceof LivingEntity && ((LivingEntity)entity).getHealth() <= 0.0F) {
                float g = (float)((LivingEntity)entity).deathTime + tickDelta;
                f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
            }

            FluidState fluidState = camera.getSubmergedFluidState();
            if (!fluidState.isEmpty()) {
                f *= 60.0 / 70.0;
            }
            returnValue = f;
        }
	    returnValue = Zoom.getFov(returnValue, tickDelta);

	    cir.setReturnValue(returnValue);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getFramebuffer()Lnet/minecraft/client/gl/Framebuffer;"))
    public void worldMotionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        motionBlur(tickDelta, startTime, tick, null);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void motionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci){
        if(ci != null && !AxolotlClient.CONFIG.motionBlurInGuis.get()) {
            return;
        }

        this.client.getProfiler().push("Motion Blur");

        if(AxolotlClient.CONFIG.motionBlurEnabled.get()) {
            MotionBlur blur = (MotionBlur) AxolotlClient.modules.get(MotionBlur.ID);
            blur.onUpdate();
            blur.shader.render(tickDelta);
        }

        RenderSystem.enableTexture();

        this.client.getProfiler().pop();
    }
}
