package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    public void onEntityRemoved(Entity entity, CallbackInfo ci){
        if(entity instanceof PlayerEntity){
            HypixelAbstractionLayer.handleDisconnectEvents(entity.getUuid());
        }
    }
}
