package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.DiscordRPC;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin {

    @Inject(method = "joinWorld", at = @At("HEAD"))
    public void joinWorld(int index, CallbackInfo ci){
        DiscordRPC.update();
    }
}
