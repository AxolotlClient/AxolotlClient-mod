package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    public void autoGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
        AutoGG.Instance.onMessage(message);
        AutoBoop.Instance.onMessage(message);
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"))
    public Text editChat(Text message) {
        String msg = message.asString();

        if(NickHider.Instance.hideOwnName.get() || NickHider.Instance.hideOtherNames.get()) {
            String playerName = MinecraftClient.getInstance().player.getCustomName();
            if (NickHider.Instance.hideOwnName.get() && msg.contains(playerName)) {
                msg = msg.replaceAll(playerName, NickHider.Instance.hiddenNameSelf.get());

            }

            if (NickHider.Instance.hideOtherNames.get()) {
                for (PlayerEntity player : MinecraftClient.getInstance().world.playerEntities) {
                    if (msg.contains(player.getCustomName())) {
                        msg = msg.replaceAll(player.getCustomName(), NickHider.Instance.hiddenNameOthers.get());
                    }
                }
            }


            return new LiteralText(msg).setStyle(message.getStyle().deepCopy());
        }
        return message;
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), remap = false)
    public String noNamesInLogIfHidden(String message){
        return editChat(new LiteralText(message)).getString();
    }

    /*@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V"))
    public void customChatHud(int x, int y, int x2, int y2, int color){
        io.github.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.isEnabled()){

            Window window = new Window(client);

            hud.scale();
            DrawPosition pos = hud.getPos();
            if(hud.background.get())DrawableHelper.fill(pos.x-2, pos.y - (window.getHeight()-48)+y+9+70, pos.x + hud.width-2, pos.y +y - (window.getHeight()-48)+70, hud.bgColor.get().getAsInt());
            GlStateManager.popMatrix();

        } else {
            DrawableHelper.fill(0, y, x2, y2, color);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"))
    public int customChat(TextRenderer instance, String text, float x, float y, int color){
        io.github.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.isEnabled()){
            Window window = new Window(client);
            hud.scale();
            DrawPosition pos = hud.getPos();
            instance.drawWithShadow(text, pos.x-2, pos.y - (window.getHeight()-48) + y+70, color);
            GlStateManager.popMatrix();
        } else {
            instance.drawWithShadow(text, x, y, color);
        }
        return 0;
    }*/
}
