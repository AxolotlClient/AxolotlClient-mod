package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.sun.jna.Function;

public abstract interface EnumOption extends Option {

    @Override
    default OptionType getType() {
        return OptionType.ENUM;
    }

    @Override
    public String getName();

    Enum<?> get();

    Enum<?> next();
}
