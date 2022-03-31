package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class SpeedHud extends CleanHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "speedhud");
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");
    private BooleanOption horizontal = new BooleanOption("horizontal", true);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        float vec = MinecraftClient.getInstance().player.getSpeed();
        double speed;
        /*if (horizontal.get()) {
            speed = vec.horizontalLength();
        } else {
            speed = vec.length();
        }*/
        return FORMATTER.format(vec) + " BPT";
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(horizontal);
    }

    @Override
    public String getPlaceholder() {
        return "0.95 BPT";
    }
}
