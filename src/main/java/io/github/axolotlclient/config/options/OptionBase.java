package io.github.axolotlclient.config.options;

import io.github.axolotlclient.util.clientCommands.CommandResponse;
import io.github.axolotlclient.util.Util;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class OptionBase<T> implements Option {

    protected T option;
    protected final T def;
    public String name;
    public String tooltipKeyPrefix;
    protected final ChangedListener changeCallback;

    public OptionBase(String name, T def){
        this(name, null, ()->{}, def);
    }

    public OptionBase(String name, ChangedListener onChange, T def){
        this(name, null, onChange, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, T def){
        this(name, tooltipKeyPrefix, ()->{}, def);
    }

    public OptionBase(String name, String tooltipKeyPrefix, ChangedListener onChange, T def){
        this.name=name;
        this.def = def;
        changeCallback = onChange;
        this.tooltipKeyPrefix = tooltipKeyPrefix;
    }

    public T get(){
        return option;
    }

    public void set(T value){
        option = value;
        changeCallback.onChanged();
    }

    public T getDefault(){
        return def;
    }

    public void setDefaults(){
        set(getDefault());
    }

    @Override
    public @Nullable String getTooltipLocation() {
        if(tooltipKeyPrefix != null)
            return tooltipKeyPrefix +"."+ name;
        else return name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        try {
            return getTranslatedName();
        } catch (Exception ignored){}
        return getName();
    }

    public void onCommandExec(String[] args){
        CommandResponse response = onCommandExecution(args);
        Util.sendChatMessage( new LiteralText((response.success ? Formatting.GREEN : Formatting.RED) + response.response));
    }

    protected abstract CommandResponse onCommandExecution(String[] args);

    public abstract List<String> getCommandSuggestions();

    public interface ChangedListener {
        void onChanged();
    }
}
