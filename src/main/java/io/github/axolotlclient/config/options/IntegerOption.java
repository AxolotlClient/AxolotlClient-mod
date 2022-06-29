package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class IntegerOption extends OptionBase {

    private int option;
    private final int Default;
    private final int min;
    private final int max;

    public IntegerOption(String name, int Default, int min, int max) {
        this(name, null, Default, min, max);
    }

    public IntegerOption(String name, String tooltipLocation, int Default, int min, int max) {
        super(name, tooltipLocation);
        this.Default=Default;
        this.min=min;
        this.max=max;
    }

    public int get(){
        return option;
    }

    public void set(int set){
        option=set;
    }

    public int getMin(){return min;}
    public int getMax(){return max;}

    @Override
    public OptionType getType() {
        return OptionType.INT;
    }

    public void setDefaults(){
        option = Default;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsInt();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }
}
