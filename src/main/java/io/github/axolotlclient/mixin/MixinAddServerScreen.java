package io.github.axolotlclient.mixin;

import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public abstract class MixinAddServerScreen {

    @Shadow private TextFieldWidget serverNameField;

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void noNameLimit(CallbackInfo ci){
        serverNameField.setMaxLength(1024);
    }

}
