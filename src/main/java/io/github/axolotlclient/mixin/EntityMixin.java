package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.freelook.Freelook;
import io.github.axolotlclient.util.Hooks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void interceptMovement(double cursorDeltaX, double cursorDeltaY, CallbackInfo callback) {
        if(Freelook.getInstance().consumeRotation(cursorDeltaX, cursorDeltaY)) {
            callback.cancel();
        }
    }



    @Shadow
    public abstract float getPitch();

    @Shadow public abstract float getYaw();

    @Inject(method = "changeLookDirection", at = @At("HEAD"))
    private void updateLookDirection(double mouseDeltaX, double mouseDeltaY, CallbackInfo ci) {
        if (mouseDeltaX == 0 && mouseDeltaY == 0) {
            return;
        }

        float prevPitch = getPitch();
        float prevYaw = getYaw();
        float pitch = prevPitch + (float) (mouseDeltaY * .15);
        float yaw = prevYaw + (float) (mouseDeltaX * .15);
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        Hooks.PLAYER_DIRECTION_CHANGE.invoker().onChange(prevPitch, prevYaw, pitch, yaw);
    }
}

