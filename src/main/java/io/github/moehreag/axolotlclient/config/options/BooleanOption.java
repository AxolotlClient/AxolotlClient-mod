package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class BooleanOption extends OptionBase implements Option{

    private boolean option;
    private final boolean Default;

    public BooleanOption(String name, boolean Default) {
        super(name);
        this.Default = Default;
    }

    public boolean get(){
        return option;
    }

    public void set(boolean set){option = set;}

    @Override
    public OptionType getType() {
        return OptionType.BOOLEAN;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsBoolean();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }

    public void setDefaults(){
        option=Default;
    }

    public void toggle(){
        this.option=!option;
    }
}
