package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BooleanOption extends OptionBase<Boolean> {

    private boolean option;
    private final boolean Default;

    public BooleanOption(String name, String tooltipLocation, boolean Default) {
        super(name, tooltipLocation);
        this.Default = Default;
    }

    public BooleanOption(String name, boolean Default) {
        this(name, null, Default);
    }

    public Boolean get(){
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

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){
            String arg = args[0];
            switch (arg) {
                case "toggle":
                    toggle();
                    return new CommandResponse(true, "Successfully toggled " + getName() + "!");
                case "true":
                    set(true);
                    return new CommandResponse(true, "Successfully set " + getName() + " to true!");
                case "false":
                    set(false);
                    return new CommandResponse(true, "Successfully set " + getName() + " to false!");
            }

            return new CommandResponse(false, "Please specify either toggle, true or false!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    public List<String> getCommandSuggestions(){
        return Arrays.asList("true", "false");
    }
}
