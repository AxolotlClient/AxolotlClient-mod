package io.github.moehreag.axolotlclient.modules.hypixel;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.config.options.StringOption;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import io.github.moehreag.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.moehreag.axolotlclient.modules.hypixel.autotip.AutoTip;
import io.github.moehreag.axolotlclient.modules.hypixel.levelhead.LevelHead;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HypixelMods extends AbstractModule {

    public static HypixelMods INSTANCE = new HypixelMods();

    public StringOption hypixel_api_key = new StringOption("hypixel_api_key", "");

    private final OptionCategory category = new OptionCategory(new Identifier("hypixel"), "hypixel-mods");
    private final List<AbstractHypixelMod> subModules = new ArrayList<>();

    @Override
    public void init() {

        category.add(hypixel_api_key);

        category.addSubCategory(LevelHead.getInstance().getCategory());
        category.addSubCategory(AutoGG.Instance.getCategory());
        category.addSubCategory(new AutoTip().getCategory());

        subModules.add(LevelHead.getInstance());
        subModules.add(AutoGG.Instance);
        subModules.add(new AutoTip());

        subModules.forEach(AbstractHypixelMod::init);
        //subModules.forEach(hypixelMods -> addSubModule(hypixelMods.getCategory()));


        Axolotlclient.CONFIG.addCategory(category);
    }

    public void tick(){
        subModules.forEach(abstractHypixelMod -> {
            if(abstractHypixelMod.tickable())abstractHypixelMod.tick();
        });
    }

    @Override
    public void lateInit() {
        HypixelAbstractionLayer.loadApiKey();
    }

    public static HypixelMods getInstance(){
        return INSTANCE;
    }

    private void addSubModule(OptionCategory category){
        this.category.addSubCategory(category);
    }

    public OptionCategory getCategory() {
        return null;
    }
}
