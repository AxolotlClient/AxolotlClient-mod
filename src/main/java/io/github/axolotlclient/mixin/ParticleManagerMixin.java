package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Shadow protected abstract void tickParticle(Particle particle);

    private ParticleType<?> cachedType;

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"), cancellable = true)
    public void afterCreation(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir){
        cachedType = parameters.getType();

        if(!Particles.getInstance().getShowParticle(cachedType)){
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At(value = "HEAD"))
    public void afterCreation(Particle particle, CallbackInfo ci){
        if(cachedType!=null){
            Particles.getInstance().particleMap.put(particle, cachedType);
            cachedType=null;
        }
    }

    @Redirect(method = "tickParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;tickParticle(Lnet/minecraft/client/particle/Particle;)V"))
    public void removeParticlesWhenRemoved(ParticleManager instance, Particle particle){
        if(!particle.isAlive()) {
            Particles.getInstance().particleMap.remove(particle);
        }
        tickParticle(particle);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Queue;removeAll(Ljava/util/Collection;)Z"))
    public boolean removeEmitterParticlesWhenRemoved(Queue<Particle> instance, Collection<Particle> collection){
        collection.forEach(particle -> Particles.getInstance().particleMap.remove(particle));

        return instance.removeAll(collection);
    }

    @Inject(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void applyOptions(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f, CallbackInfo ci, MatrixStack matrixStack, Iterator var7, ParticleTextureSheet particleTextureSheet, Iterable<Particle> iterable, Tessellator tessellator, BufferBuilder bufferBuilder, Iterator<Particle> var12, Particle particle){
        if(Particles.getInstance().particleMap.containsKey(particle)) {
            Particles.getInstance().applyOptions(particle);
        }
    }

    // @Redirect because we need a reference of the particle, which is a local var.
    /*@Redirect(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    public void renderParticle(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v){

        if(Particles.getInstance().particleMap.containsKey(instance)) {
            Particles.getInstance().applyOptions(instance);
        }

        instance.buildGeometry(vertexConsumer, camera, v);
    }*/
}
