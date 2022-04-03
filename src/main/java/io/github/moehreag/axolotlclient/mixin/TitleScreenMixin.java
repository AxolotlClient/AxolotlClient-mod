package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    @Inject(method = "render", at = @At("HEAD"))
    public void inMenu(int mouseX, int mouseY, float tickDelta, CallbackInfo ci){
        DiscordRPC.startup();
    }

}
