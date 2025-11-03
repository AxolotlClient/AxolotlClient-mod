/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.config;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.RecreatableScreen;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.config.screen.CreditsScreen;
import io.github.axolotlclient.config.screen.ProfilesScreen;
import io.github.axolotlclient.mixin.OverlayTextureAccessor;
import io.github.axolotlclient.util.options.ForceableBooleanOption;
import io.github.axolotlclient.util.options.GenericOption;
import lombok.Getter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class AxolotlClientConfig extends AxolotlClientConfigCommon {
	public final BooleanOption customSky = new BooleanOption("customSky", true);

	public final BooleanOption lowShield = new BooleanOption("lowShield", false);
	public final ColorOption hitColor = new ColorOption("hitColor", new Color(255, 0, 0, 77),
		value -> {
			try { // needed because apparently someone created a bug that makes this be called when the config is loaded. Will be fixed with the next release.
				NativeImageBackedTexture texture = ((OverlayTextureAccessor) MinecraftClient.getInstance().gameRenderer.getOverlayTexture()).getTexture();
				NativeImage nativeImage = texture.getImage();
				if (nativeImage != null) {
					int color = 255 - value.getAlpha();
					color = (color << 8) + value.getBlue();
					color = (color << 8) + value.getGreen();
					color = (color << 8) + value.getRed();

					for (int i = 0; i < 8; ++i) {
						for (int j = 0; j < 8; ++j) {
							nativeImage.setPixelColor(j, i, color);
						}
					}

					RenderSystem.activeTexture(33985);
					texture.bindTexture();
					nativeImage.upload(0, 0, 0, 0, 0,
						nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
					RenderSystem.activeTexture(33984);
				}
			} catch (Exception ignored) {
			}
		});

	public final BooleanOption flatItems = new BooleanOption("flatItems", false);
	public final BooleanOption inventoryPotionEffectOffset = new BooleanOption("inventory.potion_effect_offset", true);

	public final ColorOption loadingScreenColor = new ColorOption("loadingBgColor", new Color(239, 50, 61, 255));
	public final BooleanOption nightMode = new BooleanOption("nightMode", false);

	public final BooleanOption outlineChroma = new BooleanOption("chroma", false);

	public final GenericOption openCredits = new GenericOption("Credits", "Open Credits", () ->
		MinecraftClient.getInstance().openScreen(new CreditsScreen(MinecraftClient.getInstance().currentScreen))
	);

	@Getter
	private final List<Option<?>> options = new ArrayList<>();

	public AxolotlClientConfig() {
		general.add(loadingScreenColor);
		general.add(nightMode);
		general.add(openCredits);

		ConfigUI.getInstance().runWhenLoaded(() -> {
			general.getOptions().removeIf(o -> "configStyle".equals(o.getName()));
			String[] themes = ConfigUI.getInstance().getStyleNames().stream().map(s -> "configStyle." + s)
				.filter(s -> AxolotlClientCommon.NVG_SUPPORTED || !s.startsWith("rounded"))
				.toArray(String[]::new);
			if (themes.length > 1) {
				StringArrayOption configStyle;
				general.add(configStyle = new StringArrayOption("configStyle", themes,
					"configStyle." + ConfigUI.getInstance().getCurrentStyle().getName(), s -> {
					ConfigUI.getInstance().setStyle(s.split("\\.")[1]);

					Screen newScreen = RecreatableScreen.tryRecreate(MinecraftClient.getInstance().currentScreen);
					MinecraftClient.getInstance().openScreen(newScreen);
				}));
				AxolotlClient.getInstance().getConfigManager().load();
				ConfigUI.getInstance().setStyle(configStyle.get().split("\\.")[1]);
			} else {
				AxolotlClient.getInstance().getConfigManager().load();
			}
		});

		rendering.add(customSky,
			lowShield,
			hitColor,
			flatItems,
			inventoryPotionEffectOffset);

		outlines.add(outlineChroma);

		general.add(new GenericOption("profiles.title", "profiles.configure", () ->
			MinecraftClient.getInstance().openScreen(new ProfilesScreen(MinecraftClient.getInstance().currentScreen))), false);

		var toggleFullbright = new KeyBinding("toggle_fullbright", -1, "category.axolotlclient");
		KeyBindingHelper.registerKeyBinding(toggleFullbright);
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			if (toggleFullbright.wasPressed()) {
				fullBright.toggle();
			}
		});
	}
}
