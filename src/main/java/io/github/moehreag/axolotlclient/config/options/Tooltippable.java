package io.github.moehreag.axolotlclient.config.options;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

	String getName();


	default @Nullable Text getTooltip(){
		if(!Objects.equals(Text.translatable(getName() + ".tooltip").getString(), getName() + ".tooltip")) {
			return Text.translatable(getName()+ ".tooltip");
		}
		return null;
	}
}
