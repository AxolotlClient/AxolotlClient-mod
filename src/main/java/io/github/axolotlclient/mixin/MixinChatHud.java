package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {

	@Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
	public void autoGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
		AutoGG.Instance.onMessage(message);
		AutoBoop.Instance.onMessage(message);
	}

	@ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"))
	public Text editChat(Text message) {

		String msg = message.getString();
		String newMsg;

		String playerName = MinecraftClient.getInstance().player.getName().getString();
		if (NickHider.Instance.hideOwnName.get() && msg.contains(playerName)){
			newMsg = msg.replace(playerName, NickHider.Instance.hiddenNameSelf.get());
			return Text.literal(newMsg);
		}
		/*|| AxolotlClient.CONFIG.NickHider.hideOtherNames) {
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
				if (!Objects.equals(s, msg[msg.length - 1])) {
					editedMessage.append(name);
				}
			}


		if (!editedMessage.getString().split(":")[0].contains("<") && editedMessage.getString().split(":")[0].contains("] " ) && !editedMessage.getString().contains("[NPC]")){
			editedMessage = new LiteralText(editedMessage.getString().split("] ")[editedMessage.getString().split("] ").length-1]);
		}


			return editedMessage.setStyle(message.getStyle().withFont(Axolotlclient.FONT));
		}*/
		return message;
	}

}
