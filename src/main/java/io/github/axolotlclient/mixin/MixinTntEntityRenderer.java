package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.tnttime.TntTime;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntityRenderer.class)
public class MixinTntEntityRenderer extends EntityRenderer<TntEntity> {

    protected MixinTntEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(TntEntity entity) {
        return null;
    }

    @Inject(method = "render(Lnet/minecraft/entity/TntEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
        cancellable = true)
    public void render(TntEntity entity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci){
        if(TntTime.Instance.enabled.get()) {
            renderLabelIfPresent(entity, TntTime.Instance.getFuseTime(entity.getFuse()), matrixStack, vertexConsumerProvider, light);
            ci.cancel();
        }
    }
}