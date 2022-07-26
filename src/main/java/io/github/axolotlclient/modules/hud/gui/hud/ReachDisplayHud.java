package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionBase;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;

public class ReachDisplayHud extends CleanHudEntry {

    public static Identifier ID = new Identifier("axolotlclient", "reachdisplayhud");

    public IntegerOption time = new IntegerOption("time", 2, 1, 20);
    public IntegerOption decimalPlaces = new IntegerOption("decimalplaces", 2, 0, 10);

    public ReachDisplayHud(){
        super(100, 13);
    }

    private String currentDist;
    private Instant lastTime = Instant.now();

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return currentDist +" "+ I18n.translate("blocks");
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if(lastTime.getEpochSecond()+time.get()<Instant.now().getEpochSecond()){
            currentDist = "0";
        }
    }

    public void updateDistance(double dist){
        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.get() > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces.get(); i++) {
                format.append("0");
            }
        }
        DecimalFormat df = new DecimalFormat(format.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        currentDist = df.format(dist);
        lastTime = Instant.now();
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(time);
        options.add(decimalPlaces);
    }

    @Override
    public String getPlaceholder() {
        return "25 "+ I18n.translate("blocks");
    }
}
