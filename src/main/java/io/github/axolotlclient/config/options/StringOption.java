package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class StringOption extends OptionBase {

    private String value;
    private final String def;

    public StringOption(String name, String tooltipLocation, String def){
        super(name, tooltipLocation);
        this.def = def;
    }

    public StringOption(String name, String def){
        this(name, null, def);
    }

    public String get(){
        return value;
    }

    public void set(String set){
        value = set;
    }

    @Override
    public OptionType getType() {
        return OptionType.STRING;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        this.value=element.getAsString();
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
