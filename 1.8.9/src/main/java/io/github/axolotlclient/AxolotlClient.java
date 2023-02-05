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

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.DefaultConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
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
import io.github.axolotlclient.modules.unfocusedFpsLimiter.UnfocusedFpsLimiter;
import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.FeatureDisabler;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.LoggerImpl;
import io.github.axolotlclient.util.NetworkHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

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

    public static Logger LOGGER = new LoggerImpl();

    @Override
    public void onInitializeClient() {
        CONFIG = new AxolotlClientConfig();
        config.add(someNiceBackground);

        getModules();
        addExternalModules();
        CONFIG.init();
        modules.forEach(Module::init);

        CONFIG.config.addAll(CONFIG.getCategories());
        CONFIG.config.add(config);

        AxolotlClientConfigManager.getInstance().registerConfig(modid, CONFIG, configManager = new DefaultConfigManager(modid,
                FabricLoader.getInstance().getConfigDir().resolve("AxolotlClient.json"), CONFIG.config));
        AxolotlClientConfigManager.getInstance().addIgnoredName(modid, "x");
        AxolotlClientConfigManager.getInstance().addIgnoredName(modid, "y");

        modules.forEach(Module::lateInit);

        /*FabricLoader.getInstance().getModContainer("axolotlclient").ifPresent(modContainer -> {
            Optional<Path> optional = modContainer.findPath("resourcepacks/AxolotlClientUI.zip");
            optional.ifPresent(path -> MinecraftClient.getInstance().getResourcePackLoader().method_10366(path.toFile()));
        });*/

        ClientTickEvents.END_CLIENT_TICK.register(client -> tickClient());

        FeatureDisabler.init();

        LOGGER.debug("Debug Output enabled, Logs will be quite verbose!");

        LOGGER.info("AxolotlClient Initialized");
    }

    private static void getModules() {
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
        modules.add(UnfocusedFpsLimiter.getInstance());
        modules.add(Tablist.getInstance());
        modules.add(Auth.getInstance());
    }

    private static void addExternalModules() {
        modules.addAll(ModuleLoader.loadExternalModules());
    }

    public static boolean isUsingClient(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        assert MinecraftClient.getInstance().player != null;
        if (uuid == MinecraftClient.getInstance().player.getUuid()) {
            return true;
        } else {
            return NetworkHelper.getOnline(uuid);
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

    public static void addBadge(Entity entity) {
        if (entity instanceof PlayerEntity && !entity.isSneaking()) {
            if (AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableDepthTest();
                GlStateManager.enableAlphaTest();
                MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

                int x = -(MinecraftClient.getInstance().textRenderer
                        .getStringWidth(entity.getUuid() == MinecraftClient.getInstance().player.getUuid()
                                ? (NickHider.getInstance().hideOwnName.get() ? NickHider.getInstance().hiddenNameSelf.get()
                                : entity.getName().asFormattedString())
                                : (NickHider.getInstance().hideOtherNames.get() ? NickHider.getInstance().hiddenNameOthers.get()
                                : entity.getName().asFormattedString()))
                        / 2
                        + (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer
                        .getStringWidth(" " + AxolotlClient.CONFIG.badgeText.get()) : 10));

                GlStateManager.color(1, 1, 1, 1);

                if (AxolotlClient.CONFIG.customBadge.get())
                    MinecraftClient.getInstance().textRenderer.draw(AxolotlClient.CONFIG.badgeText.get(), x, 0, -1,
                            AxolotlClient.CONFIG.useShadows.get());
                else
                    DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);
            }
        }
    }
}
