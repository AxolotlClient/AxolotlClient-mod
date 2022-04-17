package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.config.Color;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawUtil;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getTextAt(II)Lnet/minecraft/text/Text;"))
    public void redirectMousePos(Args args){
        io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.isEnabled()){
            Window window = new Window(MinecraftClient.getInstance());
            int x=args.get(0);
            int y = args.get(1);

            /*Rectangle rectX = new Rectangle(0,
                    ((window.getHeight() -(y/window.getScaleFactor())-1) + (window.getHeight() - hud.getPos().y))-58*2,
                    window.getWidth(), 1);
            Rectangle rectY = new Rectangle((x/ window.getScaleFactor()) - hud.getPos().x, 0, 1, window.getHeight());
            DrawUtil.fillRect(rectX, Color.ERROR);
            DrawUtil.fillRect(rectY, Color.ERROR);*/

            args.set(0, ((x/window.getScaleFactor() ) - (hud.getPos().x))+ 8*window.getScaleFactor());
            args.set(1, (MinecraftClient.getInstance().height- (((window.getHeight() -(y/window.getScaleFactor())-1) + (window.getHeight() - hud.getPos().y))-58*2)*window.getScaleFactor()));
        }
    }

    @ModifyArgs(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getTextAt(II)Lnet/minecraft/text/Text;"))
    public void redirectMousePosOnClick(Args args){
        io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud hud = (io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud) HudManager.getINSTANCE().get(io.github.moehreag.axolotlclient.modules.hud.gui.hud.ChatHud.ID);
        if(hud.isEnabled()){
            int x=args.get(0);
            int y = args.get(1);
            Window window = new Window(MinecraftClient.getInstance());
            
            args.set(0, ((x/window.getScaleFactor() ) - (hud.getPos().x))+ 8*window.getScaleFactor());//-2*window.getScaleFactor()
            args.set(1, (MinecraftClient.getInstance().height- (((window.getHeight() -(y/window.getScaleFactor())-1) + (window.getHeight() - hud.getPos().y))-58*2)*window.getScaleFactor()));

        }

    }
}
