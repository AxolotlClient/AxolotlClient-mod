package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import io.github.axolotlclient.config.CommandResponse;
import org.jetbrains.annotations.NotNull;

public class FloatOption extends NumericOption<Float> {

    public FloatOption(String name, Float def, Float min, Float max) {
        super(name, def, min, max);
    }

    public FloatOption(String name, ChangedListener<Float> onChange, Float def, Float min, Float max) {
        super(name, onChange, def, min, max);
    }

    public FloatOption(String name, String tooltipKeyPrefix, Float def, Float min, Float max) {
        super(name, tooltipKeyPrefix, def, min, max);
    }

    public FloatOption(String name, String tooltipKeyPrefix, ChangedListener<Float> onChange, Float def, Float min, Float max) {
        super(name, tooltipKeyPrefix, onChange, def, min, max);
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsFloat();
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {
        try {
            if (arg.length() > 0) {
                set(Float.parseFloat(arg));
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }
        } catch (NumberFormatException ignored){
            return new CommandResponse(false, "Please specify the number to set "+getName()+" to!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");

    }

}
