package io.github.axolotlclient.config.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.util.Util;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OptionCategory implements Tooltippable {

    Identifier Id;
	String name;
    private final List<OptionBase<?>> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategory(String key){
	    this.name =key;

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> Util.registerCommand(buildCommand()));
    }

	public OptionCategory(Identifier Id, String key){
		this(key);
		this.Id=Id;
    }

    public List<OptionBase<?>> getOptions(){return options;}

    public void add(OptionBase<?> option){options.add(option);}

    public void add(OptionBase<?>... options){
        Collections.addAll(this.options, options);
    }

    public void add(Collection<OptionBase<?>> options){this.options.addAll(options);}

    public void addSubCategory(OptionCategory category){subCategories.add(category);}

    public OptionCategory addSubCategories(List<OptionCategory> categories){subCategories.addAll(categories); return this;}

    public List<OptionCategory> getSubCategories(){return subCategories;}

    public void clearOptions(){options.clear();}

    public Identifier getID() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public Text getTranslatedName(){return Text.translatable(name);}

    @Override
    public String toString() {
        try {
            return getTranslatedName().getString();
        } catch (Exception ignored){}
        return getName();
    }

    public LiteralArgumentBuilder<QuiltClientCommandSource> buildCommand(){
        LiteralArgumentBuilder<QuiltClientCommandSource> builder = ClientCommandManager.literal(getName());
        for(OptionBase<?> o:getOptions()){
            o.getCommand(builder);
        }
        builder.executes(context -> {
            StringBuilder string = new StringBuilder();
            for (OptionBase<?> o : getOptions()) {
                string.append("    ").append(Formatting.AQUA).append(o.getName()).append(": ").append(o.get()).append("\n");
            }
            Util.sendChatMessage(Text.literal(Formatting.BLUE + "Values in this category are: \n" + string));
            return Command.SINGLE_SUCCESS;
        });
        return builder;
    }
}
