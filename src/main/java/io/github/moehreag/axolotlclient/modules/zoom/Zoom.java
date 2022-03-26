package io.github.moehreag.axolotlclient.modules.zoom;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;


//Based on https://github.com/LogicalGeekBoy/logical_zoom/blob/master/src/main/java/com/logicalgeekboy/logical_zoom/LogicalZoom.java
public class Zoom {

    public static boolean zoomed;
    //private static boolean smoothCameraEnabled;
    private static float fadeFactor;
    private static final float step=0.01F;
    private static float lastFadeFactor;
    private static KeyBinding keyBinding;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void init(){
        keyBinding = new KeyBinding("zoomKey", 46, "category.axolotlclient");

        KeyBindingHelper.registerKeyBinding(keyBinding);
        zoomed = false;
        fadeFactor=1F;
        //smoothCameraEnabled = false;
    }

    public static boolean isZoomed(){
        return keyBinding.isPressed();
    }

    public static float getFov(float current, float delta){
        //startFade(Axolotlclient.CONFIG.General.zoomDivisor);
        decreaseFov();
        return current / fadeFactor;
    }

    public static void manageZoom() {
        if (zoomStarting()) {
            zoomStarted();
            //enableSmoothCamera();
        }

        if (zoomStopping()) {
            zoomStopped();
            //resetSmoothCamera();
        }
    }


    private static boolean zoomStarting() {
        return isZoomed() && !zoomed;
    }

    private static boolean zoomStopping() {
        return !isZoomed() && zoomed;
    }

    private static void zoomStarted() {
        //smoothCameraEnabled = isSmoothCamera();
        zoomed = true;
    }

    private static void zoomStopped() {
        zoomed = false;
    }

    /*private static void resetSmoothCamera() {
        if (smoothCameraEnabled) {
            enableSmoothCamera();
        } else {
            disableSmoothCamera();
        }
    }*/

    public static boolean isFadingOut(){
        return fadeFactor>1F;
    }

    public static void decreaseFov(){
        if(isZoomed()){
            if(fadeFactor <Axolotlclient.CONFIG.General.zoomDivisor) fadeFactor+=step;
        } else {
            if(fadeFactor > 1F) fadeFactor-=step;
            else fadeFactor = 1F;
        }
    }
}
