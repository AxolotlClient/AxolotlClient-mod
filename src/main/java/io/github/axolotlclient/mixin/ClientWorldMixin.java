package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    public void onEntityRemoved(Entity entity, CallbackInfo ci){
        if(entity instanceof PlayerEntity && Objects.equals(HypixelMods.getInstance().cacheMode.get(), HypixelMods.HypixelApiCacheMode.ON_PLAYER_DISCONNECT.toString())){
            HypixelAbstractionLayer.handleDisconnectEvents(entity.getUuid());
        }
    }

    @ModifyArg(method = "setTimeOfDay", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setTimeOfDay(J)V"))
    public long timeChanger(long time){
        if(AxolotlClient.CONFIG.timeChangerEnabled.get()){
            return AxolotlClient.CONFIG.customTime.get();
        }
        return time;
    }
}
