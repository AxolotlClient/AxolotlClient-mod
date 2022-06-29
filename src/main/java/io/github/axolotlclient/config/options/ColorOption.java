package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.config.Color;
import org.jetbrains.annotations.NotNull;

public class ColorOption extends OptionBase {

    private final Color def;
    private Color value;

    public ColorOption(String name, String def){
        this(name, Color.parse(def));
    }

    public ColorOption(String name, String tooltipLocation, Color def){
        super(name);
        this.def=def;
    }

    public ColorOption(String name, Color def){
        this(name, null, def);
    }

    public Color get(){
        return value;
    }
    public void set(Color set){this.value=set;}

    @Override
    public OptionType getType() {
        return OptionType.COLOR;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        value=Color.parse(element.getAsString());
    }

    @Override
    public void setDefaults() {
        value=def;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(String.valueOf(value));
    }

    public Color getChroma(){
        return Color.getChroma();
    }
}
