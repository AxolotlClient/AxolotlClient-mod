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

package io.github.axolotlclient.config;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigHolder;
import io.github.axolotlclient.AxolotlClientConfig.common.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.options.*;
import io.github.axolotlclient.config.screen.CreditsScreen;
import io.github.axolotlclient.mixin.OverlayTextureAccessor;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class AxolotlClientConfig extends ConfigHolder {

	public final BooleanOption showOwnNametag = new BooleanOption("showOwnNametag", false);
	public final BooleanOption useShadows = new BooleanOption("useShadows", false);
	public final BooleanOption nametagBackground = new BooleanOption("nametagBackground", true);

	public final BooleanOption showBadges = new BooleanOption("showBadges", true);
	public final BooleanOption customBadge = new BooleanOption("customBadge", false);
	public final StringOption badgeText = new StringOption("badgeText", "");

	public final BooleanOption timeChangerEnabled = new BooleanOption("enabled", false);
	public final IntegerOption customTime = new IntegerOption("time", 0, 0, 24000);
	public final BooleanOption customSky = new BooleanOption("customSky", true);
	public final BooleanOption dynamicFOV = new BooleanOption("dynamicFov", true);
	public final BooleanOption fullBright = new BooleanOption("fullBright", false);
	public final BooleanOption removeVignette = new BooleanOption("removeVignette", false);
	public final BooleanOption lowFire = new BooleanOption("lowFire", false);
	public final BooleanOption lowShield = new BooleanOption("lowShield", false);
	public final ColorOption hitColor = new ColorOption("hitColor",
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
		},
		new Color(255, 0, 0, 77));
	public final BooleanOption minimalViewBob = new BooleanOption("minimalViewBob", false);
	public final BooleanOption noHurtCam = new BooleanOption("noHurtCam", false);
	public final BooleanOption flatItems = new BooleanOption("flatItems", false);

	public final ColorOption loadingScreenColor = new ColorOption("loadingBgColor", new Color(239, 50, 61, 255));
	public final BooleanOption nightMode = new BooleanOption("nightMode", false);
	public final BooleanOption customWindowTitle = new BooleanOption("customWindowTitle", true);

	public final BooleanOption enableCustomOutlines = new BooleanOption("enabled", false);
	public final ColorOption outlineColor = new ColorOption("color", Color.parse("#DD000000"));
	public final FloatOption outlineWidth = new FloatOption("outlineWidth", RenderSystem::lineWidth, 1F, 1F, 10F);

	public final BooleanOption noRain = new BooleanOption("noRain", false);

	public final GenericOption openCredits = new GenericOption("Credits", "Open Credits", (mouseX, mouseY) ->
		MinecraftClient.getInstance().setScreen(new CreditsScreen(MinecraftClient.getInstance().currentScreen))
	);
	public final BooleanOption debugLogOutput = new BooleanOption("debugLogOutput", false);
	public final BooleanOption creditsBGM = new BooleanOption("creditsBGM", true);

	public final OptionCategory general = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("general");
	public final OptionCategory nametagOptions = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("nametagOptions");
	public final OptionCategory rendering = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("rendering");
	public final OptionCategory outlines = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("blockOutlines");
	public final OptionCategory timeChanger = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("timeChanger");
	public final OptionCategory searchFilters = new io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory("searchFilters");

	private final List<Option<?>> options = new ArrayList<>();
	private final List<OptionCategory> categories = new ArrayList<>();

	@Getter
	private final List<OptionCategory> config = new ArrayList<>();

	public void add(Option<?> option) {
		options.add(option);
	}

	public void addCategory(OptionCategory cat) {
		categories.add(cat);
	}

	public List<OptionCategory> getCategories() {
		return categories;
	}

	public List<Option<?>> getOptions() {
		return options;
	}


	public void init() {

		categories.add(general);
		categories.add(nametagOptions);
		categories.add(rendering);

		rendering.addSubCategory(outlines);

		categories.forEach(OptionCategory::clearOptions);

		nametagOptions.add(showOwnNametag);
		nametagOptions.add(useShadows);
		nametagOptions.add(nametagBackground);

		nametagOptions.add(showBadges);
		nametagOptions.add(customBadge);
		nametagOptions.add(badgeText);

		general.add(loadingScreenColor);
		general.add(nightMode);
		general.add(AxolotlClientConfigConfig.showQuickToggles);
		general.add(AxolotlClientConfigConfig.showOptionTooltips);
		general.add(AxolotlClientConfigConfig.showCategoryTooltips);
		general.add(customWindowTitle);
		general.add(openCredits);
		general.add(debugLogOutput);

		searchFilters.add(AxolotlClientConfigConfig.searchIgnoreCase,
			AxolotlClientConfigConfig.searchForOptions,
			AxolotlClientConfigConfig.searchSort,
			AxolotlClientConfigConfig.searchSortOrder);
		general.addSubCategory(searchFilters);

		rendering.add(customSky,
			AxolotlClientConfigConfig.chromaSpeed,
			dynamicFOV,
			fullBright,
			removeVignette,
			lowFire,
			lowShield,
			hitColor,
			minimalViewBob,
			flatItems,
			noHurtCam);

		timeChanger.add(timeChangerEnabled);
		timeChanger.add(customTime);
		rendering.addSubCategory(timeChanger);

		outlines.add(enableCustomOutlines);
		outlines.add(outlineColor);
		//outlines.add(outlineWidth); // I could not get this to have an effect.

		rendering.add(noRain);

		AxolotlClient.config.add(creditsBGM);

	}

}
