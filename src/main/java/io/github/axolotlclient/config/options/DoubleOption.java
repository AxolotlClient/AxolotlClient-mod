package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.config.CommandResponse;
import org.jetbrains.annotations.NotNull;

public class DoubleOption extends OptionBase<Double> {

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

    public Double get(){
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
