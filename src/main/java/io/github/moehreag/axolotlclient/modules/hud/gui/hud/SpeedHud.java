package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_321;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class SpeedHud extends CleanHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "speedhud");
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        Vec3d vec = class_321.method_9372(MinecraftClient.getInstance().player, 0.2);
        double speed = vec.length();
        return FORMATTER.format(speed) + " BPT";
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
    }

    @Override
    public String getPlaceholder() {
        return "0.95 BPT";
    }
}
