package io.github.axolotlclient.config;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.ConfigHolder;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.config.screen.CreditsScreen;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class AxolotlClientConfig extends ConfigHolder {

    public final BooleanOption showOwnNametag = new BooleanOption("axolotlclient.showOwnNametag", false);
    public final BooleanOption useShadows = new BooleanOption("axolotlclient.useShadows", false);
    public final BooleanOption nametagBackground = new BooleanOption("axolotlclient.nametagBackground", true);

    public final BooleanOption showBadges = new BooleanOption("axolotlclient.showBadges", value -> {
        if(value){
            NetworkHelper.setOnline();
        } else {
            NetworkHelper.setOffline();
        }
    }, true);
    public final BooleanOption customBadge = new BooleanOption("axolotlclient.customBadge", false);
    public final StringOption badgeText = new StringOption("axolotlclient.badgeText", "");

    public final BooleanOption timeChangerEnabled = new BooleanOption("axolotlclient.enabled", false);
    public final IntegerOption customTime = new IntegerOption("axolotlclient.time", 0, 0, 24000);
    public final BooleanOption customSky = new BooleanOption("axolotlclient.customSky", true);
    public final IntegerOption cloudHeight = new IntegerOption("axolotlclient.cloudHeight", 128, 100, 512);
    public final BooleanOption dynamicFOV = new BooleanOption("axolotlclient.dynamicFov", true);
    public final BooleanOption fullBright = new BooleanOption("axolotlclient.fullBright", false);
    public final BooleanOption lowFire = new BooleanOption("axolotlclient.lowFire", false);

    public final ColorOption loadingScreenColor = new ColorOption("axolotlclient.loadingBgColor", new Color(-1));
    public final BooleanOption nightMode = new BooleanOption("axolotlclient.nightMode", false);
    public final BooleanOption rawMouseInput = new BooleanOption("axolotlclient.rawMouseInput", false);

    public final BooleanOption enableCustomOutlines = new BooleanOption("axolotlclient.enabled", false);
    public final ColorOption outlineColor = new ColorOption("axolotlclient.color", Color.parse("#DD000000"));
    public final IntegerOption outlineWidth = new IntegerOption("axolotlclient.outlineWidth", 1, 1, 10);

    public final BooleanOption debugLogOutput = new BooleanOption("axolotlclient.debugLogOutput", false);
    public final GenericOption openCredits = new GenericOption("Credits", "Open Credits", (mouseX, mouseY)->
        MinecraftClient.getInstance().openScreen(new CreditsScreen(MinecraftClient.getInstance().currentScreen))
    );
    public final BooleanOption creditsBGM = new BooleanOption("axolotlclient.creditsBGM", true);

    public final OptionCategory general = new OptionCategory("axolotlclient.general");
    public final OptionCategory nametagOptions = new OptionCategory("axolotlclient.nametagOptions");
    public final OptionCategory rendering = new OptionCategory("axolotlclient.rendering");
    public final OptionCategory outlines= new OptionCategory("axolotlclient.blockOutlines");
    public final OptionCategory timeChanger = new OptionCategory("axolotlclient.timeChanger");
    public final OptionCategory searchFilters = new OptionCategory("searchFilters");

    private final List<Option<?>> options = new ArrayList<>();
    private final List<OptionCategory> categories = new ArrayList<>();

    public final List<OptionCategory> config = new ArrayList<>();

    public void add(Option<?> option){
        options.add(option);
    }

    public void addCategory(OptionCategory cat){
        categories.add(cat);
    }

    public List<OptionCategory> getCategories(){
        return categories;
    }

    public List<Option<?>> getOptions(){
        return options;
    }


    public void init(){

        categories.add(general);
        categories.add(nametagOptions);
        categories.add(rendering);

        categories.forEach(OptionCategory::clearOptions);

        nametagOptions.add(showOwnNametag);
        nametagOptions.add(useShadows);
        nametagOptions.add(nametagBackground);

        nametagOptions.add(showBadges);
        nametagOptions.add(customBadge);
        nametagOptions.add(badgeText);

        general.add(loadingScreenColor);
        general.add(nightMode);
        general.add(AxolotlClientConfigConfig.showQuickToggles);
        general.add(AxolotlClientConfigConfig.showOptionTooltips);
        general.add(AxolotlClientConfigConfig.showCategoryTooltips);
        general.add(rawMouseInput);
        general.add(openCredits);
        general.add(debugLogOutput);

        searchFilters.add(AxolotlClientConfigConfig.searchIgnoreCase,
                AxolotlClientConfigConfig.searchForOptions,
                AxolotlClientConfigConfig.searchSort,
                AxolotlClientConfigConfig.searchSortOrder);
        general.addSubCategory(searchFilters);

        rendering.add(customSky,
                cloudHeight,
                AxolotlClientConfigConfig.chromaSpeed,
                dynamicFOV,
                fullBright,
                lowFire
        );

        timeChanger.add(timeChangerEnabled);
        timeChanger.add(customTime);
        rendering.addSubCategory(timeChanger);

        outlines.add(enableCustomOutlines);
        outlines.add(outlineColor);
        outlines.add(outlineWidth);
        rendering.addSubCategory(outlines);

        AxolotlClient.config.add(creditsBGM);

    }

}
