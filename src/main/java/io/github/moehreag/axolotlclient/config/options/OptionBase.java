package io.github.moehreag.axolotlclient.config.options;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;

public abstract class OptionBase extends Option {
    public String name;
    public OptionBase(String name){
	    super(name);
	    this.name=name;
    }


}
