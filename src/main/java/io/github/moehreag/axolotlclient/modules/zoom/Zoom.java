package io.github.moehreag.axolotlclient.modules.zoom;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.config.AxolotlClientConfig;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import io.github.moehreag.axolotlclient.util.Util;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Identifier;

//Based on https://github.com/LogicalGeekBoy/logical_zoom/blob/master/src/main/java/com/logicalgeekboy/logical_zoom/LogicalZoom.java
public class Zoom extends AbstractModule {

    public static boolean active;
    private static float originalSensitivity;
    private static boolean originalSmoothCamera;
    private static KeyBinding keyBinding;
    private static float targetFactor = 1;
    private static float divisor;
    private static float lastAnimatedFactor = 1;
    private static float animatedFactor = 1;
    private static float lastReturnedFov;

    public static final Identifier ID = new Identifier("zoom");

    @Override
    public void init() {
        keyBinding = new KeyBinding("key.zoom", 46, "category.axolotlclient");
        KeyBindingHelper.registerKeyBinding(keyBinding);
        active = false;
    }

    private static boolean keyHeld() {
        return keyBinding.isPressed();
    }

    public static float getFov(float current, float tickDelta) {
        float result = current * (AxolotlClient.CONFIG.zoomSpeed.get() == 10 ? targetFactor
                : Util.lerp(lastAnimatedFactor, animatedFactor, tickDelta));

        if (lastReturnedFov != 0 && lastReturnedFov != result) {
            MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
        }
        lastReturnedFov = result;

        return result;
    }

    public static void setOptions() {
        originalSensitivity = MinecraftClient.getInstance().options.sensitivity;

        if (AxolotlClient.CONFIG.smoothCamera.get()) {
            originalSmoothCamera = MinecraftClient.getInstance().options.smoothCameraEnabled;
            MinecraftClient.getInstance().options.smoothCameraEnabled = true;
        }

        updateSensitivity();
    }

    private static void updateSensitivity() {
        if (AxolotlClient.CONFIG.decreaseSensitivity.get()) {
            MinecraftClient.getInstance().options.sensitivity = originalSensitivity / divisor;
        }
    }

    public static void restoreOptions() {
        MinecraftClient.getInstance().options.sensitivity = originalSensitivity;
        MinecraftClient.getInstance().options.smoothCameraEnabled = originalSmoothCamera;
    }

    public static void update() {
        if(shouldStart()) {
            start();
        } else if(shouldStop()) {
            stop();
        }
    }

    public static boolean scroll(int amount) {
        if (active && AxolotlClient.CONFIG.zoomScrolling.get() && amount != 0) {
            setDivisor(Math.max(1, divisor + (amount / Math.abs(amount))));
            updateSensitivity();
            return true;
        }

        return false;
    }

    public static void tick() {
        lastAnimatedFactor = animatedFactor;
        animatedFactor += (targetFactor - animatedFactor) * (AxolotlClient.CONFIG.zoomSpeed.get() / 10F);
    }

    private static boolean shouldStart() {
        return keyHeld() && !active;
    }

    private static boolean shouldStop() {
        return !keyHeld() && active;
    }

    private static void setDivisor(float value) {
        divisor = value;
        targetFactor = 1F / value;
    }

    private static void start() {
        active = true;
        setDivisor(AxolotlClient.CONFIG.zoomDivisor.get());
        setOptions();
    }

    private static void stop() {
        active = false;
        targetFactor = 1;
        restoreOptions();
    }

}
