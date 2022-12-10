/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.freelook;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.FeatureDisabler;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;

public class Freelook extends AbstractModule {

    private static final Freelook Instance = new Freelook();
    private static final KeyBind KEY = new KeyBind("key.freelook", InputUtil.KEY_V_CODE, "category.axolotlclient");

    private final MinecraftClient client = MinecraftClient.getInstance();

    private float yaw, pitch;
    public boolean active;

    private final OptionCategory category = new OptionCategory("freelook");
    public final BooleanOption enabled = new BooleanOption("enabled", false);

    private final EnumOption mode = new EnumOption("mode",
            value -> FeatureDisabler.update(),
            new String[]{"snap_perspective", "freelook"},
            "freelook");
    private final EnumOption perspective = new EnumOption("perspective", Perspective.values(),
            Perspective.THIRD_PERSON_BACK.toString());
    private final BooleanOption invert = new BooleanOption("invert", false);

    private final BooleanOption toggle = new BooleanOption("toggle", false);

    private Perspective previousPerspective;

    public static Freelook getInstance() {
        return Instance;
    }

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(KEY);
        category.add(enabled, perspective, invert, toggle);
        AxolotlClient.CONFIG.addCategory(category);
    }

    @Override
    public void tick() {
        if (!enabled.get())
            return;

        if (toggle.get()) {
            if (KEY.wasPressed()) {
                if (active) {
                    stop();
                } else {
                    start();
                }
            }
        } else {
            if (KEY.isPressed()) {
                if (!active) {
                    start();
                }
            } else if (active) {
                stop();
            }
        }
    }

    private void start() {
        active = true;

        previousPerspective = client.options.getPerspective();
        setPerspective(Perspective.valueOf(perspective.get()));

        Entity camera = client.getCameraEntity();

        if (camera == null)
            camera = client.player;
        if (camera == null)
            return;

        yaw = camera.getYaw();
        pitch = camera.getPitch();
    }

    private void stop() {
        active = false;
        client.worldRenderer.scheduleTerrainUpdate();
        setPerspective(previousPerspective);
    }

    public boolean consumeRotation(double dx, double dy) {
        if (!active || !enabled.get())
            return false;

        if (!invert.get())
            dy = -dy;

        if (MinecraftClient.getInstance().options.getPerspective().isFrontView()
                || MinecraftClient.getInstance().options.getPerspective().isFirstPerson())
            dy *= -1;

        yaw += dx * 0.15F;
        pitch += dy * 0.15F;

        if (pitch > 90) {
            pitch = 90;
        } else if (pitch < -90) {
            pitch = -90;
        }

        client.worldRenderer.scheduleTerrainUpdate();
        return true;
    }

    public float yaw(float defaultValue) {
        if (!active || !enabled.get())
            return defaultValue;

        return yaw;
    }

    public float pitch(float defaultValue) {
        if (!active || !enabled.get())
            return defaultValue;

        return pitch;
    }

    private void setPerspective(Perspective perspective) {
        MinecraftClient.getInstance().options.setPerspective(perspective);
    }

    public boolean needsDisabling(){
        return mode.get().equals("freelook");
    }
}
