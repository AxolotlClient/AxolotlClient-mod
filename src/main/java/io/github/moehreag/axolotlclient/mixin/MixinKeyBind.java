package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.moehreag.axolotlclient.util.Hooks;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBind.class)
public abstract class MixinKeyBind {

	@Inject(method = "setBoundKey", at = @At("RETURN"))
	public void boundKeySet(InputUtil.Key key, CallbackInfo ci) {
		Hooks.KEYBIND_CHANGE.invoker().setBoundKey(key);
	}

	@Inject(method = "setPressed", at = @At("RETURN"))
	public void onPress(boolean pressed, CallbackInfo ci) {
		if (pressed) {
			Hooks.KEYBIND_PRESS.invoker().onPress((KeyBind)((Object)this));
		}
	}
}
