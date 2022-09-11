package io.github.axolotlclient.config.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.config.CommandResponse;
import io.github.axolotlclient.util.Util;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

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
            return getTranslatedName().getString();
        } catch (Exception ignored){}
        return getName();
    }

    public int onCommandExec(String arg){
        CommandResponse response = onCommandExecution(arg);
        Util.sendChatMessage(Text.literal(response.response).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(response.success?Formatting.GREEN:Formatting.RED))));
        return Command.SINGLE_SUCCESS;
    }

    protected abstract CommandResponse onCommandExecution(String arg);

    public void getCommand(LiteralArgumentBuilder<QuiltClientCommandSource> builder){
        builder.then(ClientCommandManager.literal(getName())
            .then(ClientCommandManager.argument("value",
            StringArgumentType.greedyString()).executes(ctx -> onCommandExec(StringArgumentType.getString(ctx, "value"))))
            .executes(ctx -> onCommandExec(""))
        );
    }

    public interface ChangedListener {
        void onChanged();
    }
}
