package io.github.moehreag.axolotlclient.config.options;

import io.github.moehreag.axolotlclient.config.screen.widgets.CategoryWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class OptionCategory extends OptionBase {

    Identifier Id;
    private final List<Option> options = new ArrayList<>();
    private final List<OptionCategory> subCategories = new ArrayList<>();

    public OptionCategory(String key){
	    super(key);
    }

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		return new CategoryWidget(this, x, y, width, 20);
	}

	public OptionCategory(Identifier Id, String key){
		super(key);
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

}
