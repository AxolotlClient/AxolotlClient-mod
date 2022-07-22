package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BooleanOption extends OptionBase<Boolean> {

    private boolean option;
    private final boolean Default;
    private boolean forceOff = false;
    private DisableReason disableReason;

    public BooleanOption(String name, String tooltipLocation, boolean Default) {
        super(name, tooltipLocation);
        this.Default = Default;
    }

    public BooleanOption(String name, boolean Default) {
        this(name, null, Default);
    }

    public Boolean get(){
        if(getForceDisabled()) return false;
        return option;
    }

    public void set(boolean set){
        if(!getForceDisabled()) {
            option = set;
        }
    }

    @Override
    public OptionType getType() {
        return OptionType.BOOLEAN;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        if(!getForceDisabled()) {
            option = element.getAsBoolean();
        }
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }

    public void setDefaults(){
        option=Default;
    }

    public void toggle(){
        if(!getForceDisabled()) {
            this.option = !option;
        }
    }

    public boolean getForceDisabled(){
        return forceOff;
    }

    public void setForceOff(boolean forceOff, DisableReason reason){
        this.forceOff=forceOff;
        disableReason=reason;
    }

    @Override
    public @Nullable String getTooltip(String location) {
        if(getForceDisabled()){
            return super.getTooltip("disableReason."+disableReason.toString().toLowerCase(Locale.ROOT));
        }
        return super.getTooltip(location);
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){

            String arg = args[0];
            if(forceOff){
                return new CommandResponse(false, "You cannot use this option since it's force disabled.");
            }

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
