package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ComboHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
    public void onDamageEntity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        ComboHud comboCounterHud = (ComboHud) HudManager.getInstance().get(ComboHud.ID);
        if(comboCounterHud != null && comboCounterHud.isEnabled()){
            comboCounterHud.onEntityAttack((LivingEntity)(Object)this);
        }
    }
}
