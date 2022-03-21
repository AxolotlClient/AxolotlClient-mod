package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.NetworkHelper;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin {

    @Inject(method = "joinWorld", at = @At("HEAD"))
    public void joinWorld(int index, CallbackInfo ci){
        if(Axolotlclient.features){
            NetworkHelper.setOnline();

            DiscordRPC.update();
        }
    }
}
