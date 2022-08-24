package io.github.axolotlclient.config.options;

import io.github.axolotlclient.util.clientCommands.CommandResponse;
import io.github.axolotlclient.util.Util;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class OptionBase<T> implements Option {

    public String name;
    public String tooltipKeyPrefix;

    public OptionBase(String name){
        this.name=name;

    }

    public OptionBase(String name, String tooltipKeyPrefix){
        this(name);
        this.tooltipKeyPrefix = tooltipKeyPrefix;
    }

    public abstract T get();

    @Override
    public @Nullable String getTooltipLocation() {
        if(tooltipKeyPrefix != null)
            return tooltipKeyPrefix +"."+ name;
        else return name;
    }

    public String getName(){
        return name;
    }

    public void onCommandExec(String[] args){
        CommandResponse response = onCommandExecution(args);
        Util.sendChatMessage( new LiteralText((response.success ? Formatting.GREEN : Formatting.RED) + response.response));
    }

    protected abstract CommandResponse onCommandExecution(String[] args);

    public abstract List<String> getCommandSuggestions();
}
