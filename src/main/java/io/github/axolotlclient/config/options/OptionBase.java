package io.github.axolotlclient.config.options;

import io.github.axolotlclient.config.CommandResponse;
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
    public @Nullable String getTooltip() {
        if(tooltipKeyPrefix != null)
            return Option.super.getTooltip(tooltipKeyPrefix +"."+ getName());
        else return Option.super.getTooltip();
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
