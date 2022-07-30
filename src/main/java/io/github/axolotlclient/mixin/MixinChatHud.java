package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.text.Text;
import net.minecraft.unmapped.C_bzcwstys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Inject(method = "method_44811", at = @At("HEAD"))
	public void autoThings(Text message, MessageSignature messageSignature, C_bzcwstys c_bzcwstys, CallbackInfo ci){
		AutoGG.Instance.onMessage(message);
		AutoBoop.Instance.onMessage(message);
	}

	@ModifyArg(method = "method_1815", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine;<init>(ILnet/minecraft/text/Text;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/unmapped/C_bzcwstys;)V"), index = 1)
	public Text editChat(Text message) {
        return NickHider.Instance.editMessage(message);
	}

	@ModifyArg(method = "method_1815", at = @At(value = "INVOKE", target =  "Lnet/minecraft/client/gui/hud/ChatHud;method_45027(Lnet/minecraft/text/Text;Lnet/minecraft/unmapped/C_bzcwstys;)V"))
	public Text noNamesInLogIfHidden(Text text){
		return editChat(text);
	}
}
