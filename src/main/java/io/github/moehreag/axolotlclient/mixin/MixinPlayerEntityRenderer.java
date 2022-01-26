package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer{


	@ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	ordinal = 1))
	public void addBadge(Args args){

		LiteralText badgesText = (LiteralText) new LiteralText(Axolotlclient.badge).append((Text) args.get(1));
		badgesText.setStyle(Style.EMPTY.withExclusiveFormatting(Formatting.WHITE).withFont(Axolotlclient.FONT));

		AbstractClientPlayerEntity player = args.get(0);

		if(Axolotlclient.features && Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(player.getUuid())){
			if (!Objects.equals(Axolotlclient.CONFIG.OwnName, "") && player.getUuid() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)){

					args.set(1, new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ? Axolotlclient.CONFIG.badgeOptions.badgeText + " ":Axolotlclient.badge + " ").setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)).append(new LiteralText(Axolotlclient.CONFIG.OwnName).setStyle(((Text) args.get(1)).shallowCopy().getStyle().withFont(Axolotlclient.FONT))));
			} else {

					args.set(1, (Axolotlclient.CONFIG.badgeOptions.CustomBadge ? new LiteralText(Axolotlclient.CONFIG.badgeOptions.badgeText+ " ").append((Text) args.get(1)):badgesText));


			}
		}
	}

}
