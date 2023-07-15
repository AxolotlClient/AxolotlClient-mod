package io.github.axolotlclient.mixin;

import java.util.List;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
		at = @At("STORE"), index = 11)
	private int axolotlclient$scrollableTooltipsX(int x){
		if (ScrollableTooltips.getInstance().enabled.get()) {
			if ((MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen)
				&& !((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).isInventoryOpen()) {
				return x;
			}

			return x + ScrollableTooltips.getInstance().tooltipOffsetX;
		}
		return x;
	}

	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
		at = @At("STORE"), index = 12)
	private int axolotlclient$scrollableTooltipsY(int y){
		if (ScrollableTooltips.getInstance().enabled.get()) {
			if ((MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen)
				&& !((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).isInventoryOpen()) {
				return y;
			}
			return y + ScrollableTooltips.getInstance().tooltipOffsetY;
		}
		return y;
	}

}
