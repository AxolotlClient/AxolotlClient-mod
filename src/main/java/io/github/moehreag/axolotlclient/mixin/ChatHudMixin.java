package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.modules.hypixel.autogg.AutoGG;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    public void autoGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
        AutoGG.Instance.onMessage(message);
    }
}
