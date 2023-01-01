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

package io.github.axolotlclient.modules.unfocusedFpsLimiter;

import java.util.concurrent.locks.LockSupport;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.FloatOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundCategory;

/**
 * This module is based on the mod DynamicFps by juliand665.
 * <p>Original License: MIT</p>
 * <a href="https://github.com/juliand665/dynamic-fps">Original Source Github</a>
 */

public class UnfocusedFpsLimiter extends AbstractModule {

    @Getter
    private static final UnfocusedFpsLimiter Instance = new UnfocusedFpsLimiter();

    private final BooleanOption enabled = new BooleanOption("enabled", false);
    private final BooleanOption reduceFPSWhenUnfocused = new BooleanOption("reduceFPS", false);
    private final IntegerOption unfocusedFPS = new IntegerOption("unfocusedFPS", 10, 0, 60);
    private final BooleanOption restoreOnHover = new BooleanOption("restoreOnHover", true);
    private final FloatOption unfocusedVolumeMultiplier = new FloatOption("unfocusedVolumeMultiplier", 0.25f, 0f, 1f);
    private final FloatOption hiddenVolumeMultiplier = new FloatOption("hiddenVolumeMultiplier", 0f, 0f, 1f);
    private final BooleanOption runGCOnUnfocus = new BooleanOption("runGCOnUnfocus", false);

    @Override
    public void init() {
        OptionCategory category = new OptionCategory("fpsLimiter");
        category.add(enabled, reduceFPSWhenUnfocused, unfocusedFPS, restoreOnHover, unfocusedVolumeMultiplier, hiddenVolumeMultiplier, runGCOnUnfocus);
        AxolotlClient.CONFIG.rendering.add(category);
    }

    private boolean isFocused, isVisible, isHovered;
    private long lastRender;

    public boolean checkForRender(){

        if(!enabled.get()){
            return true;
        }

        isFocused = Display.isActive();
        isVisible = Display.isVisible();
        isHovered = Mouse.isInsideWindow();

        checkForStateChanges();

        long currentTime = MinecraftClient.getTime();
        long timeSinceLastRender = currentTime - lastRender;

        if (!checkForRender(timeSinceLastRender)) return false;

        lastRender = currentTime;
        return true;
    }

    private boolean wasFocused = true;
    private boolean wasVisible = true;

    private void checkForStateChanges() {
        if (isFocused != wasFocused) {
            wasFocused = isFocused;
            if (isFocused) {
                onFocus();
            } else {
                onUnfocus();
            }
        }

        if (isVisible != wasVisible) {
            wasVisible = isVisible;
            if (isVisible) {
                onAppear();
            } else {
                onDisappear();
            }
        }
    }

    private void onFocus() {
        setVolumeMultiplier(1);
    }

    private void onUnfocus() {
        if (isVisible) {
            setVolumeMultiplier(unfocusedVolumeMultiplier.get());
        }

        if (runGCOnUnfocus.get()) {
            System.gc();
        }
    }

    private void onAppear() {
        if (!isFocused) {
            setVolumeMultiplier(unfocusedVolumeMultiplier.get());
        }
    }

    private void onDisappear() {
        setVolumeMultiplier(hiddenVolumeMultiplier.get());
    }

    private void setVolumeMultiplier(float multiplier) {
        // setting the volume to 0 stops all sounds (including music), which we want to avoid if possible.
        boolean clientWillPause = !isFocused && client.options.pauseOnLostFocus && client.currentScreen == null;
        // if the client would pause anyway, we don't need to do anything because that will already pause all sounds.
        if (multiplier == 0 && clientWillPause) return;

        float baseVolume = MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().updateSoundVolume(
                SoundCategory.MASTER,
                baseVolume * multiplier
        );
    }

    // we always render one last frame before actually reducing FPS, so the hud text shows up instantly when forcing low fps.
    // additionally, this would enable mods which render differently while mc is inactive.
    private boolean hasRenderedLastFrame = false;
    private boolean checkForRender(long timeSinceLastRender) {
        Integer fpsOverride = fpsOverride();
        if (fpsOverride == null) {
            hasRenderedLastFrame = false;
            return true;
        }

        if (!hasRenderedLastFrame) {
            // render one last frame before reducing, to make sure differences in this state show up instantly.
            hasRenderedLastFrame = true;
            return true;
        }

        if (fpsOverride == 0) {
            idle(1000);
            return false;
        }

        long frameTime = 1000 / fpsOverride;
        boolean shouldSkipRender = timeSinceLastRender < frameTime;
        if (!shouldSkipRender) return true;

        idle(frameTime);
        return false;
    }

    /**
     force minecraft to idle because otherwise we'll be busy checking for render again and again
     */
    private void idle(long waitMillis) {
        // cap at 30 ms before we check again so user doesn't have to wait long after tabbing back in
        waitMillis = Math.min(waitMillis, 30);
        LockSupport.parkNanos("waiting to render", waitMillis * 1_000_000);
    }

    @Nullable
    private Integer fpsOverride() {
        if (!isVisible) return 0;
        if (restoreOnHover.get() && isHovered) return null;
        if (reduceFPSWhenUnfocused.get() && !Display.isActive()) return unfocusedFPS.get();
        return null;
    }
}
