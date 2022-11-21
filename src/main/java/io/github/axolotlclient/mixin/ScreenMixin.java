/*
 * This File is part of AxolotlClient (mod)
 * Copyright (C) 2021-present moehreag + Contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.screenshotUtils.ScreenshotUtils;
import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.axolotlclient.util.OSUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.net.URI;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow public int height;

    @ModifyArgs(method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Ljava/util/List;II)V"))
    public void modifyTooltipPosition(Args args){
        if(ScrollableTooltips.getInstance().enabled.get()) {

            if((MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) &&
                    ((CreativeInventoryScreen)MinecraftClient.getInstance().currentScreen).getSelectedTab() != ItemGroup.INVENTORY.getIndex()){
                return;
            }

            ScrollableTooltips.getInstance().onRenderTooltip();
            args.set(1, (int)args.get(1) + ScrollableTooltips.getInstance().tooltipOffsetX);
            args.set(2, (int)args.get(2) + ScrollableTooltips.getInstance().tooltipOffsetY);
        }
    }

    @ModifyConstant(method = "renderTooltip(Ljava/util/List;II)V", constant = @Constant(intValue = 6))
    public int noLimit(int constant){

        return - (height*2);
    }

    @Inject(method = "openLink", at = @At("HEAD"), cancellable = true)
    public void openLink(URI link, CallbackInfo ci){
        OSUtil.getOS().open(link);
        ci.cancel();
    }

    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/ClickEvent;getAction()Lnet/minecraft/text/ClickEvent$Action;", ordinal = 0), cancellable = true)
    public void customClickEvents(Text text, CallbackInfoReturnable<Boolean> cir){
        ClickEvent event = text.getStyle().getClickEvent();
        if(event instanceof ScreenshotUtils.CustomClickEvent){
            ((ScreenshotUtils.CustomClickEvent) event).doAction();
            cir.setReturnValue(true);
        }
    }
}
