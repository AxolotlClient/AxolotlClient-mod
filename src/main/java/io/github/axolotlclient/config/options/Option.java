package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Option extends Tooltippable {

    OptionType getType();

    String getName();

    default String getTranslatedName(){
        return I18n.translate(this.getName());
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();
}
