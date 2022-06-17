package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.Option;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RealTimeHud extends CleanHudEntry{

    private Date date = new Date();
    private final SimpleDateFormat dateFormatSeconds = new SimpleDateFormat("kk:mm:ss");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm");

    private int second = 0;

    protected BooleanOption showSeconds = new BooleanOption("showSeconds", false);

    public static Identifier ID = new Identifier("realtimehud");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return showSeconds.get()?dateFormatSeconds.format(date):dateFormat.format(date);
    }

    @Override
    public String getPlaceholder() {
        return getValue();
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(showSeconds);
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if(second>=20){
            date = new Date();
            second=0;
        } else second++;
    }
}
