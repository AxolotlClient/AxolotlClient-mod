package io.github.axolotlclient.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen {

    @Inject(method = "initWidgets", at = @At("RETURN"))
    public void addConfigButton(CallbackInfo ci){
        /*if(MinecraftClient.getInstance().isInSingleplayer() && MinecraftClient.getInstance().getServer().isDedicated()) {
            buttons.add(new ButtonWidget(20, width / 2 - 100, height / 4 +
                    (FabricLoader.getInstance().isModLoaded("modmenu")? 82 :80),
                    I18n.translate("config")));
            for (ButtonWidget button : buttons) {
                if (button.y >= this.height / 4 - 16 + 24 * 4 - 1 && !(button.id == 20)) {
                    button.y += 24;
                }
                //button.y -= 12;
            }
        } else {
            for (ButtonWidget button:buttons){
                if(!button.active && button.id==20){
                    button.active=true;
                }
            }
        }
    }

    @ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIIILjava/lang/String;)V", ordinal = 1))
    public void addOptionsButton(Args args){
        if((MinecraftClient.getInstance().getServer()!=null && !MinecraftClient.getInstance().getServer().isDedicated())
                || MinecraftClient.getInstance().getCurrentServerEntry() != null){
            args.set(0, 20);
            args.set(5, I18n.translate("title_short"));
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    public void customButtons(ButtonWidget button, CallbackInfo ci){
        if(button.id==20){
            MinecraftClient.getInstance().openScreen(new HudEditScreen(new GameMenuScreen()));
        }*/
    }
}
