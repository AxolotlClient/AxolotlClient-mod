package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.NetworkHelper;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class MixinWindow {
	@Inject(method = "close", at = @At("HEAD"))
	private void AxolotlClientLogout(CallbackInfo ci){
		if (Axolotlclient.features) NetworkHelper.setOffline();
		DiscordRPC.shutdown();
	}
}
