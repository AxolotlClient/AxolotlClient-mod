package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer{


	/*@ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	ordinal = 1))
	public void addBadge(Args args){
		if(Axolotlclient.CONFIG != null) {

			LiteralText badgesText = (LiteralText) new LiteralText(Axolotlclient.badge).append(Axolotlclient.CONFIG.NickHider.hideOtherNames ? new LiteralText(Axolotlclient.CONFIG.NickHider.otherName) : args.get(1));
			badgesText.setStyle(Style.EMPTY.withExclusiveFormatting(Formatting.WHITE).withFont(Axolotlclient.FONT));

			AbstractClientPlayerEntity player = args.get(0);

			if (Axolotlclient.features && Axolotlclient.CONFIG.badgeOptions.showBadge && Axolotlclient.isUsingClient(player.getUuid())) {
				if (player.getUuid() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)) {

					args.set(1, new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ? Axolotlclient.CONFIG.badgeOptions.badgeText + " " : Axolotlclient.badge + " ").setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)).append(Axolotlclient.CONFIG.NickHider.hideOwnName ? new LiteralText(Axolotlclient.CONFIG.NickHider.OwnName) : args.get(1)).setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)));
				} else {
					args.set(1, (Axolotlclient.CONFIG.badgeOptions.CustomBadge ? new LiteralText(Axolotlclient.CONFIG.badgeOptions.badgeText + " ").append(Axolotlclient.CONFIG.NickHider.hideOtherNames ? new LiteralText(Axolotlclient.CONFIG.NickHider.otherName) : args.get(1)) : badgesText));
				}
				return;
			}
			if (Axolotlclient.CONFIG.NickHider.hideOtherNames && Axolotlclient.features) {
				assert MinecraftClient.getInstance().player != null;
				if (player.getName() != MinecraftClient.getInstance().player.getName()) {
					args.set(1, new LiteralText(Axolotlclient.CONFIG.NickHider.otherName));
				}
			}
		}
	}

	@Inject(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;", at=@At("TAIL"), cancellable = true)
	public void hideSkins(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfoReturnable<Identifier> cir){

		if(Axolotlclient.CONFIG != null) {
			if (Axolotlclient.CONFIG.NickHider.hideOtherSkins && abstractClientPlayerEntity.getUuid() != (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)) {
				cir.setReturnValue(DefaultSkinHelper.getTexture(abstractClientPlayerEntity.getUuid()));
				cir.cancel();
			} else if (Axolotlclient.CONFIG.NickHider.hideOwnSkin && abstractClientPlayerEntity.getUuid() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : null)) {
				cir.setReturnValue(DefaultSkinHelper.getTexture(MinecraftClient.getInstance().player.getUuid()));
				cir.cancel();
			}
		}
	}*/

}
