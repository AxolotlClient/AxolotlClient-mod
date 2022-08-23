package io.github.axolotlclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Redirect(method = "method_10200", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
    public float freelook$yaw(Entity entity) {
        return Freelook.getInstance().yaw(entity.yaw);
    }

    @Redirect(method = "method_10200", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
    public float freelook$prevYaw(Entity entity) {
        return Freelook.getInstance().yaw(entity.prevYaw);
    }

    @Redirect(method = "method_10200", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
    public float freelook$pitch(Entity entity) {
        return Freelook.getInstance().pitch(entity.pitch);
    }

    @Redirect(method = "method_10200", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
    public float freelook$prevPitch(Entity entity) {
        return Freelook.getInstance().pitch(entity.prevPitch);
    }

}
