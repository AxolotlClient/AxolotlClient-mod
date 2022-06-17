package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.config.options.OptionCategory;

public interface AbstractHypixelMod {

    void init();

    OptionCategory getCategory();

    default void tick(){}

    default boolean tickable(){return false;}
}
