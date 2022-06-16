package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Option {

    OptionType getType();

    String getName();

    default Text getTranslatedName(){
        return Text.translatable(this.getName());
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();

	default @Nullable Text getTooltip(){
		if(!Objects.equals(Text.translatable(getName() + ".tooltip").getString(), getName() + ".tooltip")) {
			return Text.translatable(getName() + ".tooltip");
		}
		return null;
	}
}
