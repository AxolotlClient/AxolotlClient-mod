package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer{


	@ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	ordinal = 1))
	public void addBadge(Args args){

		LiteralText badgesText = (LiteralText) new LiteralText(Axolotlclient.badge).append(Axolotlclient.CONFIG.NickHider.hideOtherNames ? new LiteralText(Axolotlclient.CONFIG.NickHider.otherName) : args.get(1));
		badgesText.setStyle(Style.EMPTY.withExclusiveFormatting(Formatting.WHITE).withFont(Axolotlclient.FONT));

		AbstractClientPlayerEntity player = args.get(0);

		if(Axolotlclient.features && Axolotlclient.CONFIG.badgeOptions.showBadge && Axolotlclient.isUsingClient(player.getUuid())){
			if (player.getUuid() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)){

				args.set(1, new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ? Axolotlclient.CONFIG.badgeOptions.badgeText + " ":Axolotlclient.badge + " ").setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)).append(Axolotlclient.CONFIG.NickHider.hideOwnName ? new LiteralText(Axolotlclient.CONFIG.NickHider.OwnName): args.get(1)).setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)));
			} else {
				args.set(1, (Axolotlclient.CONFIG.badgeOptions.CustomBadge ? new LiteralText(Axolotlclient.CONFIG.badgeOptions.badgeText+ " ").append(Axolotlclient.CONFIG.NickHider.hideOtherNames ? new LiteralText(Axolotlclient.CONFIG.NickHider.otherName) : args.get(1)):badgesText));
			}
			return;
		}
		if(Axolotlclient.CONFIG.NickHider.hideOtherNames && Axolotlclient.features) {
			assert MinecraftClient.getInstance().player != null;
			if (player.getName() != MinecraftClient.getInstance().player.getName()) {
				args.set(1, new LiteralText(Axolotlclient.CONFIG.NickHider.otherName));
			}
		}
	}

	@Inject(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;", at=@At("TAIL"), cancellable = true)
	public void hideSkins(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfoReturnable<Identifier> cir){

		if(Axolotlclient.CONFIG.NickHider.hideOtherSkins && abstractClientPlayerEntity.getUuid() != (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)){cir.setReturnValue(DefaultSkinHelper.getTexture(abstractClientPlayerEntity.getUuid()));cir.cancel();}
		else if(Axolotlclient.CONFIG.NickHider.hideOwnSkin && abstractClientPlayerEntity.getUuid() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)){cir.setReturnValue(DefaultSkinHelper.getTexture(MinecraftClient.getInstance().player.getUuid()));cir.cancel();}

	}

}
