package io.github.axolotlclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public class DownloadingTerrainScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    public void noLoadingScreen(CallbackInfo ci){
        MinecraftClient.getInstance().closeScreen();
    }
}
