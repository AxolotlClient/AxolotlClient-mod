package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Shadow private boolean pressed;

    @Shadow private int code;

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
}
