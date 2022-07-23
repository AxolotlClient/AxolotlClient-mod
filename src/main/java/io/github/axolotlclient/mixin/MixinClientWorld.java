package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Shadow @Final private Int2ObjectMap<Entity> regularEntities;

    @Inject(method = "removeEntity", at = @At("HEAD"))
    public void onEntityRemoved(int entityId, CallbackInfo ci){
	    Entity entity = this.regularEntities.get(entityId);
        if(entity instanceof PlayerEntity && HypixelMods.getInstance().cacheMode.get().equals(HypixelMods.HypixelCacheMode.ON_PLAYER_DISCONNECT.toString())){
            HypixelAbstractionLayer.handleDisconnectEvents(entity.getUuid());
        }
    }

	@ModifyArg(method = "setTimeOfDay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld$Properties;setTimeOfDay(J)V"))
	public long timeChanger(long time){
		if(AxolotlClient.CONFIG.timeChangerEnabled.get()){
			return AxolotlClient.CONFIG.customTime.get();
		}
		return time;
	}
}
