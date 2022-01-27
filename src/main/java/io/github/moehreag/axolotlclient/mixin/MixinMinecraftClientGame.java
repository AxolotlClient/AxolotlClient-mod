package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.NetworkHelper;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import io.github.moehreag.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClientGame.class)
public class MixinMinecraftClientGame {
	@Inject(method = "onStartGameSession", at = @At("HEAD"))
	public void startup(CallbackInfo ci){
		if(Axolotlclient.features){
			NetworkHelper.setOnline();

			DiscordRPC.update();
		}
	}

	@Inject(method = "onLeaveGameSession", at=@At("HEAD"))
	public void logout(CallbackInfo ci){
		if(Axolotlclient.features) {
			NetworkHelper.setOffline();
			Axolotlclient.otherPlayers = "";
			Util.game = "";
			Util.lastgame = "";
			DiscordRPC.menu();
		}
	}
}
