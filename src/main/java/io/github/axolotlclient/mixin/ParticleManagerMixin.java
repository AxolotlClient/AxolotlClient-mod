package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    // @Redirect because we need a reference of the particle, which is a local var.
    @Redirect(method = "method_1296", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;method_1283(Lnet/minecraft/client/render/BufferBuilder;Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderParticle(Particle instance, BufferBuilder builder, Entity entity, float f, float g, float h, float i, float j, float k){

        if(Particles.getInstance().particleMap.containsKey(instance)) {
            Particles.getInstance().applyOptions(instance);
        }

        instance.method_1283(builder, entity, f, g, h, i, j, k);
    }
}
