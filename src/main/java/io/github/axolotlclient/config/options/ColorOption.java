package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ColorOption extends OptionBase<Color> {

    private final Color def;
    private Color value;

    public ColorOption(String name, String def){
        this(name, Color.parse(def));
    }

    public ColorOption(String name, int def){
        this(name, new Color(def));
    }

    public ColorOption(String name, String tooltipLocation, Color def){
        super(name, tooltipLocation);
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

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){
            Color newColor = Color.parse(args[0]);
            if(newColor== Color.ERROR){
                return new CommandResponse(false, "Please enter a valid Color in Hex format!");
            } else {
                set(newColor);
                return new CommandResponse(true, "Successfully set "+getName() + " to "+args[0]);
            }

        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public List<String> getCommandSuggestions() {
        return Collections.singletonList("#FFFFFFFF");
    }
}
