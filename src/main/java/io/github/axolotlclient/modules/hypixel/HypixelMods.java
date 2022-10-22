package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlclientConfig.options.StringOption;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.autotip.AutoTip;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHead;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;

import java.util.ArrayList;
import java.util.List;

public class HypixelMods extends AbstractModule {

    public static final HypixelMods INSTANCE = new HypixelMods();

    public StringOption hypixel_api_key = new StringOption("axolotlclient.hypixel_api_key", "");
    public EnumOption cacheMode = new EnumOption("axolotlclient.cache_mode", HypixelApiCacheMode.values(), HypixelApiCacheMode.ON_CLIENT_DISCONNECT.toString());

    private final OptionCategory category = new OptionCategory("axolotlclient.hypixel-mods");
    private final List<AbstractHypixelMod> subModules = new ArrayList<>();

    @Override
    public void init() {

        category.add(hypixel_api_key);
        category.add(cacheMode);

        addSubModule(LevelHead.getInstance());
        addSubModule(AutoGG.Instance);
        addSubModule(AutoTip.INSTANCE);
        addSubModule(NickHider.Instance);
        addSubModule(AutoBoop.Instance);

        subModules.forEach(AbstractHypixelMod::init);

        AxolotlClient.CONFIG.addCategory(category);
    }

    @Override
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

    public enum HypixelApiCacheMode {
        ON_CLIENT_DISCONNECT,
        ON_PLAYER_DISCONNECT,


    }
}
