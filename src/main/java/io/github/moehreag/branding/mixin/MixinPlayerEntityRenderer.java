package io.github.moehreag.branding.mixin;

import io.github.moehreag.branding.Axolotlclient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer{


	@ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	ordinal = 1))
	public void addBadge(Args args){

		AbstractClientPlayerEntity player = args.get(0);

		if(Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(player.getUuid())){

		if (Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(abstractClientPlayerEntity.getUuid())) {
			text = new LiteralText("✵ ").append(text);
			args.set(1, new LiteralText("✵ ").append((Text) args.get(1)));
		}
	}
}
