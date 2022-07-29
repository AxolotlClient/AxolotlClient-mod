package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.itemgroup.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow public int height;

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Ljava/util/List;II)V"))
    public void modifyTooltipPosition(Args args){
        if(ScrollableTooltips.instance.enabled.get()) {

            if((MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) &&
                    ((CreativeInventoryScreen)MinecraftClient.getInstance().currentScreen).getSelectedTab() != ItemGroup.INVENTORY.getIndex()){
                return;
            }

            ScrollableTooltips.instance.onRenderTooltip();
            args.set(1, (int)args.get(1) + ScrollableTooltips.instance.tooltipOffsetX);
            args.set(2, (int)args.get(2) + ScrollableTooltips.instance.tooltipOffsetY);
        }
    }

    @ModifyConstant(method = "renderTooltip(Ljava/util/List;II)V", constant = @Constant(intValue = 6))
    public int noLimit(int constant){

        return - (height*2);
    }

}
