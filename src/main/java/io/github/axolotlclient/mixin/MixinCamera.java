package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Shadow private float pitch;

    @Shadow private float yaw;

    @Shadow protected abstract double clipToSpace(double desiredCameraDistance);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/client/render/Camera.moveBy(DDD)V", ordinal = 0))
    private void perspectiveUpdatePitchYaw(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        this.pitch = Freelook.getInstance().pitch(pitch) * (inverseView && Freelook.getInstance().enabled.get() && Freelook.getInstance().active ? -1 : 1);
        this.yaw = Freelook.getInstance().yaw(yaw) + (inverseView && Freelook.getInstance().enabled.get() && Freelook.getInstance().active ? 180 : 0);
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "net/minecraft/client/render/Camera.setRotation(FF)V", ordinal = 0))
    private void perspectiveFixRotation(Args args) {
        args.set(0, Freelook.getInstance().yaw(args.get(0)));
        args.set(1, Freelook.getInstance().pitch(args.get(1)));
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V", ordinal = 0), index = 0)
    private double correctDistance(double x){
        if(Freelook.getInstance().enabled.get() && Freelook.getInstance().active && MinecraftClient.getInstance().options.getPerspective().isFrontView()){
            return -clipToSpace(4);
        }
        return x;
    }
}
