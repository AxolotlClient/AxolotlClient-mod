package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

	@Shadow protected abstract EntityLookup<Entity> getEntityLookup();

	@Inject(method = "removeEntity", at = @At("HEAD"))
    public void onEntityRemoved(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci){
	    Entity entity = this.getEntityLookup().get(entityId);
        if(entity instanceof PlayerEntity){
            HypixelAbstractionLayer.handleDisconnectEvents(entity.getUuid());
        }
    }
}
