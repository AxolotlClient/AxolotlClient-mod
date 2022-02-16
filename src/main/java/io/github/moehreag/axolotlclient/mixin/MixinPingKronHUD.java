package io.github.moehreag.axolotlclient.mixin;

import io.github.darkkronicle.kronhud.gui.hud.PingHud;
import io.github.moehreag.axolotlclient.util.Util;
import net.minecraft.obfuscate.DontObfuscate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PingHud.class)
public class MixinPingKronHUD {


	@Inject(method = "getValue", at = @At("HEAD"), cancellable = true, remap = false)
	@DontObfuscate
	public void realPing(CallbackInfoReturnable<String> cir){
		cir.setReturnValue(Util.currentServerPing + " ms");
		cir.cancel();
	}
}
