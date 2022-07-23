package io.github.axolotlclient.config.options;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Tooltippable {

	String getName();

    default Text getTooltip(){
        return getTooltip(getName());
    }

	default @Nullable Text getTooltip(String location){

        if(!Objects.equals(new TranslatableText(location + ".tooltip").getString(), location + ".tooltip")) {
            return new TranslatableText(location + ".tooltip");
        }

		return null;
	}
}
