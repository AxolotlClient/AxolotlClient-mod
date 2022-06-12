package io.github.moehreag.axolotlclient.modules.hypixel;

import io.github.moehreag.axolotlclient.config.options.OptionCategory;

public interface AbstractHypixelMod {

    void init();

    OptionCategory getCategory();

    default void tick(){}

    default boolean tickable(){return false;}
}
