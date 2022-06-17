package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.ToggleSprintHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {


    /**
     * @author moehreag
     * @param instance The sneak key
     * @return boolean whether the player should be sneaking or not
     */
    @Redirect(method = "method_1302", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/KeyBinding;isPressed()Z", ordinal = 5))
    public boolean toggleSneak(KeyBinding instance){
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getINSTANCE().get(ToggleSprintHud.ID);
        return hud.isEnabled() && hud.sneakToggled.get() && MinecraftClient.getInstance().currentScreen==null ||
                instance.isPressed();
    }
}
