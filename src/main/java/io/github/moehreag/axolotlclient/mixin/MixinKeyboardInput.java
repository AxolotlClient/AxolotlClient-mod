package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.ToggleSprintHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {


    /**
     * @author moehreag
     * @param instance The sneak key
     * @return boolean whether the player should be sneaking or not
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBind;isPressed()Z", ordinal = 5))
    public boolean toggleSneak(KeyBind instance){
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getINSTANCE().get(ToggleSprintHud.ID);
        return hud.isEnabled() && hud.sneakToggled.get() && MinecraftClient.getInstance().currentScreen==null ||
                instance.isPressed();
    }
}
