package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

public interface AbstractHypixelMod {

    void init();

    OptionCategory getCategory();

    default void tick(){}

    default boolean tickable(){return false;}
}
