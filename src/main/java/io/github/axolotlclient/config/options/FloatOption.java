package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class FloatOption extends OptionBase {

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

    public float get(){
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
}
