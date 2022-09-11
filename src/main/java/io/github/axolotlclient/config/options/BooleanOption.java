package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.config.CommandResponse;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class BooleanOption extends OptionBase<Boolean> {

    private boolean forceOff = false;
    private DisableReason disableReason;

    public BooleanOption(String name, String tooltipLocation, boolean Default) {
        super(name, tooltipLocation, Default);
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
        set(!get());
    }

    public boolean getForceDisabled(){
        return forceOff;
    }

    public void setForceOff(boolean forceOff, DisableReason reason){
        this.forceOff=forceOff;
        disableReason=reason;
    }

    @Override
    public @Nullable Text getTooltip(String location) {
        if(getForceDisabled()){
            return super.getTooltip("disableReason."+disableReason.toString().toLowerCase(Locale.ROOT));
        }
        return super.getTooltip(location);
    }

    @Override
    protected CommandResponse onCommandExecution(String arg) {
        if(arg.length()>0){
            switch (arg) {
                case "toggle" -> {
                    toggle();
                    return new CommandResponse(true, "Successfully toggled " + getName() + "!");
                }
                case "true" -> {
                    set(true);
                    return new CommandResponse(true, "Successfully set " + getName() + " to true!");
                }
                case "false" -> {
                    set(false);
                    return new CommandResponse(true, "Successfully set " + getName() + " to false!");
                }
            }

            return new CommandResponse(false, "Please specify either toggle, true or false!");
        }

        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

}
