package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(ChatHud.class)
public class MixinChatHud {

	@ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"))
	public Text editChat(Text message) {

		if (Axolotlclient.CONFIG.NickHider.hideOwnName || Axolotlclient.CONFIG.NickHider.hideOtherNames) {
			assert MinecraftClient.getInstance().player != null;

			LiteralText name = new LiteralText("");

			LiteralText editedMessage = new LiteralText("");
			String sender;

			if (message.getString().contains(">")) {
				sender = message.getString().split(">")[0].split("<")[1];
			} else if (message.getString().contains(":")) {
				sender = message.getString().split(":")[0];
				if (sender.contains("]")) {
					String[] send = sender.split("] ");
					sender = send[send.length - 1];
				}if (sender.contains("[NPC]"))return message;
				if (sender.contains(" ")) return message;
			} else {return message;}

			if (Objects.equals(sender, MinecraftClient.getInstance().player.getName().getString())) {
				name.append(Axolotlclient.CONFIG.NickHider.hideOwnName ? new LiteralText(Axolotlclient.CONFIG.NickHider.OwnName) : MinecraftClient.getInstance().player.getName()).setStyle(message.getStyle());
			} else {
				name.append(new LiteralText(Axolotlclient.CONFIG.NickHider.hideOtherNames ? Axolotlclient.CONFIG.NickHider.otherName : sender).setStyle(message.getStyle()));
			}

			String[] msg = message.getString().split(sender);
			for (String s : msg) {
				editedMessage.append(s);
				/*
				if (Objects.equals(s, msg[0]) && Axolotlclient.CONFIG.badgeOptions.showChatBadge && Axolotlclient.onlinePlayers.contains(sender)) {
					editedMessage.append(Axolotlclient.badge);
				}*/
				if (!Objects.equals(s, msg[msg.length - 1])) {
					editedMessage.append(name);
				}
			}


		if (!editedMessage.getString().split(":")[0].contains("<") && editedMessage.getString().split(":")[0].contains("] " ) && !editedMessage.getString().contains("[NPC]")){
			editedMessage = new LiteralText(editedMessage.getString().split("] ")[editedMessage.getString().split("] ").length-1]);
		}


			return editedMessage.setStyle(message.getStyle().withFont(Axolotlclient.FONT));
		}
		return message;
	}

}
