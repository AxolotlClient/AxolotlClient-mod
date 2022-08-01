package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void interceptMovement(double cursorDeltaX, double cursorDeltaY, CallbackInfo callback) {
        if(Freelook.INSTANCE.consumeRotation(cursorDeltaX, cursorDeltaY)) {
            callback.cancel();
        }
    }
}
