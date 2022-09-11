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

    private boolean forceOff = false;
    private DisableReason disableReason;

    public BooleanOption(String name, Boolean def) {
        super(name, def);
    }

    public BooleanOption(String name, ChangedListener onChange, Boolean def) {
        super(name, onChange, def);
    }

    public BooleanOption(String name, String tooltipKeyPrefix, Boolean def) {
        super(name, tooltipKeyPrefix, def);
    }

    public BooleanOption(String name, String tooltipKeyPrefix, ChangedListener onChange, Boolean def) {
        super(name, tooltipKeyPrefix, onChange, def);
    }


    public Boolean get(){
        if(getForceDisabled()) return false;
        return super.get();
    }

    public void set(Boolean set){
        if(!getForceDisabled()) {
            super.set(set);
        }
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

    public void toggle(){
        if(!getForceDisabled()) {
            set(!get());
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
