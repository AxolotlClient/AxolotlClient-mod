package io.github.moehreag.axolotlclient.config.options;

public interface EnumOption extends Option {

    @Override
    default OptionType getType() {
        return OptionType.ENUM;
    }

    @Override
    String getName();

    Enum<?> get();

    Enum<?> next();
}
