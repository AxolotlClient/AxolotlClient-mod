package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.motionblur.MotionBlur;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Final
    @Shadow private MinecraftClient client;

    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void setZoom(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir){
	    Zoom.update();
	    double returnValue = cir.getReturnValue();

		if(!AxolotlClient.CONFIG.dynamicFOV.get()) {
            Entity entity = this.client.getCameraEntity();
            double f = changingFov ? client.options.getFov().get() :70F;
            if (entity instanceof LivingEntity && ((LivingEntity)entity).getHealth() <= 0.0F) {
                float g = (float)((LivingEntity)entity).deathTime + tickDelta;
                f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
            }

	        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
	        if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
		        f *= MathHelper.lerp(this.client.options.getFovEffectScale().get(), 1.0, 0.85714287F);
	        }
            returnValue = f;
        }
	    returnValue = Zoom.getFov(returnValue, tickDelta);

	    cir.setReturnValue(returnValue);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getFramebuffer()Lcom/mojang/blaze3d/framebuffer/Framebuffer;"))
    public void worldMotionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        motionBlur(tickDelta, startTime, tick, null);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void motionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci){
        if(ci != null && !MotionBlur.getInstance().inGuis.get()) {
            return;
        }

        this.client.getProfiler().push("Motion Blur");

        if(MotionBlur.getInstance().enabled.get()) {
            MotionBlur blur = MotionBlur.getInstance();
            blur.onUpdate();
            blur.shader.render(tickDelta);
        }

        this.client.getProfiler().pop();
    }
}
