package io.github.moehreag.axolotlclient.config.options;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OptionCategory {

    Identifier Id;
	String name;
    private final List<Option> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategory(String key){
	    this.name =key;
    }

	public OptionCategory(Identifier Id, String key){
		this.name = key;
		this.Id=Id;
    }

    public List<Option> getOptions(){return options;}

    public void add(Option option){options.add(option);}

    public void add(List<Option> options){this.options.addAll(options);}

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

	public @Nullable Text getTooltip(){
		if(!Objects.equals(Text.translatable(getName() + ".tooltip").getString(), getName() + ".tooltip")) {
			return Text.translatable(getName() + ".tooltip");
		}
		return null;
	}
}
