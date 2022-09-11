package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FloatOption extends NumericOption<Float> {


    public FloatOption(String name, Float def, Float min, Float max) {
        super(name, def, min, max);
    }

    public FloatOption(String name, ChangedListener onChange, Float def, Float min, Float max) {
        super(name, onChange, def, min, max);
    }

    public FloatOption(String name, String tooltipKeyPrefix, Float def, Float min, Float max) {
        super(name, tooltipKeyPrefix, def, min, max);
    }

    public FloatOption(String name, String tooltipKeyPrefix, ChangedListener onChange, Float def, Float min, Float max) {
        super(name, tooltipKeyPrefix, onChange, def, min, max);
    }

    @Override
    public OptionType getType() {
        return OptionType.FLOAT;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsFloat();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        try {
            if (args.length > 0) {
                set(Float.parseFloat(args[0]));
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }
        } catch (NumberFormatException ignored){
            return new CommandResponse(false, "Please specify the number to set "+getName()+" to!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");

    }

    @Override
    public List<String> getCommandSuggestions() {
        return Collections.singletonList(String.valueOf(def));
    }
}
