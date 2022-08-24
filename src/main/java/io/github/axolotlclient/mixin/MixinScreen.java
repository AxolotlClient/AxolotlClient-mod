package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"))
    public void modifyTooltipPosition(Args args) {
        if (ScrollableTooltips.getInstance().enabled.get()) {

            if ((MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) &&
                ((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).getSelectedTab() != ItemGroup.INVENTORY.getIndex()) {
                return;
            }

            args.set(3, (int) args.get(3) + ScrollableTooltips.getInstance().tooltipOffsetX);
            args.set(4, (int) args.get(4) + ScrollableTooltips.getInstance().tooltipOffsetY);
        }
    }
}
