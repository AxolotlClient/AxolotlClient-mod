package io.github.axolotlclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.class_321;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(class_321.class)
public abstract class RenderDataMixin {

    @Redirect(method = "method_804", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;yaw:F"))
    private static float freelook$getYaw(PlayerEntity entity) {
        return Freelook.getInstance().yaw(entity.yaw);
    }

    @Redirect(method = "method_804", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;pitch:F"))
    private static float freelook$getPitch(PlayerEntity entity) {
        return Freelook.getInstance().pitch(entity.pitch);
    }

}
