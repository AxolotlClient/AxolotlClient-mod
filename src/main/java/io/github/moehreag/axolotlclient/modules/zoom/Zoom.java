package io.github.moehreag.axolotlclient.modules.zoom;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;


//Based on https://github.com/LogicalGeekBoy/logical_zoom/blob/master/src/main/java/com/logicalgeekboy/logical_zoom/LogicalZoom.java
public class Zoom extends AbstractModule {

    public static boolean zoomed;
    private static float fadeFactor;
    private static float originalSensitivity;
    private static KeyBinding keyBinding;

    public void init(){
        keyBinding = new KeyBinding("key.zoom", 46, "category.axolotlclient");

        KeyBindingHelper.registerKeyBinding(keyBinding);
        zoomed = false;
        fadeFactor=1F;
    }

    public static boolean isZoomed(){
        return keyBinding.isPressed();
    }

    public static float getFov(float current){
        decreaseFov(current);
        return current / fadeFactor;
    }

    public static void decreaseSensitivity(){
        originalSensitivity=MinecraftClient.getInstance().options.sensitivity;
        MinecraftClient.getInstance().options.sensitivity /= Axolotlclient.CONFIG.zoomDivisor.get();
    }

    public static void restoreSensitivity(){
        MinecraftClient.getInstance().options.sensitivity=originalSensitivity;
    }

    public static void manageZoom() {
        if (zoomStarting()) {
            zoomStarted();
        }

        if (zoomStopping()) {
            zoomStopped();
        }
    }


    private static boolean zoomStarting() {
        return isZoomed() && !zoomed;
    }

    private static boolean zoomStopping() {
        return !isZoomed() && zoomed;
    }

    private static void zoomStarted() {
        zoomed = true;
        if(Axolotlclient.CONFIG.decreaseSensitivity.get())decreaseSensitivity();
    }

    private static void zoomStopped() {
        zoomed = false;
        if(Axolotlclient.CONFIG.decreaseSensitivity.get())restoreSensitivity();
    }

    public static boolean isFadingOut(){
        return fadeFactor>1F;
    }

    public static void decreaseFov(float current){
        if(isZoomed()){
            if(fadeFactor <Axolotlclient.CONFIG.zoomDivisor.get()) fadeFactor+= (fadeFactor/500F) *(Axolotlclient.CONFIG.zoomDivisor.get()/4);
        } else {
            if(fadeFactor > 1F) fadeFactor-=(fadeFactor/500F) *(Axolotlclient.CONFIG.zoomDivisor.get()/4);
            else fadeFactor = 1F;
        }
    }
}
