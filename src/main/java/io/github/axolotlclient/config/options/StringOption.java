package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StringOption extends OptionBase<String> {

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

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){
            StringBuilder v = new StringBuilder();
            for(String s:args){
                v.append(s);
                v.append(" ");
            }
            set(v.toString());
            return new CommandResponse(true, "Successfully set "+getName()+" to "+v+"!");
        }
        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public List<String> getCommandSuggestions() {
        return Collections.singletonList(String.valueOf(def));
    }
}
