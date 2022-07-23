package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hud.HudEditScreen;
import io.github.axolotlclient.modules.zoom.Zoom;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen{

	protected MixinTitleScreen() {
		super(Text.of(""));
	}

	@Inject(method = "initWidgetsNormal", at = @At("HEAD"))
	public void inMenu(int y, int spacingY, CallbackInfo ci){
		if(MinecraftClient.getInstance().options.keySaveToolbarActivator.equals(Zoom.keyBinding)){
			MinecraftClient.getInstance().options.keySaveToolbarActivator.setBoundKey(InputUtil.UNKNOWN_KEY);
			AxolotlClient.LOGGER.info("Unbound \"Save Toolbar Activator\" to resolve conflict with the zoom key!");
		}
	}

	@ModifyArgs(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Lnet/minecraft/client/gui/widget/ButtonWidget$TooltipSupplier;)V", ordinal = 1))
	public void noRealmsbutOptionsButton(Args args){
		if(!FabricLoader.getInstance().isModLoaded("modmenu")) {
			args.set(4, new TranslatableText("config"));
			args.set(5, (ButtonWidget.PressAction) buttonWidget ->
				MinecraftClient.getInstance().openScreen(new HudEditScreen(this)));
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0), index = 2)
	public String setVersionText(String s){
		return "Minecraft "+ SharedConstants.getGameVersion().getName() +
			"/AxolotlClient "+
			(FabricLoader.getInstance().getModContainer("axolotlclient").isPresent() ?
				FabricLoader.getInstance().getModContainer("axolotlclient").get().getMetadata().getVersion().getFriendlyString():"");
	}

	@Inject(method = "areRealmsNotificationsEnabled", at = @At("HEAD"), cancellable = true)
	public void noRealmsIcons(CallbackInfoReturnable<Boolean> cir){
		cir.setReturnValue(false);
	}

	@Inject(method = "init", at = @At("HEAD"))
	public void showBadModsScreen(CallbackInfo ci){

		if (AxolotlClient.showWarning) {
			MinecraftClient.getInstance().openScreen(new ConfirmScreen(
				(boolean confirmed) -> {
					if (confirmed) {
						AxolotlClient.showWarning = false;
						AxolotlClient.titleDisclaimer = true;
						System.out.println("Proceed with Caution!");
						MinecraftClient.getInstance().openScreen(new TitleScreen());
					} else {
						MinecraftClient.getInstance().stop();
					}
				},
				new LiteralText("Axolotlclient warning").formatted(Formatting.RED),
				new LiteralText("The mod ").append(
					new LiteralText(AxolotlClient.badmod).formatted(Formatting.BOLD, Formatting.DARK_RED)).append(" is most likely prohibited to be used on many Servers!\n" +
					"AxolotlClient will not be responsible for any punishment you will get for using it. Proceed with Caution!"),
				new LiteralText("Proceed"), new TranslatableText("menu.quit")));
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void addDisclaimer(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
		if(AxolotlClient.titleDisclaimer){
			TitleScreen.drawCenteredText(matrices, this.textRenderer, Text.of("You are playing at your own risk with unsupported Mods"),
				this.width/2, 5, 0xFFCC8888);
			TitleScreen.drawCenteredText(matrices, this.textRenderer, Text.of("Things could break!"), this.width/2, 15, 0xFFCC8888);
		}
	}
}
