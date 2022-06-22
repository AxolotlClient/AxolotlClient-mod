package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerRenderer {


    @ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void modifiyName(Args args){
        if(AxolotlClient.CONFIG != null) {
            AbstractClientPlayerEntity player = args.get(0);
            if(player.getUuid() == MinecraftClient.getInstance().player.getUuid() &&
                    NickHider.Instance.hideOwnName.get()){
                args.set(1, Text.literal(NickHider.Instance.hiddenNameSelf.get()));
            } else if(player.getUuid()!=MinecraftClient.getInstance().player.getUuid() &&
                    NickHider.Instance.hideOtherNames.get()){
                args.set(1, Text.literal(NickHider.Instance.hiddenNameOthers.get()));
            }
        }
    }
}
