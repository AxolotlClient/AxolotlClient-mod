package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getHandle()Lnet/minecraft/client/model/ModelPart;"))
    public void lowShield(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci){
        if (AxolotlClient.CONFIG.lowShield.get() &&
            MinecraftClient.getInstance().options.getPerspective().isFirstPerson() &&
            (mode.equals(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) || mode.equals(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND))){
            matrixStack.translate(0, 0.2F, 0);
        }
    }
}
