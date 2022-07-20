package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen {

    @Inject(method = "init", at = @At("HEAD"))
    public void freePlayerData(CallbackInfo ci){
        if(Objects.equals(HypixelMods.getInstance().cacheMode.get(), HypixelMods.HypixelCacheMode.ON_CLIENT_DISCONNECT.toString())){
            HypixelAbstractionLayer.clearPlayerData();
        }
    }
}
