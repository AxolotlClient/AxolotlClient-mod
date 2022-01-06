package io.github.moehreag.branding.mixin;

import io.github.moehreag.branding.NetworkHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Mixin(Window.class)
public class MixinWindow {
	@Inject(method = "close", at = @At("HEAD"))
	private void AxolotlClientLogout(CallbackInfo ci){
		NetworkHelper.setOffline();
	}
}
