package io.github.moehreag.axolotlclient.mixin;


import io.github.moehreag.axolotlclient.modules.hud.HudEditScreen;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen{

	protected MixinTitleScreen() {
		super(Text.of(""));
	}

	@Inject(method = "initWidgetsNormal", at = @At("HEAD"))
	public void inMenu(int y, int spacingY, CallbackInfo ci){
		DiscordRPC.startup();
	}

	@ModifyArgs(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Lnet/minecraft/client/gui/widget/ButtonWidget$TooltipSupplier;)V", ordinal = 1))
	public void noRealmsbutOptionsButton(Args args){
		args.set(4, Text.translatable("config"));
		args.set(5, (ButtonWidget.PressAction) buttonWidget ->
			MinecraftClient.getInstance().setScreen(new HudEditScreen(this)));
	}

	@Inject(method = "init", at = @At("HEAD"))
	public void showBadModsScreen(CallbackInfo ci){

		/*if (Axolotlclient.showWarning) {
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
		}*/
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void addDisclaimer(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
		/*if(Axolotlclient.TitleDisclaimer){
			TitleScreen.drawCenteredText(matrices, this.textRenderer, "You are playing at your own risk with unsupported Mods",
				this.width/2, 5, 0xFFCC8888);
			TitleScreen.drawCenteredText(matrices, this.textRenderer, "Things could break!", this.width/2, 15, 0xFFCC8888);
		}*/
	}
}
