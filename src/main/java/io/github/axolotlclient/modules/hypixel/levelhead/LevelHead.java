package io.github.axolotlclient.modules.hypixel.levelhead;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;

public class LevelHead implements AbstractHypixelMod {

    private static final LevelHead Instance = new LevelHead();

    private final OptionCategory category = new OptionCategory("axolotlclient.levelhead");
    public BooleanOption enabled = new BooleanOption("axolotlclient.enabled", false);
    public BooleanOption background = new BooleanOption("axolotlclient.background", false);
    public ColorOption textColor = new ColorOption("axolotlclient.textColor", Color.GOLD);
    public EnumOption mode = new EnumOption("axolotlclient.levelHeadMode", LevelHeadMode.values(), LevelHeadMode.NETWORK.toString());

    @Override
    public void init() {

        category.add(enabled);
        category.add(textColor);
        category.add(background);
        category.add(mode);
    }

    public static LevelHead getInstance(){
        return Instance;
    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }

    public enum LevelHeadMode{
        NETWORK,
        BEDWARS,
        SKYWARS
    }
}
