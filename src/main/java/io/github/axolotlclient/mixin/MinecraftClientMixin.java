package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.modules.rpc.DiscordRPC;
import io.github.axolotlclient.modules.sky.SkyResourceManager;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.ResourceType;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

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
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(SkyResourceManager.getInstance());
		return SharedConstants.getGameVersion().getName();
	}

	@Inject(method = "getVersionType", at = @At("HEAD"), cancellable = true)
    public void noVersionType(CallbackInfoReturnable<String> cir){
        if(QuiltLoader.getModContainer("axolotlclient").isPresent()) {
            cir.setReturnValue(QuiltLoader.getModContainer("axolotlclient").get().metadata().version().raw());
        }
    }

	@Inject(method = "stop", at = @At("HEAD"))
	public void stop(CallbackInfo ci){
		if(AxolotlClient.CONFIG.showBadges.get()) {
			NetworkHelper.setOffline();
		}
		DiscordRPC.shutdown();
	}
}
