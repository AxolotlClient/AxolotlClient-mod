package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FloatOption extends OptionBase<Float> {

    float min;
    float max;
    float def;
    float value;

    public FloatOption(String name, String tooltipLocation, Float def, Float min, Float max) {
        super(name, tooltipLocation);
        this.min=min;
        this.max=max;
        this.def=def;
    }

    public FloatOption(String name, Float min, Float max, Float def) {
        this(name, null, def, min, max);
    }

    public Float get(){
        return value;
    }

    public void set(float set){
        value=set;
    }

    public float getMin(){return min;}
    public float getMax(){return max;}

    @Override
    public OptionType getType() {
        return OptionType.FLOAT;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        value = element.getAsFloat();
    }

    @Override
    public void setDefaults() {
        value=def;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(value);
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
