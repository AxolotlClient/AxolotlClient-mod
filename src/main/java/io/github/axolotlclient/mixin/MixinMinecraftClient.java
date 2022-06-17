package io.github.axolotlclient.mixin;

import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.util.DiscordRPC;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	/**
	 * @author meohreag
	 * @reason Customize Window title for use in AxolotlClient
	 */
	@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
	private void getWindowTitle(CallbackInfoReturnable<String> cir) {

		cir.setReturnValue("AxolotlClient" + " " +SharedConstants.getGameVersion().getName());
	}

	@Redirect(
		method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/RunArgs$Game;version:Ljava/lang/String;"))
	private String redirectVersion(RunArgs.Game game) {
		return SharedConstants.getGameVersion().getName();
	}

	@Redirect(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/RunArgs$Game;versionType:Ljava/lang/String;"
		)
	)
	private String redirectVersionType(RunArgs.Game game) {
		String versionType = game.versionType;

		if (versionType.endsWith("Fabric")) {
			if (versionType.endsWith("/Fabric")) {
				return versionType.substring(0, versionType.length() - 7);
			}

			return "release";
		}

		return versionType;
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;checkIs64Bit()Z"))
	public void startup(RunArgs runArgs, CallbackInfo ci){
		DiscordRPC.startup();
	}

	@Inject(method = "stop", at = @At("HEAD"))
	public void stop(CallbackInfo ci){
		NetworkHelper.setOffline();
		DiscordRPC.shutdown();
	}

	@Inject(method = "startOnlineMode", at = @At(value = "HEAD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;method_1236(Lnet/minecraft/entity/player/PlayerEntity;)V"))
	public void login(CallbackInfo ci){
		NetworkHelper.setOnline();
	}
}
