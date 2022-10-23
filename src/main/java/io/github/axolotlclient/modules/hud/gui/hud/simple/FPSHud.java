package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import net.minecraft.util.Identifier;

public class FPSHud extends SimpleTextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "fpshud");

    public FPSHud() {
        super();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return MinecraftClientAccessor.getCurrentFps() + " FPS";
    }

    @Override
    public String getPlaceholder() {
        return "60 FPS";
    }

}
