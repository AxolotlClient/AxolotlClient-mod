package io.github.moehreag.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hypixel.autogg.AutoGG;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract boolean isChatFocused();

    @Shadow public abstract float getChatScale();

    @Shadow public abstract int getVisibleLineCount();

    @Shadow @Final private List<ChatHudLine> visibleMessages;

    @Shadow public abstract int getWidth();

    @Shadow private int scrolledLines;

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    public void autoGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci){
        AutoGG.Instance.onMessage(message);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V"))
    public void customChatHud(int x, int y, int x2, int y2, int color){
        io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
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
        io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
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

    /*@Inject(method = "getTextAt", at = @At("HEAD"), cancellable = true)
    public void modifyCoords(int x, int y, CallbackInfoReturnable<Text> cir){
        io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.isEnabled()) {

            int newY;

            Window window = new Window(client);
            hud.scale();
            DrawPosition pos = hud.getPos();
            newY=y+(window.getHeight()-pos.y);
            GlStateManager.popMatrix();

            //System.out.println(y);
            cir.setReturnValue(getTextAt(pos.x, newY));

        }
    }

    public Text getTextAt(int x, int y){

        if (!this.isChatFocused()) {
            return null;
        } else {
            Window window = new Window(this.client);
            int i = window.getScaleFactor();
            float f = this.getChatScale();
            int j = x / i - 3;
            int k = y / i - 27;
            j = MathHelper.floor((float)j / f);
            k = MathHelper.floor((float)k / f);
            if (j >= 0 && k >= 0) {
                int l = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
                if (j <= MathHelper.floor((float)this.getWidth() / this.getChatScale()) && k < this.client.textRenderer.fontHeight * l + l) {
                    int m = k / this.client.textRenderer.fontHeight + this.scrolledLines;
                    if (m >= 0 && m < this.visibleMessages.size()) {
                        ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(m);
                        int n = 0;
                        Iterator<Text> iterator = chatHudLine.getText().iterator();

                        while(iterator.hasNext()) {
                            Text text = (Text)iterator.next();
                            if (text instanceof LiteralText) {
                                n += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText)text).getRawString(), false));
                                if (n > j) {
                                    return text;
                                }
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        /*if (this.isChatFocused()) {
            Window window = new Window(this.client);
            int i = window.getScaleFactor();
            float f = this.getChatScale();
            int j = x / i - 3;
            int k = y / i - 27;
            j = MathHelper.floor((float) j / f);
            k = MathHelper.floor((float) k / f);
            if (j >= 0 && k >= 0) {
                int l = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
                if (j <= MathHelper.floor((float) this.getWidth() / this.getChatScale()) && k < this.client.textRenderer.fontHeight * l + l) {
                    int m = k / this.client.textRenderer.fontHeight + this.scrolledLines;
                    if (m >= 0 && m < this.visibleMessages.size()) {
                        ChatHudLine chatHudLine = this.visibleMessages.get(m);
                        int n = 0;

                        for (Text text : chatHudLine.getText()) {
                            if (text instanceof LiteralText) {
                                n += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText) text).getRawString(), false));
                                if (n > j) {
                                    return text;
                                }
                            }
                        }
                    }

                }
            }
        }
        return null;
    }*/
}
