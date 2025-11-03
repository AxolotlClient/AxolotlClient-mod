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

package io.github.axolotlclient;

import java.util.HashMap;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIOptions;
import io.github.axolotlclient.api.StatusUpdateProviderImpl;
import io.github.axolotlclient.bridge.impl.Bridge;
import io.github.axolotlclient.config.AxolotlClientConfig;
import io.github.axolotlclient.modules.ModuleLoader;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.blur.MenuBlur;
import io.github.axolotlclient.modules.blur.MotionBlur;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.modules.particles.Particles;
import io.github.axolotlclient.modules.screenshotUtils.ScreenshotUtils;
import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.axolotlclient.modules.sky.SkyResourceManager;
import io.github.axolotlclient.modules.tablist.Tablist;
import io.github.axolotlclient.modules.unfocusedFpsLimiter.UnfocusedFpsLimiter;
import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.FeatureDisabler;
import io.github.axolotlclient.util.FeatureDisablerCommon;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.LoggerImpl;
import io.github.axolotlclient.util.notifications.Notifications;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.resource.Resource;
import net.minecraft.resource.Identifier;

public class AxolotlClient extends AxolotlClientCommon implements ClientModInitializer {

	public static final HashMap<Identifier, Resource> runtimeResources = new HashMap<>();
	public static final Identifier badgeIcon = new Identifier(MODID, "textures/badge.png");
	public static final Logger LOGGER = new LoggerImpl();

	private void addBuiltinModules() {
		registerModule(SkyResourceManager.getInstance());
		registerModule(Zoom.getInstance());
		registerModule(HudManager.getInstance());
		registerModule(HypixelMods.getInstance());
		registerModule(MotionBlur.getInstance());
		registerModule(MenuBlur.getInstance());
		registerModule(ScrollableTooltips.getInstance());

		registerModule(Particles.getInstance());
		registerModule(ScreenshotUtils.getInstance());
		registerModule(UnfocusedFpsLimiter.getInstance());
		registerModule(Tablist.getInstance());
		registerModule(Auth.getInstance());
		registerModule(APIOptions.getInstance());
	}

	private void addExternalModules() {
		ModuleLoader.loadExternalModules().forEach(this::registerModule);
	}

	@Override
	public void onInitializeClient() {
		Bridge.init();

		addBuiltinModules();
		addExternalModules();

		init(LOGGER, Notifications.getInstance());
		new API(new StatusUpdateProviderImpl(), APIOptions.getInstance());

		LOGGER.debug("Debug Output enabled, Logs will be quite verbose!");
		LOGGER.info("AxolotlClient Initialized");

		Bridge.postInit();
	}

	@Override
	protected FeatureDisablerCommon getFeatureDisabler() {
		return FeatureDisabler.getInstance();
	}

	@Override
	protected AxolotlClientConfigCommon createConfig() {
		return new AxolotlClientConfig();
	}

	public static AxolotlClientConfig config() {
		return (AxolotlClientConfig) getInstance().getConfig();
	}
}
