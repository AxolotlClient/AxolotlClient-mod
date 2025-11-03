/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.*;
import io.github.axolotlclient.util.options.ForceableBooleanOption;
import net.fabricmc.loader.api.FabricLoader;

public abstract class AxolotlClientConfigCommon {
	public enum MenuButtonMode {
		DISABLED,
		MODMENU() {
			@Override
			public boolean showButton() {
				return !(FabricLoader.getInstance().isModLoaded("modmenu") && !FabricLoader.getInstance().isModLoaded("axolotlclient-modmenu"));
			}
		},
		ALWAYS() {
			@Override
			public boolean showButton() {
				return true;
			}
		};

		@Override
		public String toString() {
			return "menu_button_mode." + super.toString().toLowerCase(Locale.ROOT);
		}

		public boolean showButton() {
			return false;
		}
	}

	// options

	public final BooleanOption showOwnNametag = new BooleanOption("showOwnNametag", false);
	public final BooleanOption useShadows = new BooleanOption("useShadows", false);
	public final BooleanOption nametagBackground = new BooleanOption("nametagBackground", true);

	public final BooleanOption showBadges = new BooleanOption("showBadges", true);
	public final BooleanOption customBadge = new BooleanOption("customBadge", false);
	public final StringOption badgeText = new StringOption("badgeText", "");

	public final ForceableBooleanOption timeChangerEnabled = new ForceableBooleanOption("enabled", false);
	public final IntegerOption customTime = new IntegerOption("time", 0, 0, 24000);

	public final BooleanOption dynamicFOV = new BooleanOption("dynamicFov", true);
	public final ForceableBooleanOption fullBright = new ForceableBooleanOption("fullBright", false);
	public final BooleanOption removeVignette = new BooleanOption("removeVignette", false);
	public final ForceableBooleanOption lowFire = new ForceableBooleanOption("lowFire", false);

	public final BooleanOption minimalViewBob = new BooleanOption("minimalViewBob", false);
	public final BooleanOption noHurtCam = new BooleanOption("noHurtCam", false);

	public final BooleanOption enableCustomOutlines = new BooleanOption("enabled", false);
	public final ColorOption outlineColor = new ColorOption("color", Color.parse("#DD000000"));

	public final BooleanOption customWindowTitle = new BooleanOption("customWindowTitle", true);

	public final OptionCategory general = OptionCategory.create("general");
	public final OptionCategory nametagOptions = OptionCategory.create("nametagOptions");
	public final OptionCategory rendering = OptionCategory.create("rendering");
	public final OptionCategory outlines = OptionCategory.create("blockOutlines");
	public final OptionCategory timeChanger = OptionCategory.create("timeChanger");

	public final BooleanOption creditsBGM = new BooleanOption("creditsBGM", true);
	public final BooleanOption debugLogOutput = new BooleanOption("debugLogOutput", false);

	public final BooleanOption noRain = new BooleanOption("noRain", false);

	public final OptionCategory config = OptionCategory.create("config");
	public final OptionCategory hidden = OptionCategory.create("storedOptions");
	public final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
	public final StringOption datetimeFormat = new StringOption("datetime_format", "yyyy/MM/dd HH:mm:ss", s -> dateTimeFormatter = DateTimeFormatter.ofPattern(s));
	public final EnumOption<MenuButtonMode> titleScreenOptionButtonMode = new EnumOption<>("title_screen_button_mode", MenuButtonMode.class, MenuButtonMode.MODMENU);
	public final EnumOption<MenuButtonMode> gameMenuScreenOptionButtonMode = new EnumOption<>("game_menu_screen_button_mode", MenuButtonMode.class, MenuButtonMode.MODMENU);

	public DateTimeFormatter dateTimeFormatter;

	public AxolotlClientConfigCommon() {
		config.add(general);
		config.add(nametagOptions);
		config.add(rendering);
		config.add(hidden);

		rendering.add(outlines);

		nametagOptions.add(showOwnNametag);
		nametagOptions.add(useShadows);
		nametagOptions.add(nametagBackground);

		nametagOptions.add(showBadges);
		nametagOptions.add(customBadge);
		nametagOptions.add(badgeText);

		general.add(customWindowTitle);
		general.add(debugLogOutput);

		general.add(datetimeFormat);
		general.add(titleScreenOptionButtonMode);
		general.add(gameMenuScreenOptionButtonMode);

		timeChanger.add(timeChangerEnabled);
		timeChanger.add(customTime);

		outlines.add(enableCustomOutlines);
		outlines.add(outlineColor);

		rendering.add(timeChanger);

		rendering.add(
			dynamicFOV,
			fullBright,
			removeVignette,
			lowFire,
			minimalViewBob,
			noHurtCam,
			noRain
		);

		hidden.add(creditsBGM, someNiceBackground);
	}

	public DateTimeFormatter getDateTimeFormatter() {
		if (dateTimeFormatter == null) {
			dateTimeFormatter = DateTimeFormatter.ofPattern(datetimeFormat.get());
		}

		return dateTimeFormatter;
	}

	public static AxolotlClientConfigCommon instance() {
		return AxolotlClientCommon.getInstance().getConfig();
	}

	public final void add(Option<?> option) {
		config.add(option);
	}

	public final void addCategory(OptionCategory cat) {
		config.add(cat);
	}

	public final OptionCategory getConfig() {
		return config;
	}
}
