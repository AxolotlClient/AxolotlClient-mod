package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(ChatHud.class)
public class MixinChatHud {

	@ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"))
	public Text editChat(Text message){
		assert MinecraftClient.getInstance().player != null;

		LiteralText name = new LiteralText("");

		LiteralText editedMessage = new LiteralText("");

		if(!message.getString().contains("<")){return message;}

		String sender = message.getString().split(">")[0].split("<")[1];

		if (Objects.equals(sender, MinecraftClient.getInstance().player.getName().getString())){
			name.append(Axolotlclient.CONFIG.hideOwnName ? new LiteralText(Axolotlclient.CONFIG.OwnName): MinecraftClient.getInstance().player.getName());
		} else {
			name.append(new LiteralText(Axolotlclient.CONFIG.hideOtherNames ? Axolotlclient.CONFIG.otherName: sender));
		}

			String[] msg = message.getString().split(message.getString().split(">")[0].split("<")[1]);
			for (String s: msg){
				editedMessage.append(s);
				if (Objects.equals(s, msg[0]) && Axolotlclient.CONFIG.badgeOptions.showChatBadge){editedMessage.append(Axolotlclient.badge + " ");}
				if(!Objects.equals(s, msg[msg.length - 1])){editedMessage.append(name);}

			}

		return editedMessage.setStyle(Style.EMPTY.withFont(Axolotlclient.FONT));
	}
}
