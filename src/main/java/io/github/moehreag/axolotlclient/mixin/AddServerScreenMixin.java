package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public class AddServerScreenMixin {

    @Shadow private TextFieldWidget serverNameField;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;<init>(ILnet/minecraft/client/font/TextRenderer;IIII)V", ordinal = 1))
    public void noNameLimit(CallbackInfo ci){
        serverNameField.setMaxLength(1024);
    }

}
