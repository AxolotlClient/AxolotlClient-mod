package io.github.axolotlclient.mixin;

import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public abstract class SharedConstantsMixin {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void noInvalidChars(char chr, CallbackInfoReturnable<Boolean> cir){
        if(chr=='§' && MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
            cir.setReturnValue(true);
        }
    }
}
