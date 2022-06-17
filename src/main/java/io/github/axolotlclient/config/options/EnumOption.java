package io.github.axolotlclient.config.options;

public interface EnumOption extends Option {

    @Override
    default OptionType getType() {
        return OptionType.ENUM;
    }

    Enum<?> get();

    Enum<?> next();
}
