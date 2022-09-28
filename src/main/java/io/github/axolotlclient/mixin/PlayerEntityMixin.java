package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.ComboCounterHud;
import io.github.axolotlclient.modules.hud.gui.hud.ReachDisplayHud;
import io.github.axolotlclient.modules.particles.Particles;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    public void getReach(Entity entity, CallbackInfo ci){
        if((Object)this == MinecraftClient.getInstance().player || entity.equals(MinecraftClient.getInstance().player)){
            ReachDisplayHud reachDisplayHud = (ReachDisplayHud) HudManager.getInstance().get(ReachDisplayHud.ID);
            if(reachDisplayHud != null && reachDisplayHud.isEnabled()){
                reachDisplayHud.updateDistance(Util.calculateDistance(super.getPos(), entity.getPos()));
            }
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onAttacking(Lnet/minecraft/entity/Entity;)V"))
    public void alwaysCrit(Entity entity, CallbackInfo ci){
        if(((BooleanOption) Particles.getInstance().particleOptions.get(ParticleTypes.CRIT).get("alwaysCrit")).get()) {
            MinecraftClient.getInstance().player.addCritParticles(entity);
        }
        if(((BooleanOption)Particles.getInstance().particleOptions.get(ParticleTypes.ENCHANTED_HIT).get("alwaysCrit")).get()) {
            MinecraftClient.getInstance().player.addEnchantedHitParticles(entity);
        }
    }
}