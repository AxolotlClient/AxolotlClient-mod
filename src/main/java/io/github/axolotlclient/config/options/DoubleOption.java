package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import io.github.axolotlclient.config.CommandResponse;
import org.jetbrains.annotations.NotNull;

public class DoubleOption extends NumericOption<Double> {

    public DoubleOption(String name, String tooltipLocation, double Default, double min, double max) {
        super(name, tooltipLocation, Default, min, max);
    }

    public DoubleOption(String name, ChangedListener<Double> onChange, double Default, double min, double max) {
        super(name, onChange, Default, min, max);
    }

    public DoubleOption(String name, double Default, double min, double max) {
        this(name, (String) null, Default, min, max);
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsDouble();
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {
        try {
            if (arg.length() > 0) {
                set(Double.parseDouble(arg));
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }
        } catch (NumberFormatException ignored){
            return new CommandResponse(false, "Please specify the number to set "+getName()+" to!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");

    }
}
