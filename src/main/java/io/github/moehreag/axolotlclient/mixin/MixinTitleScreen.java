package io.github.moehreag.axolotlclient.mixin;


import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.hud.HudEditScreen;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

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

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), index = 2)
	public String setVersionText(String s){
		return "Minecraft "+ SharedConstants.getGameVersion().getName() +
			"/AxolotlClient "+
			(QuiltLoader.getModContainer("axolotlclient").isPresent() ?
				QuiltLoader.getModContainer("axolotlclient").get().metadata().version().raw():"");
	}

	@Inject(method = "init", at = @At("HEAD"))
	public void showBadModsScreen(CallbackInfo ci){

		if (AxolotlClient.showWarning) {
			MinecraftClient.getInstance().setScreen(new ConfirmScreen(
				(boolean confirmed) -> {
					if (confirmed) {
						AxolotlClient.showWarning = false;
						AxolotlClient.titleDisclaimer = true;
						System.out.println("Proceed with Caution!");
						MinecraftClient.getInstance().setScreen(new TitleScreen());
					} else {
						MinecraftClient.getInstance().stop();
					}
				},
				Text.literal("Axolotlclient warning").formatted(Formatting.RED),
				Text.literal("The mod ").append(
					Text.literal(AxolotlClient.badmod).formatted(Formatting.BOLD, Formatting.DARK_RED)).append(" is most likely prohibited to be used on many Servers!\n" +
					"AxolotlClient will not be responsible for any punishment you will get for using it. Proceed with Caution!"),
				Text.literal("Proceed"), Text.translatable("menu.quit")));
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void addDisclaimer(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
		if(AxolotlClient.titleDisclaimer){
			TitleScreen.drawCenteredText(matrices, this.textRenderer, "You are playing at your own risk with unsupported Mods",
				this.width/2, 5, 0xFFCC8888);
			TitleScreen.drawCenteredText(matrices, this.textRenderer, "Things could break!", this.width/2, 15, 0xFFCC8888);
		}
	}
}
