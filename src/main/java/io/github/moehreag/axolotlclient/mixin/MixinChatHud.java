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
		String sender;

		if (message.getString().contains(">")){
			sender = message.getString().split(">")[0].split("<")[1];
		} else if (message.getString().contains(":")){
			sender = message.getString().split(":")[0];
			if (sender.contains(" "))return message;
		} else {return message;}

		if (Objects.equals(sender, MinecraftClient.getInstance().player.getName().getString())){
			name.append(Axolotlclient.CONFIG.NickHider.hideOwnName ? new LiteralText(Axolotlclient.CONFIG.NickHider.OwnName): MinecraftClient.getInstance().player.getName());
		} else {
			name.append(new LiteralText(Axolotlclient.CONFIG.NickHider.hideOtherNames ? Axolotlclient.CONFIG.NickHider.otherName: sender));
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
