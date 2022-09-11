package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StringOption extends OptionBase<String> {

    public StringOption(String name, String tooltipLocation, String def){
        super(name, tooltipLocation, def);
    }

    public StringOption(String name, String def){
        this(name, null, def);
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        this.option=element.getAsString();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option == null?def:option);
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
