package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.freelook.Freelook;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.CrosshairHud;
import io.github.axolotlclient.modules.motionblur.MotionBlur;
import io.github.axolotlclient.modules.sky.SkyboxManager;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.class_321;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MouseInput;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.ClientPlayerEntity;

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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

    private float cachedMouseFactor;
    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseInput;x:I"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void rawMouseInput(float tickDelta, long nanoTime, CallbackInfo ci, boolean displayActive, float f, float g){
        if(AxolotlClient.CONFIG.rawMouseInput.get()){
            cachedMouseFactor = g;
        }
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseInput;x:I"))
    public int rawMouseX(MouseInput instance){
        if(AxolotlClient.CONFIG.rawMouseInput.get()){
            return (int) (instance.x/cachedMouseFactor * client.options.sensitivity);
        }
        return instance.x;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseInput;y:I"))
    public int rawMouseY(MouseInput instance){
        if(AxolotlClient.CONFIG.rawMouseInput.get()){
            return (int) (instance.y/cachedMouseFactor * client.options.sensitivity);
        }
        return instance.y;
    }

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
        Zoom.update();

        float returnValue = cir.getReturnValue();

        if (!AxolotlClient.CONFIG.dynamicFOV.get()) {
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
            returnValue = f;
        }

        returnValue = Zoom.getFov(returnValue, tickDelta);

        cir.setReturnValue(returnValue);
    }

    @Redirect(method = "updateLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;gamma:F", opcode = Opcodes.GETFIELD))
    public float setGamma(GameOptions instance){
        if(AxolotlClient.CONFIG.fullBright.get()) return  15F;
        return instance.gamma;
    }

    @Inject(method = "renderDebugCrosshair", at = @At("HEAD"), cancellable = true)
    public void customCrosshairF3(float tickDelta, CallbackInfo ci){
        CrosshairHud hud = (CrosshairHud) HudManager.getInstance().get(CrosshairHud.ID);
        if(hud.isEnabled() && this.client.options.debugEnabled
                && !this.client.options.hudHidden
                && hud.showInF3.get()) {
            ci.cancel();
        }

    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;bind(Z)V",
            shift = Shift.BEFORE))
    public void worldMotionBlur(float tickDelta, long nanoTime, CallbackInfo ci) {
        motionBlur(tickDelta, nanoTime, null);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void motionBlur(float tickDelta, long nanoTime, CallbackInfo ci){
        if((ci == null) == MotionBlur.getInstance().inGuis.get()) {
            return;
        }

        this.client.profiler.push("Motion Blur");

        if(MotionBlur.getInstance().enabled.get() && GLX.shadersSupported) {
            MotionBlur blur = MotionBlur.getInstance();
            blur.onUpdate();
            blur.shader.render(tickDelta);
        }

        this.client.profiler.pop();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;increaseTransforms(FF)V"))
    public void updateFreelook(ClientPlayerEntity entity, float yaw, float pitch) {
        Freelook freelook = Freelook.getInstance();

        if(freelook.consumeRotation(yaw, pitch)) return;

        entity.increaseTransforms(yaw, pitch);
    }

    @Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
    public float freelook$yaw(Entity entity) {
        return Freelook.getInstance().yaw(entity.yaw);
    }

    @Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
    public float freelook$prevYaw(Entity entity) {
        return Freelook.getInstance().yaw(entity.prevYaw);
    }

    @Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
    public float freelook$pitch(Entity entity) {
        return Freelook.getInstance().pitch(entity.pitch);
    }

    @Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
    public float freelook$prevPitch(Entity entity) {
        return Freelook.getInstance().pitch(entity.prevPitch);
    }
}
