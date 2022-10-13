package io.github.axolotlclient.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class)
public abstract class IdentifierMixin {

    // Only because some specific sky pack creators can't name their stuff correctly...

    @Inject(method = "isPathValid", at = @At("HEAD"), cancellable = true)
    private static void isPathValidYes(String path, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!path.contains("DUMMY"));
    }
}
