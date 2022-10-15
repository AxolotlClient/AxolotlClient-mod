package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/gui/hud/ChatMessageTag;)V", at = @At("HEAD"))
	public void autoThings(Text message, MessageSignature signature, ChatMessageTag tag, CallbackInfo ci){
		AutoGG.Instance.onMessage(message);
		AutoBoop.Instance.onMessage(message);
	}

	@ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/gui/hud/ChatMessageTag;)V",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/gui/hud/ChatMessageTag;Z)V"), index = 0)
	public Text editChat(Text message) {
        return NickHider.Instance.editMessage(message);
	}
}
