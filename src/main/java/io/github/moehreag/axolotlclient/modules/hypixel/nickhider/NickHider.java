package io.github.moehreag.axolotlclient.modules.hypixel.nickhider;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.config.options.StringOption;
import io.github.moehreag.axolotlclient.modules.hypixel.AbstractHypixelMod;

public class NickHider implements AbstractHypixelMod {

    public static NickHider Instance = new NickHider();

    private final OptionCategory category = new OptionCategory("nickhider");

    public StringOption hiddenNameSelf = new StringOption("hiddenNameSelf", "You");
    public StringOption hiddenNameOthers = new StringOption("hiddenNameOthers", "Player");
    public BooleanOption hideOwnName = new BooleanOption("hideOwnName", false);
    public BooleanOption hideOtherNames = new BooleanOption("hideOtherNames", false);
    public BooleanOption hideOwnSkin = new BooleanOption("hideOwnSkin", false);
    public BooleanOption hideOtherSkins = new BooleanOption("hideOtherSkins", false);

    @Override
    public void init() {
        category.add(hiddenNameSelf);
        category.add(hiddenNameOthers);
        category.add(hideOwnName);
        category.add(hideOtherNames);
        category.add(hideOwnSkin);
        category.add(hideOtherSkins);
    }

    @Override
    public OptionCategory getCategory() {
        return category;
    }
}
