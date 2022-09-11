package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ColorOption extends OptionBase<Color> {

    private boolean chroma;

    public ColorOption(String name, String def){
        this(name, Color.parse(def));
    }

    public ColorOption(String name, int def){
        this(name, new Color(def));
    }

    public ColorOption(String name, String tooltipLocation, Color def){
        super(name, tooltipLocation, def);
    }

    public ColorOption(String name, Color def){
        this(name, null, def);
    }

    public Color get(){
        return chroma ? Color.getChroma().withAlpha(super.get().getAlpha()) : super.get();
    }

    public void set(Color set, boolean chroma){
        super.set(set);
        this.chroma=chroma;
    }

    public void setChroma(boolean set){
        chroma=set;
    }

    @Override
    public OptionType getType() {
        return OptionType.COLOR;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        try {
            chroma = element.getAsJsonObject().get("chroma").getAsBoolean();
            set(Color.parse(element.getAsJsonObject().get("color").getAsString()));
        } catch (Exception ignored){
        }
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.add("color",new JsonPrimitive(get().toString()));
        object.add("chroma", new JsonPrimitive(chroma));
        return object;
    }

    public boolean getChroma(){
        return chroma;
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
