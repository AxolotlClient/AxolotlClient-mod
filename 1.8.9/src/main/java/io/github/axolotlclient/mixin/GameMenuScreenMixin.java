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
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.util.FeatureDisabler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    @Inject(method = "init", at = @At("RETURN"))
    public void axolotlclient$addConfigButton(CallbackInfo ci) {
        if (MinecraftClient.getInstance().isInSingleplayer() && !this.client.getServer().isPublished()) {
            buttons.add(new ButtonWidget(20, width / 2 - 100,
                    height / 4 + (!axolotlclient$alternateLayout() ? 82 : 80),
                    I18n.translate("config")));
            for (ButtonWidget button : buttons) {
                if (button.y >= this.height / 4 - 16 + 24 * 4 - 1 && !(button.id == 20)) {
                    button.y += 24;
                }
                //button.y -= 12;
            }
        } else {
            for (ButtonWidget button : buttons) {
                if (!button.active && button.id == 20) {
                    button.active = true;
                }
            }
        }
    }

    @ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIIILjava/lang/String;)V", ordinal = 1))
    public void axolotlclient$addOptionsButton(Args args) {
        if ((MinecraftClient.getInstance().getServer() != null
                && MinecraftClient.getInstance().getServer().isPublished())
                || MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            args.set(0, 20);
            args.set(5, I18n.translate("title_short"));
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    public void axolotlclient$customButtons(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 20) {
            MinecraftClient.getInstance().setScreen(new HudEditScreen((GameMenuScreen) (Object) this));
        } else if (button.id == 1) {
            FeatureDisabler.clear();
            if (HypixelMods.getInstance().cacheMode.get() != null
                    && Objects.equals(HypixelMods.getInstance().cacheMode.get(),
                    HypixelMods.HypixelApiCacheMode.ON_CLIENT_DISCONNECT.toString())) {
                HypixelAbstractionLayer.clearPlayerData();
            }
        }
    }

    private boolean axolotlclient$alternateLayout() {
        return FabricLoader.getInstance().isModLoaded("modmenu") && !FabricLoader.getInstance().isModLoaded("axolotlclient-modmenu");
    }
}
