package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.util.DiscordRPC;
import io.github.axolotlclient.util.Util;
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
        DiscordRPC.update();
	}

	@Inject(method = "onLeaveGameSession", at=@At("HEAD"))
	public void logout(CallbackInfo ci){
        NetworkHelper.setOffline();
        AxolotlClient.otherPlayers = "";
        Util.game = "";
        Util.lastgame = "";
        DiscordRPC.menu();

	}
}
