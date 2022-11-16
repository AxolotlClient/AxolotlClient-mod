package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class SpeedHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "speedhud");
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");
    private final BooleanOption horizontal = new BooleanOption("axolotlclient.horizontal", true);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if (client.player == null) {
            return getPlaceholder();
        }
        Entity entity = client.player.vehicle == null ? client.player : client.player.vehicle;
        Vec3d vec = new Vec3d(entity.velocityX, entity.velocityY, entity.velocityZ);
        if (entity.onGround && vec.y < 0) {
            vec = new Vec3d(vec.x, 0, vec.z);
        }
        double speed;
        if (horizontal.get()) {
            speed = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        } else {
            speed = vec.length();
        }
        return FORMATTER.format(speed * 20) + " BPS";
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(horizontal);
        return options;
    }

    @Override
    public String getPlaceholder() {
        return "4.35 BPS";
    }
}
