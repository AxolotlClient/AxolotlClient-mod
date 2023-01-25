/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
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
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudEditScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    public abstract void render(int par1, int par2, float par3);

    @ModifyArgs(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIILjava/lang/String;)V", ordinal = 2))
    private void axolotlclient$replaceRealmsButton(Args args){
        if(!axolotlclient$alternateLayout()){

            args.set(0, 192);
            args.set(3, I18n.translate("config") + "...");
        }
    }

    @Inject(method = "initWidgetsNormal", at = @At("TAIL"))
    private void axolotlclient$addOptionsButton(int y, int spacingY, CallbackInfo ci){
        if(axolotlclient$alternateLayout()){
            buttons.add(new ButtonWidget(192, this.width / 2 - 100, y+spacingY*3, I18n.translate("config") + "..."));
        }
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 72))
    private int axolotlclient$moveButtons(int constant){
        if(axolotlclient$alternateLayout()){
            return constant + 25;
        }
        return constant;
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    public void axolotlclient$onClick(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 192)
            MinecraftClient.getInstance().openScreen(new HudEditScreen(this));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
    public void axolotlclient$customBranding(TitleScreen instance, TextRenderer textRenderer, String s, int x, int y, int color) {
        if (FabricLoader.getInstance().getModContainer("axolotlclient").isPresent()) {
            instance.drawWithShadow(textRenderer,
                    "Minecraft 1.8.9/" + ClientBrandRetriever.getClientModName() + " " + FabricLoader.getInstance()
                            .getModContainer("axolotlclient").get().getMetadata().getVersion().getFriendlyString(),
                    x, y, color);
        } else {
            instance.drawWithShadow(textRenderer, s, x, y, color);
        }
    }

    private boolean axolotlclient$alternateLayout(){
        return FabricLoader.getInstance().isModLoaded("modmenu") && !FabricLoader.getInstance().isModLoaded("axolotlclient-modmenu");
    }
}
