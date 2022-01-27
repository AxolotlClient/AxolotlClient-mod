package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public class MixinInGameHud {



	@Inject(method = "addChatMessage", at = @At("HEAD"))
	private void addChatMessage(MessageType type, Text message, UUID senderUuid, CallbackInfo ci) {
		if (senderUuid != Util.NIL_UUID && message instanceof TranslatableText && ((TranslatableText) message).getKey().equals("chat.type.text")) {
			Object[] args = ((TranslatableText) message).getArgs();
			if (args.length > 0 && args[0] instanceof MutableText playerName) {
				if (Axolotlclient.features && Axolotlclient.isUsingClient(senderUuid) && Axolotlclient.CONFIG.showBadge && Axolotlclient.CONFIG.badgeOptions.showChatBadge){
					playerName.append(new LiteralText(" "+(Axolotlclient.CONFIG.badgeOptions.CustomBadge ? Axolotlclient.CONFIG.badgeOptions.badgeText: Axolotlclient.badge)).setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)));
				}
			}
		}
	}
}
