package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Option extends Tooltippable {

    OptionType getType();

    String getName();

    default Text getTranslatedName(){
        return Text.translatable(this.getName());
    }

    void setValueFromJsonElement(JsonElement element);

    void setDefaults();

    JsonElement getJson();

}
