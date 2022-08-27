package io.github.axolotlclient.config.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.config.CommandResponse;
import io.github.axolotlclient.util.Util;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public abstract class OptionBase<T> implements Option {

    public String name;
    public String tooltipKeyPrefix;

    public OptionBase(String name){
	    this.name=name;
    }

    public OptionBase(String name, String tooltipKeyPrefix){
        this.name = name;
        this.tooltipKeyPrefix = tooltipKeyPrefix;
    }

    public abstract T get();

    @Override
    public @Nullable Text getTooltip() {
        if(tooltipKeyPrefix != null)
            return Option.super.getTooltip(tooltipKeyPrefix +"."+ getName());
        else return Option.super.getTooltip();
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
        Util.sendChatMessage(new LiteralText(response.response).setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(response.success?Formatting.GREEN:Formatting.RED))));
        return Command.SINGLE_SUCCESS;
    }

    protected abstract CommandResponse onCommandExecution(String arg);

    public void getCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder){
        builder.then(ClientCommandManager.literal(getName())
            .then(ClientCommandManager.argument("value",
            StringArgumentType.greedyString()).executes(ctx -> onCommandExec(StringArgumentType.getString(ctx, "value"))))
            .executes(ctx -> onCommandExec(""))
        );
    }
}
