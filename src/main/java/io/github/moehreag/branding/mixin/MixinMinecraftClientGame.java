package io.github.moehreag.branding.mixin;

import io.github.moehreag.branding.NetworkHelper;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClientGame.class)
public class MixinMinecraftClientGame {
	@Inject(method = "onStartGameSession", at = @At("HEAD"))
	public void startup(CallbackInfo ci){
			NetworkHelper.setOnline();
	}

	@Inject(method = "onLeaveGameSession", at=@At("HEAD"))
	public void logout(CallbackInfo ci){
		NetworkHelper.setOffline();
	}
}
