package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text text) {
		super(text);
	}

	@Inject(method = "render", at = @At("HEAD"))
    public void inMenu(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        DiscordRPC.startup();
    }

    /*@Inject(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIILjava/lang/String;)V", ordinal = 2), cancellable = true)
    public void customTextures(int y, int spacingY, CallbackInfo ci){
        this.buttons.add(new ButtonWidget(192, this.width / 2 - 100, y + spacingY * 2, 200, 20, I18n.translate("config")+"..."));
        ci.cancel();
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    public void onClick(ButtonWidget button, CallbackInfo ci){
        if(button.id==192) MinecraftClient.getInstance().openScreen(new HudEditScreen(this));
    }*/

}
