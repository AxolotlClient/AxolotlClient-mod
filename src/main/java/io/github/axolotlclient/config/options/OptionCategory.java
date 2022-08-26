package io.github.axolotlclient.config.options;

import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class OptionCategory implements Tooltippable {

    Identifier Id;
    String name;
    private final List<OptionBase<?>> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategory(String key){
        this.name=key;
        Util.registerCommand(name.toLowerCase(Locale.ENGLISH), this::getCommandSuggestions, this::onCommandExec);
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

    public String getTranslatedName(){return I18n.translate(name);}

    @Override
    public String toString() {
        try {
            return getTranslatedName();
        } catch (Exception ignored){}
        return getName();
    }

    private void onCommandExec(String[] args) {
        if(args.length>0){
            for(OptionBase<?> o:getOptions()){
                if(o.getName().equals(args[0])){
                    StringBuilder newArgs= new StringBuilder();
                    if(args.length>1) {
                        for (int i = 1; i < args.length; i++) {
                            newArgs.append(args[i]);
                        }
                        o.onCommandExec(new String[]{newArgs.toString()});
                    } else {
                        o.onCommandExec(new String[]{});
                    }

                    ConfigManager.save();
                }
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (OptionBase<?> o : getOptions()) {
                builder.append("    ").append(Formatting.AQUA).append(o.getName()).append(": ").append(o.get()).append("\n");
            }
            Util.sendChatMessage(new LiteralText(Formatting.BLUE + "Values in this category are: \n" + builder));
        }
    }

    protected List<String> getCommandSuggestions(String[] args){
        List<String> list = new ArrayList<>();
        if(args.length>=1){
            for(OptionBase<?> o:getOptions()){
                if(o.getName().equals(args[0])){
                    list.addAll(o.getCommandSuggestions());
                }
            }
        } else {
            for (Option o : getOptions()) {
                list.add(o.getName());
            }
        }
        return list;
    }
}
