package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager {

	@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/SimpleOption;"))
	public SimpleOption<Double> fullBright(GameOptions instance){
		if(AxolotlClient.CONFIG.fullBright.get()) return new SimpleOption<>("options.gamma", SimpleOption.emptyTooltip(),
			(optionText, value) -> optionText, SimpleOption.DoubleSliderCallbacks.INSTANCE, 15D, value -> {});
		return instance.getGamma();
	}
}
