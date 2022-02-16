package io.github.moehreag.axolotlclient.mixin;

import com.intro.client.render.drawables.PingDisplay;
import io.github.moehreag.axolotlclient.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PingDisplay.class)
public class MixinPingHUDOsmium {

	@ModifyArg(method = "render", at=@At(value = "INVOKE", target = "Lcom/intro/client/render/drawables/PingDisplay;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index = 2)
	public String realPing(String par3){
		return Util.currentServerPing+" ms";
	}
}
