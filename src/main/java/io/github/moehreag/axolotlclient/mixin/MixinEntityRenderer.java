package io.github.moehreag.axolotlclient.mixin;


import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	@ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"), index = 4)
	public boolean enableShadows(boolean shadow){
		return Axolotlclient.CONFIG.NametagConf.useShadows;
	}


}
