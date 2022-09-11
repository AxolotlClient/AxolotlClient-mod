package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EmitterParticle.class)
public abstract class EmitterParticleMixin {

    @Shadow @Final private ParticleEffect parameters;

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 16))
    public int multiplyParticles(int constant){
        return constant * Particles.getInstance().getMultiplier(parameters.getType());
    }
}
