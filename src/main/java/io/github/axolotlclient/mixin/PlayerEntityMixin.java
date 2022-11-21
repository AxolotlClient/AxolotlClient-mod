/*
 * This File is part of AxolotlClient (mod)
 * Copyright (C) 2021-present moehreag + Contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ComboHud;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ReachHud;
import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;initializeAttribute(Lnet/minecraft/entity/attribute/EntityAttribute;)Lnet/minecraft/entity/attribute/EntityAttributeInstance;"))
    public void getReach(Entity entity, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player || entity.equals(MinecraftClient.getInstance().player)) {
            ReachHud reachDisplayHud = (ReachHud) HudManager.getInstance().get(ReachHud.ID);
            if (reachDisplayHud != null && reachDisplayHud.isEnabled()) {
                reachDisplayHud.updateDistance(this, entity);
            }

            ComboHud comboHud = (ComboHud) HudManager.getInstance().get(ComboHud.ID);
            comboHud.onEntityAttack(entity);
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