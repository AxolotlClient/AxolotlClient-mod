package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class CrosshairHudOption extends OptionBase implements EnumOption{

    private CrosshairOption value;

    public CrosshairHudOption(String name) {
        super(name);
        value=CrosshairOption.CROSS;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        switch (element.getAsString()){
            case ("CROSS")      :this.value=CrosshairOption.CROSS;break;
            case ("DOT")        :this.value=CrosshairOption.DOT;break;
            case ("TEXTURE")    :this.value=CrosshairOption.TEXTURE;break;
        }
    }

    @Override
    public void setDefaults() {
        value=CrosshairOption.CROSS;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(value.toString());
    }

    @Override
    public Enum<?> get() {
        return value;
    }

    @Override
    public Enum<?> next() {
        switch (value){
            case CROSS:value=CrosshairOption.DOT;break;
            case DOT: value=CrosshairOption.TEXTURE;break;
            case TEXTURE:value=CrosshairOption.CROSS;break;
        }

        return value;
    }

    public enum CrosshairOption{
        CROSS,
        DOT,
        TEXTURE
    }
}
