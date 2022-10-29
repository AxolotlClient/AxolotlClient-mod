package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ComboHud;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ReachHud;
import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    public void getReach(Entity entity, CallbackInfo ci){
        if((Object) this == MinecraftClient.getInstance().player || entity.equals(MinecraftClient.getInstance().player)){
            ReachHud reachDisplayHud = (ReachHud) HudManager.getInstance().get(ReachHud.ID);
            if(reachDisplayHud != null && reachDisplayHud.isEnabled()){
                reachDisplayHud.updateDistance(this, entity);
            }

            ComboHud comboHud = (ComboHud) HudManager.getInstance().get(ComboHud.ID);
            comboHud.onEntityAttack(entity);
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onAttacking(Lnet/minecraft/entity/Entity;)V"))
    public void alwaysCrit(Entity entity, CallbackInfo ci){
        if(Particles.getInstance().getAlwaysOn(ParticleTypes.CRIT)) {
            MinecraftClient.getInstance().player.addCritParticles(entity);
        }
        if(Particles.getInstance().getAlwaysOn(ParticleTypes.ENCHANTED_HIT)) {
            MinecraftClient.getInstance().player.addEnchantedHitParticles(entity);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(source.getAttacker() != null && getUuid() == MinecraftClient.getInstance().player.getUuid()){
            ReachHud reachDisplayHud = (ReachHud) HudManager.getInstance().get(ReachHud.ID);
            if(reachDisplayHud != null && reachDisplayHud.isEnabled()){
                reachDisplayHud.updateDistance(source.getAttacker(), this);
            }
        }
    }
}