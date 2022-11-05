package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.util.FeatureDisabler;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin {

    @Inject(method = "init", at = @At("HEAD"))
    public void freePlayerData(CallbackInfo ci){
        if(HypixelMods.getInstance().cacheMode.get()!=null && Objects.equals(HypixelMods.getInstance().cacheMode.get(), HypixelMods.HypixelApiCacheMode.ON_CLIENT_DISCONNECT.toString())) {
            HypixelAbstractionLayer.clearPlayerData();
        }
        FeatureDisabler.clear();
    }

    @Inject(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"))
    public void onServerJoin(ServerInfo entry, CallbackInfo ci){
        FeatureDisabler.onServerJoin(entry);
    }
}
