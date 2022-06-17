package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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

    private Vec3d lastTickPos = new Vec3d(0, 0, 0);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        double dx = player.getPos().x - lastTickPos.x;
        double dy = player.getPos().y - lastTickPos.y;
        double dz = player.getPos().z - lastTickPos.z;
        double speed = Math.sqrt(dx * dx + dy * dy + dz * dz);

        return FORMATTER.format(speed*20) + " m/s";
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if(MinecraftClient.getInstance().player!=null) {
            lastTickPos = MinecraftClient.getInstance().player.getPos();
        }
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
    }

    @Override
    public String getPlaceholder() {
        return "4.67 m/s";
    }
}
