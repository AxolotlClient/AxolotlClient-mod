package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
	/*
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;initWidgetsNormal(II)V"), method = "init")
	private void init(CallbackInfo info) {
		buttons.add(new ButtonWidget(42, this.width/2+20, this.height/2-50, I18n.translate("wtf")));
	}

	@Inject(method = "buttonClicked", at = @At("TAIL"))
	public void click(ButtonWidget button, CallbackInfo ci){

		if(button.id == 42){
			this.client.openScreen(new AxolotlclientScreen());
		}
	}*/

    @Inject(method = "render", at = @At("HEAD"))
    public void inMenu(int mouseX, int mouseY, float tickDelta, CallbackInfo ci){
        DiscordRPC.startup();
    }

}
