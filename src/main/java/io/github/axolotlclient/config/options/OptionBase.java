package io.github.axolotlclient.config.options;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class OptionBase implements Option {

    public String name;
    public String tooltipKeyPrefix;

    public OptionBase(String name){
	    this.name=name;
    }

    public OptionBase(String name, String tooltipKeyPrefix){
        this.name = name;
        this.tooltipKeyPrefix = tooltipKeyPrefix;
    }

    @Override
    public @Nullable Text getTooltip() {
        return Option.super.getTooltip(tooltipKeyPrefix);
    }

	public String getName(){
		return name;
	}
}
