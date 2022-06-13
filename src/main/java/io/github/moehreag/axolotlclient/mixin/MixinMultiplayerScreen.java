package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerScreen {

    @Inject(method = "connect()V", at = @At("HEAD"))
    public void connect(CallbackInfo ci){
        DiscordRPC.update();
    }
}
