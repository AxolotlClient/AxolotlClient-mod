package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

	@Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
	public void autoThings(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
		AutoGG.Instance.onMessage(message);
		AutoBoop.Instance.onMessage(message);
	}

	@ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"))
	public Text editChat(Text message) {

        if(NickHider.Instance.hideOwnName.get() || NickHider.Instance.hideOtherNames.get()) {
            String msg = message.getString();

            String playerName = MinecraftClient.getInstance().player.getDisplayName().getString();
            if (NickHider.Instance.hideOwnName.get() && msg.contains(playerName)) {
                msg = msg.replaceAll(playerName, NickHider.Instance.hiddenNameSelf.get());

            }

            if (NickHider.Instance.hideOtherNames.get()) {
                for (AbstractClientPlayerEntity player : MinecraftClient.getInstance().world.getPlayers()) {
                    if (msg.contains(player.getDisplayName().getString())) {
                        msg = msg.replaceAll(player.getDisplayName().getString(), NickHider.Instance.hiddenNameOthers.get());
                    }
                }
            }


            return Text.literal(msg).copy().setStyle(message.getStyle());
        }
        return message;
	}

	@ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"), index = 1, remap = false)
	public Object noNamesInLogIfHidden(Object o){
		return editChat((Text.of((String) o))).getString();
	}
}
