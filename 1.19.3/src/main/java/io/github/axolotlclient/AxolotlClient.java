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

package io.github.axolotlclient;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.DefaultConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIOptions;
import io.github.axolotlclient.api.StatusUpdateProviderImpl;
import io.github.axolotlclient.api.requests.User;
import io.github.axolotlclient.config.AxolotlClientConfig;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.modules.ModuleLoader;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.blur.MenuBlur;
import io.github.axolotlclient.modules.blur.MotionBlur;
import io.github.axolotlclient.modules.freelook.Freelook;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.axolotlclient.modules.particles.Particles;
import io.github.axolotlclient.modules.renderOptions.BeaconBeam;
import io.github.axolotlclient.modules.rpc.DiscordRPC;
import io.github.axolotlclient.modules.screenshotUtils.ScreenshotUtils;
import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.axolotlclient.modules.sky.SkyResourceManager;
import io.github.axolotlclient.modules.tablist.Tablist;
import io.github.axolotlclient.modules.tnttime.TntTime;
import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.*;
import io.github.axolotlclient.util.notifications.Notifications;
import io.github.axolotlclient.util.translation.Translations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AxolotlClient implements ClientModInitializer {

	public static String modid = "AxolotlClient";

	public static AxolotlClientConfig CONFIG;
	public static ConfigManager configManager;
	public static HashMap<UUID, Boolean> playerCache = new HashMap<>();

	public static HashMap<Identifier, Resource> runtimeResources = new HashMap<>();

	public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

	public static final OptionCategory config = new OptionCategory("storedOptions");
	public static final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
	public static final List<Module> modules = new ArrayList<>();

	private static int tickTime = 0;

	public static UnsupportedMod badmod;
	public static boolean titleDisclaimer = false;
	public static boolean showWarning = true;

	public static Logger LOGGER = new LoggerImpl();

	@Override
	public void onInitializeClient(ModContainer container) {
		if (QuiltLoader.isModLoaded("ares")) {
			badmod = new UnsupportedMod("Ares Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
		} else if (QuiltLoader.isModLoaded("inertia")) {
			badmod = new UnsupportedMod("Inertia Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
		} else if (QuiltLoader.isModLoaded("meteor-client")) {
			badmod = new UnsupportedMod("Meteor Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
		} else if (QuiltLoader.isModLoaded("wurst")) {
			badmod = new UnsupportedMod("Wurst Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
		} else if (QuiltLoader.isModLoaded("baritone")) {
			badmod = new UnsupportedMod("Baritone", UnsupportedMod.UnsupportedReason.BAN_REASON);
		} else if (QuiltLoader.isModLoaded("xaerominimap")) {
			badmod = new UnsupportedMod("Xaero's Minimap", UnsupportedMod.UnsupportedReason.UNKNOWN_CONSEQUENSES);
		} else if (QuiltLoader.isModLoaded("essential-container")) {
			badmod = new UnsupportedMod("Essential", UnsupportedMod.UnsupportedReason.MIGHT_CRASH,
					UnsupportedMod.UnsupportedReason.UNKNOWN_CONSEQUENSES);
		} else if (QuiltLoader.isModLoaded("optifabric")) {
			badmod = new UnsupportedMod("OptiFine", UnsupportedMod.UnsupportedReason.MIGHT_CRASH,
					UnsupportedMod.UnsupportedReason.UNKNOWN_CONSEQUENSES);
		} else {
			showWarning = false;
		}

		CONFIG = new AxolotlClientConfig();
		config.add(someNiceBackground);

		getModules();
		addExternalModules();

		CONFIG.init();

		new API(LOGGER, Notifications.getInstance(), Translations.getInstance(), new StatusUpdateProviderImpl(), APIOptions.getInstance());

		modules.forEach(Module::init);

		CONFIG.getConfig().addAll(CONFIG.getCategories());
		CONFIG.getConfig().add(config);

		AxolotlClientConfigManager.getInstance().registerConfig(modid, CONFIG, configManager = new DefaultConfigManager(modid,
				QuiltLoader.getConfigDir().resolve("AxolotlClient.json"), CONFIG.getConfig()));
		AxolotlClientConfigManager.getInstance().addIgnoredName(modid, "x");
		AxolotlClientConfigManager.getInstance().addIgnoredName(modid, "y");

		modules.forEach(Module::lateInit);

		ResourceLoader.registerBuiltinResourcePack(new Identifier("axolotlclient", "axolotlclient-ui"), container,
				ResourcePackActivationType.NORMAL);
		ClientTickEvents.END.register(client -> tickClient());

		FeatureDisabler.init();

		LOGGER.debug("Debug Output activated, Logs will be more verbose!");

		LOGGER.info("AxolotlClient Initialized");
	}

	public static void getModules() {
		modules.add(SkyResourceManager.getInstance());
		modules.add(Zoom.getInstance());
		modules.add(HudManager.getInstance());
		modules.add(HypixelMods.getInstance());
		modules.add(MotionBlur.getInstance());
		modules.add(MenuBlur.getInstance());
		modules.add(ScrollableTooltips.getInstance());
		modules.add(DiscordRPC.getInstance());
		modules.add(Freelook.getInstance());
		modules.add(TntTime.getInstance());
		modules.add(Particles.getInstance());
		modules.add(ScreenshotUtils.getInstance());
		modules.add(BeaconBeam.getInstance());
		modules.add(Tablist.getInstance());
		modules.add(Auth.getInstance());
		modules.add(APIOptions.getInstance());
	}

	private static void addExternalModules() {
		modules.addAll(ModuleLoader.loadExternalModules());
	}

	public static boolean isUsingClient(UUID uuid) {
		assert MinecraftClient.getInstance().player != null;
		if (uuid == MinecraftClient.getInstance().player.getUuid()) {
			return true;
		} else {
			return User.getOnline(API.getInstance().sanitizeUUID(uuid.toString()));
		}
	}

	public static void tickClient() {
		modules.forEach(Module::tick);

		if (tickTime >= 6000) {
			//System.out.println("Cleared Cache of Other Players!");
			if (playerCache.values().size() > 500) {
				playerCache.clear();
			}
			tickTime = 0;
		}
		tickTime++;
	}

	public static void addBadge(Entity entity, MatrixStack matrices) {
		if (entity instanceof PlayerEntity && !entity.isSneaky()) {
			if (AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
				RenderSystem.enableDepthTest();
				RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);

				assert MinecraftClient.getInstance().player != null;
				int x = -(MinecraftClient.getInstance().textRenderer
						.getWidth(
								entity.getUuid() == MinecraftClient.getInstance().player.getUuid()
										? (NickHider.getInstance().hideOwnName.get()
										? NickHider.getInstance().hiddenNameSelf.get()
										: Team.decorateName(entity.getScoreboardTeam(), entity.getName())
										.getString())
										: (NickHider.getInstance().hideOtherNames.get()
										? NickHider.getInstance().hiddenNameOthers.get()
										: Team.decorateName(entity.getScoreboardTeam(), entity.getName())
										.getString()))
						/ 2
						+ (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer
						.getWidth(" " + Formatting.strip(AxolotlClient.CONFIG.badgeText.get())) : 10));

				RenderSystem.setShaderColor(1, 1, 1, 1);

				if (AxolotlClient.CONFIG.customBadge.get()) {
					Text badgeText = Util.formatFromCodes(AxolotlClient.CONFIG.badgeText.get());
					if (AxolotlClient.CONFIG.useShadows.get()) {
						MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, badgeText, x, 0, -1);
					} else {
						MinecraftClient.getInstance().textRenderer.draw(matrices, badgeText, x, 0, -1);
					}
				} else
					DrawableHelper.drawTexture(matrices, x, 0, 0, 0, 8, 8, 8, 8);
			}
		}
	}
}
