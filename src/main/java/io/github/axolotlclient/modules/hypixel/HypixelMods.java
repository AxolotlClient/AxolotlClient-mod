package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.StringOption;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.autotip.AutoTip;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHead;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HypixelMods extends AbstractModule {

    public static Identifier ID = new Identifier("hypixel");

    public static HypixelMods INSTANCE = new HypixelMods();

    public StringOption hypixel_api_key = new StringOption("hypixel_api_key", "");

    private final OptionCategory category = new OptionCategory(new Identifier("hypixel"), "hypixel-mods");
    private final List<AbstractHypixelMod> subModules = new ArrayList<>();

    @Override
    public void init() {

        category.add(hypixel_api_key);

        addSubModule(LevelHead.getInstance());
        addSubModule(AutoGG.Instance);
        addSubModule(AutoTip.INSTANCE);
        addSubModule(NickHider.Instance);
        addSubModule(AutoBoop.Instance);

        subModules.forEach(AbstractHypixelMod::init);

        AxolotlClient.CONFIG.addCategory(category);
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

    private void addSubModule(AbstractHypixelMod mod){
        this.subModules.add(mod);
        this.category.addSubCategory(mod.getCategory());
    }

    public OptionCategory getCategory() {
        return null;
    }
}
