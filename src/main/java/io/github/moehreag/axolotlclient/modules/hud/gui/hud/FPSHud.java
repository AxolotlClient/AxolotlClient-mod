package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class FPSHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "fpshud");

    public FPSHud() {
        //super(x, y, scale);
        super();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return MinecraftClient.getCurrentFps() + " FPS";
    }

    @Override
    public String getPlaceholder() {
        return "60 FPS";
    }

}
