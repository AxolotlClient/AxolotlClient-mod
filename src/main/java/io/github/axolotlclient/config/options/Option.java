package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface Option extends Tooltippable {

    OptionType getType();

    String getName();

    default MutableText getTranslatedName(){
        return Text.translatable(this.getName());
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();

}
