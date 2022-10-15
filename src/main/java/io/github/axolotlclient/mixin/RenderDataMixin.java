package io.github.axolotlclient.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Camera.class)
public abstract class RenderDataMixin {

    @Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;yaw:F"))
    private static float freelook$getYaw(PlayerEntity entity) {
        return Freelook.getInstance().yaw(entity.yaw);
    }

    @Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;pitch:F"))
    private static float freelook$getPitch(PlayerEntity entity) {
        return Freelook.getInstance().pitch(entity.pitch);
    }

}
