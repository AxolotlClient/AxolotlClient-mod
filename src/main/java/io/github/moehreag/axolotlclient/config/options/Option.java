package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface Option {

    OptionType getType();

    String getName();

    default Text getTranslatedName(){
        return new TranslatableText(this.getName());
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();
}
