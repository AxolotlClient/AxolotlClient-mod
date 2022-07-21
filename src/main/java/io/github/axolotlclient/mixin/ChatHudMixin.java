package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow @Final private List<ChatHudLine> messages;

    @Shadow private int scrolledLines;

    @Shadow @Final private List<ChatHudLine> visibleMessages;

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    public void autoGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
        AutoGG.Instance.onMessage(message);
        AutoBoop.Instance.onMessage(message);
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"))
    public Text editChat(Text message) {
        return NickHider.Instance.editMessage(message);
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), cancellable = true, remap = false)
    public void noSuggestions(Text message, int i, CallbackInfo ci){
        if(i!=0) ci.cancel();
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), remap = false)
    public String noNamesInLogIfHidden(String message){
        return editChat(new LiteralText(message)).getString();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V", ordinal = 0))
    public void noBg(int x, int y, int x2, int y2, int color) {
        io.github.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.background.get()) {
            DrawableHelper.fill(x, y, x2, y2, color);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(int ticks, CallbackInfo ci){
        io.github.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.isEnabled()){
            hud.ticks = ticks;
            ci.cancel();
        }
    }

    @Inject(method = "getTextAt", at = @At("HEAD"), cancellable = true)
    public void getTextAt(int x, int y, CallbackInfoReturnable<Text> cir){
        io.github.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud!=null && hud.isEnabled()) {
            DrawPosition pos = Util.toMCCoords(x, y);
            cir.setReturnValue(hud.getTextAt(pos.x, pos.y));
        }
    }

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", constant = @Constant(intValue = 100), expect = 2)
    public int moreChatHistory(int constant){
        io.github.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        int length = hud.chatHistory.get();

        if(length == hud.chatHistory.getMax()){
            return visibleMessages.size()+1;
        }

        return length;
    }
}
