package io.github.axolotlclient.config.options;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

	String getName();

    default Text getTooltip(){
        return getTooltip(getName());
    }

	default @Nullable Text getTooltip(String location){

        if(!Objects.equals(Text.translatable(location + ".tooltip").getString(), location + ".tooltip")) {
            return Text.translatable(location + ".tooltip");
        }

		return null;
	}
}
