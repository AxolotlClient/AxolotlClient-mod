package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen {

	@ModifyArgs(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 3))
	public void addClientOptionsButton(Args args){
		args.set(4, new TranslatableText("title_short"));
		args.set(5, (ButtonWidget.PressAction)(buttonWidget)-> MinecraftClient.getInstance().openScreen(new HudEditScreen(((GameMenuScreen)(Object)this) )));
	}
}
