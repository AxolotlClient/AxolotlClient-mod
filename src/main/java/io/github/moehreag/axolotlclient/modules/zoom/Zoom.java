package io.github.moehreag.axolotlclient.modules.zoom;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.util.Identifier;


//Based on https://github.com/LogicalGeekBoy/logical_zoom/blob/master/src/main/java/com/logicalgeekboy/logical_zoom/LogicalZoom.java
public class Zoom extends AbstractModule {

    public static boolean zoomed;
    private static float fadeFactor;
    private static Double originalSensitivity;
    private static KeyBind keyBinding;

    public static Identifier ID = new Identifier("zoom");

    public void init(){
        keyBinding = new KeyBind("key.zoom", InputUtil.KEY_C_CODE, "category.axolotlclient");

        KeyBindingHelper.registerKeyBinding(keyBinding);
        zoomed = false;
        fadeFactor=1F;
    }

    public static boolean isZoomed(){
        return keyBinding.isPressed();
    }

    public static double getFov(Double current){
        decreaseFov();
        return current / fadeFactor;
    }

    public static void decreaseSensitivity(){
        originalSensitivity=MinecraftClient.getInstance().options.getMouseSensitivity().get();
        MinecraftClient.getInstance().options.getMouseSensitivity().set(MinecraftClient.getInstance().options.getMouseSensitivity().get() / AxolotlClient.CONFIG.zoomDivisor.get());
    }

    public static void restoreSensitivity(){
        MinecraftClient.getInstance().options.getMouseSensitivity().set(originalSensitivity);
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
        if(AxolotlClient.CONFIG.decreaseSensitivity.get())decreaseSensitivity();
    }

    private static void zoomStopped() {
        zoomed = false;
        if(AxolotlClient.CONFIG.decreaseSensitivity.get())restoreSensitivity();
    }

    public static boolean isFadingOut(){
        return fadeFactor>1F;
    }

    public static void decreaseFov(){
        if(isZoomed()){
            if(fadeFactor < AxolotlClient.CONFIG.zoomDivisor.get())
                fadeFactor+= (fadeFactor* AxolotlClient.CONFIG.zoomSpeed.get())/50 * (AxolotlClient.CONFIG.zoomDivisor.get()/4);
        } else {
            if(fadeFactor > 1F)
                fadeFactor-= (fadeFactor* AxolotlClient.CONFIG.zoomSpeed.get())/50 *(AxolotlClient.CONFIG.zoomDivisor.get()/4);
            else fadeFactor = 1F;
        }
    }
}
