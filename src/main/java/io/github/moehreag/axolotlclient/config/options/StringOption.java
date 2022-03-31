package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringOption extends OptionBase implements Option{

    private String value;
    private String def;

    public StringOption(String name, String def){
        super(name);
        this.def = def;
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
    public String getName() {
        return this.name;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
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
