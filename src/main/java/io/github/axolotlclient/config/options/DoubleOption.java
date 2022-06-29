package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class DoubleOption extends OptionBase {

    private double option;
    private final double Default;
    private final double min;
    private final double max;

    public DoubleOption(String name, String tooltipLocation, double Default, double min, double max) {
        super(name, tooltipLocation);
        this.Default=Default;
        this.min=min;
        this.max=max;
    }

    public DoubleOption(String name, double Default, double min, double max) {
        this(name, null, Default, min, max);
    }

    public double get(){
        return option;
    }

    public void set(double set){
        option=set;
    }

    public double getMin(){return min;}
    public double getMax(){return max;}

    @Override
    public OptionType getType() {
        return OptionType.DOUBLE;
    }


    public void setDefaults(){
        option = Default;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsDouble();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }
}
