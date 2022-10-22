package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.AxolotlclientConfig.options.StringOption;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RealTimeHud extends CleanHudEntry{

    private final Date date = new Date();
    private final StringOption dateFormat = new StringOption("axolotlclient.format", "HH:mm");
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    private int second = 0;

    public static Identifier ID = new Identifier("realtimehud");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return format.format(date);
    }

    @Override
    public String getPlaceholder() {
        return getValue();
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(dateFormat);
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if(second>=20){
            date.setTime(System.currentTimeMillis());
            second=0;
        } else second++;

        if(!format.toPattern().equals(dateFormat.get()) && dateFormat.get() != null){
            try {
                format = new SimpleDateFormat(dateFormat.get());
            } catch (IllegalArgumentException e){
                dateFormat.set(dateFormat.get().substring(0, dateFormat.get().length()-1));
            }
        }
    }
}
