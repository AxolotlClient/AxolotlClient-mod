package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.config.CommandResponse;
import org.jetbrains.annotations.NotNull;

public class FloatOption extends OptionBase<Float> {

    float min;
    float max;
    float def;
    float option;

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
        return option;
    }

    public void set(float set){
        option=set;
    }

    public float getMin(){return min;}
    public float getMax(){return max;}

    @Override
    public OptionType getType() {
        return OptionType.FLOAT;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsFloat();
    }

    @Override
    public void setDefaults() {
        option=def;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
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
