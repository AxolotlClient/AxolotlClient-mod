package io.github.moehreag.branding.mixin;


import io.github.moehreag.branding.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen{

	private static final int textX = 200;
	private static final int textY = 5;

	protected MixinTitleScreen() {
		super(null);
	}

	@Inject(method = "init", at = @At("HEAD"))
	public void showBadModsScreen(CallbackInfo ci){

		if (Axolotlclient.showWarning) {
			MinecraftClient.getInstance().setScreen(new ConfirmScreen(
				(boolean confirmed) -> {
					if (confirmed) {
						Axolotlclient.showWarning = false;
						Axolotlclient.TitleDisclaimer = true;
						System.out.println("Proceed with Caution!");
						MinecraftClient.getInstance().setScreen(new TitleScreen());
					} else {
						MinecraftClient.getInstance().stop();
					}
				},
				new LiteralText("Axolotlclient warning").formatted(Formatting.RED),
				new LiteralText("The mod ").append(
					new LiteralText(Axolotlclient.badmod).formatted(Formatting.BOLD, Formatting.DARK_RED)).append(" is most likely prohibited to be used on many Servers!\n" +
					"I will not be responsible for any punishment you will get for using it. Proceed with Caution!"),
				new LiteralText("Proceed"), new TranslatableText("menu.quit")));
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void addDisclaimer(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
		if(Axolotlclient.TitleDisclaimer){
			TitleScreen.drawCenteredText(matrices, this.textRenderer, "You are playing at your own risk with unsupported Mods",
				textX, textY, 0xFFCC8888);
			TitleScreen.drawCenteredText(matrices, this.textRenderer, "Things could break!", textX, textY+ 10, 0xFFCC8888);
		}
	}
}
