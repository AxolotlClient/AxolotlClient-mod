package io.github.moehreag.axolotlclient.config.options;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;

public abstract class OptionBase {
    public String name;
    public OptionBase(String name){
	    this.name=name;
    }

	public abstract ClickableWidget createButton(GameOptions options, int x, int y, int width);
}
