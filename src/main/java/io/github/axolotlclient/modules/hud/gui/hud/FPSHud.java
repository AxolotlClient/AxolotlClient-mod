package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import net.minecraft.util.Identifier;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class FPSHud extends CleanHudEntry {
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
