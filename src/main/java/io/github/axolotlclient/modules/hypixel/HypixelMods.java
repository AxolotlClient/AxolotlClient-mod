package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.EnumOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.options.StringOption;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hypixel.autoboop.AutoBoop;
import io.github.axolotlclient.modules.hypixel.autogg.AutoGG;
import io.github.axolotlclient.modules.hypixel.autotip.AutoTip;
import io.github.axolotlclient.modules.hypixel.levelhead.LevelHead;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;

import java.util.ArrayList;
import java.util.List;

public class HypixelMods extends AbstractModule {

    private static final HypixelMods Instance = new HypixelMods();

    public StringOption hypixel_api_key = new StringOption("hypixel_api_key", "");
    public final EnumOption cacheMode = new EnumOption("cache_mode", HypixelCacheMode.values(), HypixelCacheMode.ON_CLIENT_DISCONNECT);

    private final OptionCategory category = new OptionCategory("hypixel-mods");
    private final List<AbstractHypixelMod> subModules = new ArrayList<>();

    public static HypixelMods getInstance(){
        return Instance;
    }

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

    public void tick(){
        subModules.forEach(abstractHypixelMod -> {
            if(abstractHypixelMod.tickable())abstractHypixelMod.tick();
        });
    }

    @Override
    public void lateInit() {
        HypixelAbstractionLayer.loadApiKey();
    }

    private void addSubModule(AbstractHypixelMod mod){
        this.subModules.add(mod);
        this.category.addSubCategory(mod.getCategory());
    }

    public OptionCategory getCategory() {
        return null;
    }

    public enum HypixelCacheMode{
        ON_CLIENT_DISCONNECT,
        ON_PLAYER_DISCONNECT
    }
}
