package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import net.minecraft.text.Text;

public interface Option {

    OptionType getType();

    String getName();

    default Text getTranslatedName(){
        return Text.translatable(this.getName());
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();
}
