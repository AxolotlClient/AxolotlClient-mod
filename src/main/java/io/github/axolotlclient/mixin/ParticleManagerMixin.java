package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    private ParticleType cachedType;

    @Inject(method = "method_9701", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;method_1295(Lnet/minecraft/client/particle/Particle;)V"))
    public void afterCreation(int i, double d, double e, double f, double g, double h, double j, int[] is, CallbackInfoReturnable<Particle> cir){
        cachedType = ParticleType.getById(i);
    }

    @Inject(method = "method_1295", at = @At(value = "HEAD"))
    public void afterCreation(Particle particle, CallbackInfo ci){
        if(cachedType!=null){
            Particles.getInstance().particleMap.put(particle, cachedType);
            cachedType=null;
        }
    }

    // IJ might say the parameters don't match the expected values... Do not edit! It works as-is as intended.
    @Inject(method = "method_1296", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;method_1283(Lnet/minecraft/client/render/BufferBuilder;Lnet/minecraft/entity/Entity;FFFFFF)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void modifyVar(Entity entity, float f, CallbackInfo ci, float f1, float f2, float f3, float f4, float f5, int i1, int i2, Tessellator t, BufferBuilder b, int i3, Particle particle){

        if(Particles.getInstance().particleMap.containsKey(particle)) {
            Particles.getInstance().applyOptions(particle);
        }
    }
}
