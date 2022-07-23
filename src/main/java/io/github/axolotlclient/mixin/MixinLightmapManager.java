package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager {

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;gamma:D"))
	public double fullBright(GameOptions instance){
		if(AxolotlClient.CONFIG.fullBright.get()){
            return 1500;
        }
        else return instance.gamma;
	}
}
