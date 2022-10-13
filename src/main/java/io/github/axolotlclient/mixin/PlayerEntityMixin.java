package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.ReachDisplayHud;
import io.github.axolotlclient.modules.particles.Particles;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;initializeAttribute(Lnet/minecraft/entity/attribute/EntityAttribute;)Lnet/minecraft/entity/attribute/EntityAttributeInstance;"))
    public void getReach(Entity entity, CallbackInfo ci){
        if((Object)this == MinecraftClient.getInstance().player || entity.equals(MinecraftClient.getInstance().player)){
            ReachDisplayHud hud = (ReachDisplayHud) HudManager.getInstance().get(ReachDisplayHud.ID);
            if(hud != null && hud.isEnabled()){
                double d = Util.calculateDistance(super.getPos(), entity.getPos());
                if(d<=MinecraftClient.getInstance().interactionManager.getReachDistance()) {
                    hud.updateDistance(d);
                }
            }
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;method_6150(Lnet/minecraft/entity/Entity;)V"))
    public void alwaysCrit(Entity entity, CallbackInfo ci){
        if(Particles.getInstance().getAlwaysOn(ParticleType.CRIT)) {
            MinecraftClient.getInstance().player.addCritParticles(entity);
        }
        if(Particles.getInstance().getAlwaysOn(ParticleType.CRIT_MAGIC)) {
            MinecraftClient.getInstance().player.addEnchantedHitParticles(entity);
        }
    }
}