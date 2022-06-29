package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.itemgroup.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Ljava/util/List;II)V"))
    public void modifyTooltipPosition(Args args){
        if(ScrollableTooltips.Instance.enabled.get()) {

            if((MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) &&
                    ((CreativeInventoryScreen)MinecraftClient.getInstance().currentScreen).getSelectedTab() != ItemGroup.INVENTORY.getIndex()){
                return;
            }

            ScrollableTooltips.Instance.onRenderTooltip();
            args.set(1, (int)args.get(1) + ScrollableTooltips.Instance.tooltipOffsetX);
            args.set(2, (int)args.get(2) + ScrollableTooltips.Instance.tooltipOffsetY);
        }
    }

}
