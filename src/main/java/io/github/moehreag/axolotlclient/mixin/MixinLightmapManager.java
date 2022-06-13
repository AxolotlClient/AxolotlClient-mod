package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.AxolotlClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapManager {

	@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/Option;"))
	public Option<Double> fullBright(GameOptions instance){
		if(AxolotlClient.CONFIG.fullBright.get()) return new Option<>("options.gamma", Option.emptyTooltip(),
			(optionText, value) -> optionText, Option.UnitDoubleValueSet.INSTANCE, 15D, value -> {});
		return instance.getGamma();
	}
}
