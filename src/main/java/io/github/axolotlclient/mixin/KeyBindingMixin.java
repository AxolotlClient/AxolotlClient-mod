package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.Hooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.collection.IntObjectStorage;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

    @Shadow private boolean pressed;

    @Shadow private int code;

    @Shadow @Final private static IntObjectStorage<KeyBinding> KEY_MAP;

    @Inject(method = "isPressed", at = @At("HEAD"))
    public void noMovementFixAfterInventory(CallbackInfoReturnable<Boolean> cir){
        if(this.code == MinecraftClient.getInstance().options.keySneak.getCode() ||
                code == MinecraftClient.getInstance().options.keyForward.getCode() ||
                code == MinecraftClient.getInstance().options.keyBack.getCode() ||
                code == MinecraftClient.getInstance().options.keyRight.getCode() ||
                code == MinecraftClient.getInstance().options.keyLeft.getCode() ||
                code == MinecraftClient.getInstance().options.keyJump.getCode() ||
                code == MinecraftClient.getInstance().options.keySprint.getCode()){
            this.pressed = Keyboard.isKeyDown(code) && (MinecraftClient.getInstance().currentScreen == null);
        }
    }

    @Inject(method = "setCode", at = @At("RETURN"))
    public void boundKeySet(int code, CallbackInfo ci) {
        Hooks.KEYBIND_CHANGE.invoker().setBoundKey(code);
    }

    @Inject(method = "setKeyPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/KeyBinding;pressed:Z"))
    private static void onPress(int keyCode, boolean pressed, CallbackInfo ci){
        if(pressed) {
            Hooks.KEYBIND_PRESS.invoker().onPress(KEY_MAP.get(keyCode));
        }
    }
}
