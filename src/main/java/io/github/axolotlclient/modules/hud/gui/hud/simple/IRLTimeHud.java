package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.AxolotlclientConfig.options.StringOption;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import net.minecraft.util.Identifier;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class IRLTimeHud extends SimpleTextHudEntry {
    // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

    public static final Identifier ID = new Identifier("kronhud", "irltimehud");

    private DateTimeFormatter formatter = null;
    private boolean error = false;

    private final StringOption format = new StringOption("dateformat", this::updateDateTimeFormatter, "HH:mm:ss");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void updateDateTimeFormatter(String value) {
        try {
            formatter = DateTimeFormatter.ofPattern(value);
            error = false;
        } catch (Exception e) {
            error = true;
            formatter = null;
        }
    }

    @Override
    public String getValue() {
        if (error) {
            return "Error Compiling!";
        }
        if (formatter == null) {
            updateDateTimeFormatter(format.get());
            return getValue();
        }
        return formatter.format(LocalDateTime.now());
    }

    @Override
    public String getPlaceholder() {
        if (error) {
            return "Error Compiling!";
        }
        if (formatter == null) {
            updateDateTimeFormatter(format.get());
            return getValue();
        }
        return formatter.format(LocalDateTime.of(2020, Month.AUGUST, 22, 14, 28, 32, 1595135));
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(format);
        return options;
    }
}
