package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.tnttime.TntTime;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntityRenderer.class)
public abstract class TntEntityRendererMixin extends EntityRenderer<TntEntity> {


    protected TntEntityRendererMixin(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Inject(method = "render(Lnet/minecraft/entity/TntEntity;DDDFF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;DDDFF)V"),
        cancellable = true)
    public void render(TntEntity entity, double x, double y, double z, float f, float tickDelta, CallbackInfo ci){
        if(TntTime.Instance.enabled.get()) {
            super.renderLabelIfPresent(entity, TntTime.Instance.getFuseTime(entity.fuseTimer).asFormattedString(), x, y, z, 64);
            ci.cancel();
        }
    }
}
