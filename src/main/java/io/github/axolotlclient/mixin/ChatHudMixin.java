package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract int getWidth();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    public void autoGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
        AutoGG.Instance.onMessage(message);
        AutoBoop.Instance.onMessage(message);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V"))
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
    }
}
