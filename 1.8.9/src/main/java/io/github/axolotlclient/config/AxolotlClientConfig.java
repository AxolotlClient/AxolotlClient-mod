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

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.screen.ConfigScreen;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.RecreatableScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.ConfigStyles;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.config.screen.CreditsScreen;
import io.github.axolotlclient.config.screen.ProfilesScreen;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.util.WindowAccess;
import io.github.axolotlclient.util.options.ForceableBooleanOption;
import io.github.axolotlclient.util.options.GenericOption;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;

public class AxolotlClientConfig extends AxolotlClientConfigCommon {
	public final BooleanOption customSky = new BooleanOption("customSky", true);
	public final IntegerOption cloudHeight = new IntegerOption("cloudHeight", 128, 100, 512);

	public final ColorOption hitColor = new ColorOption("hitColor", new Color(255, 0, 0, 77));

	public final BooleanOption flatItems = new BooleanOption("flatItems", false);
	public final BooleanOption inventoryPotionEffectOffset = new BooleanOption("inventory.potion_effect_offset", true);

	public final ColorOption loadingScreenColor = new ColorOption("loadingBgColor", new Color(-1));
	public final BooleanOption nightMode = new BooleanOption("nightMode", false);
	public final BooleanOption rawMouseInput = new BooleanOption("rawMouseInput", false, v ->
		WindowAccess.getInstance().setRawMouseMotion(v));

	public final IntegerOption outlineWidth = new IntegerOption("outlineWidth", 1, 1, 10);

	public final GenericOption openCredits = new GenericOption("Credits", "Open Credits",
		() -> Minecraft.getInstance()
			.openScreen(new CreditsScreen(Minecraft.getInstance().screen)));

	public final BooleanOption scaleTitles = new BooleanOption("titles.scaling", false);
	public final IntegerOption titlePadding = new IntegerOption("titles.padding", 4, 1, 10);

	public final OptionCategory titles = OptionCategory.create("titles");

	@Getter
	private final List<Option<?>> options = new ArrayList<>();

	public AxolotlClientConfig() {
		general.add(loadingScreenColor);
		general.add(nightMode);
		general.add(rawMouseInput);
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

					Screen newScreen = RecreatableScreen.tryRecreate(Minecraft.getInstance().screen);
					Minecraft.getInstance().openScreen(newScreen);
				}));
				AxolotlClient.getInstance().getConfigManager().load();
				ConfigUI.getInstance().setStyle(configStyle.get().split("\\.")[1]);
			} else {
				AxolotlClient.getInstance().getConfigManager().load();
			}
		});

		rendering.add(customSky,
			cloudHeight,
			hitColor,
			flatItems,
			inventoryPotionEffectOffset);

		outlines.add(outlineWidth);

		titles.add(scaleTitles, titlePadding);
		rendering.add(titles);

		AxolotlClient.getInstance().modules.add(new Module() {
			@Override
			public void lateInit() {
				if (WindowAccess.getInstance().rawMouseMotionAvailable()) {

					if (System.getProperty("org.lwjgl.input.Mouse.disableRawInput") == null) {
						System.setProperty("org.lwjgl.input.Mouse.disableRawInput", "true");
					}
					WindowAccess.getInstance().setRawMouseMotion(rawMouseInput.get());
				} else {
					AxolotlClient.getInstance().getConfigManager().suppressName(rawMouseInput.getName());
				}
			}
		});

		general.add(new GenericOption("profiles.title", "profiles.configure", () ->
			Minecraft.getInstance().openScreen(new ProfilesScreen(Minecraft.getInstance().screen))), false);

		var toggleFullbright = new KeyBinding("toggle_fullbright", 0, "category.axolotlclient");
		KeyBindingEvents.REGISTER_KEYBINDS.register(reg -> reg.register(toggleFullbright));
		MinecraftClientEvents.TICK_END.register(minecraft -> {
			if (toggleFullbright.consumeClick()) {
				fullBright.toggle();
			}
		});
	}
}
