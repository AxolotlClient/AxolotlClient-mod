package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.Hooks;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse {

	@Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBind;setKeyPressed(Lcom/mojang/blaze3d/platform/InputUtil$Key;Z)V"))
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		if (action == 1) {
			Hooks.MOUSE_INPUT.invoker().onMouseButton(window, button, action, mods);
		}
	}

	@ModifyArg(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
	public double scrollZoom(double scrollAmount){
		if(scrollAmount != 0 && Zoom.scroll(scrollAmount)){
			return 0;
		}
		return scrollAmount;
	}
}