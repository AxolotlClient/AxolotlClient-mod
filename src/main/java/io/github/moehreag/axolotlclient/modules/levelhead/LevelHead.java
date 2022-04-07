package io.github.moehreag.axolotlclient.modules.levelhead;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.ColorOption;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.config.options.StringOption;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import net.minecraft.util.Identifier;

public class LevelHead extends AbstractModule {

    private static final LevelHead Instance = new LevelHead();

    private OptionCategory category = new OptionCategory(new Identifier("levelhead"), "levelhead");
    public BooleanOption enabled = new BooleanOption("enabled", false);
    public BooleanOption background = new BooleanOption("background", false);
    public ColorOption textColor = new ColorOption("textColor", Color.GOLD);
    public StringOption hypixel_api_key = new StringOption("hypixel_api_key", "");

    @Override
    public void init() {

        category.add(enabled);
        category.add(textColor);
        category.add(background);
        category.add(hypixel_api_key);

        Axolotlclient.CONFIG.addCategory(category);
    }

    @Override
    public void lateInit(){
        HypixelAbstractionLayer.loadApiKey();
    }

    public static LevelHead getInstance(){
        return Instance;
    }
}
