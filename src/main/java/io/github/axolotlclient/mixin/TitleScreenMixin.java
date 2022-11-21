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

import io.github.axolotlclient.modules.hud.HudEditScreen;
import io.github.axolotlclient.modules.hud.HudManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Inject(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIILjava/lang/String;)V", ordinal = 2), cancellable = true)
    public void customTextures(int y, int spacingY, CallbackInfo ci){
        this.buttons.add(new ButtonWidget(192, this.width / 2 - 100, y + spacingY * 2, 200, 20, I18n.translate("axolotlclient.config")+"..."));
        ci.cancel();
    }

    @Inject(method = "buttonClicked", at = @At("TAIL"))
    public void onClick(ButtonWidget button, CallbackInfo ci){
        if(button.id==192) MinecraftClient.getInstance().openScreen(new HudEditScreen(this));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0))
    public void customBranding(TitleScreen instance, TextRenderer textRenderer, String s, int x, int y, int color){
        if(FabricLoader.getInstance().getModContainer("axolotlclient").isPresent()) {
            instance.drawWithShadow(textRenderer,
                    "Minecraft 1.8.9/"+ClientBrandRetriever.getClientModName() +
                            " " + FabricLoader.getInstance().getModContainer("axolotlclient").get()
                            .getMetadata().getVersion().getFriendlyString(), x, y, color);
        } else {
            instance.drawWithShadow(textRenderer, s, x, y, color);
        }
    }
}
