package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.ReachDisplayHud;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity {

    public MixinPlayerEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    public void getReach(Entity entity, CallbackInfo ci){
        if((Object)this == MinecraftClient.getInstance().player || entity.equals(MinecraftClient.getInstance().player)){
            ReachDisplayHud hud = (ReachDisplayHud) HudManager.getINSTANCE().get(ReachDisplayHud.ID);
            if(hud != null && hud.isEnabled()){
                hud.updateDistance(Util.calculateDistance(super.getPos(), entity.getPos()));
            }
        }
    }
}