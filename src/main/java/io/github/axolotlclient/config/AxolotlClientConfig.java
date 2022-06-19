package io.github.axolotlclient.config;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.*;

import java.util.ArrayList;
import java.util.List;

public class AxolotlClientConfig {

    public final BooleanOption showOwnNametag = new BooleanOption("showOwnNametag", false);
    public final BooleanOption useShadows = new BooleanOption("useShadows", false);
    public final BooleanOption nametagBackground = new BooleanOption("nametagBackground", true);

    public final BooleanOption showBadges = new BooleanOption("showBadges", true);
    public final BooleanOption customBadge = new BooleanOption("customBadge", false);
    public final StringOption badgeText = new StringOption("badgeText", "");

    public final BooleanOption timeChangerEnabled = new BooleanOption("enabled", false);
    public final IntegerOption customTime = new IntegerOption("time", 0, 0, 24000);
    public final BooleanOption customSky = new BooleanOption("customSky", true);
    public final BooleanOption showSunMoon = new BooleanOption("showSunMoon", true);
    public final IntegerOption cloudHeight = new IntegerOption("cloudHeight", 128, 100, 512);
    public final BooleanOption dynamicFOV = new BooleanOption("dynamicFov", true);
    public final BooleanOption fullBright = new BooleanOption("fullBright", false);
    public final IntegerOption chromaSpeed = new IntegerOption("chromaSpeed", 20, 10, 50);

    public final BooleanOption motionBlurEnabled = new BooleanOption("enabled", false);
    public final FloatOption motionBlurStrength = new FloatOption("strength", 1F, 99F, 50F);
    public final BooleanOption motionBlurInGuis = new BooleanOption("inGuis", false);

    public final BooleanOption showOptionTooltips = new BooleanOption("showOptionTooltips", true);
    public final BooleanOption showCategoryTooltips = new BooleanOption("showCategoryTooltips", false);
    public final BooleanOption quickToggles = new BooleanOption("quickToggles", false);
    public final BooleanOption enableRPC = new BooleanOption("enableRPC", true);
    public final BooleanOption showActivity = new BooleanOption("showActivity", true);
    public final ColorOption loadingScreenColor = new ColorOption("loadingBgColor", new Color(-1));
    public final BooleanOption nightMode = new BooleanOption("nightMode", false);

    public final BooleanOption rotateWorld = new BooleanOption("rotateWorld", false);

    public final BooleanOption enableCustomOutlines = new BooleanOption("enabled", false);
    public final ColorOption outlineColor = new ColorOption("color", Color.parse("#04000000"));
    public final BooleanOption outlineChroma = new BooleanOption("chroma", false);
    public final IntegerOption outlineWidth = new IntegerOption("outlineWidth", 1, 1, 10);

    public final BooleanOption creditsBGM = new BooleanOption("creditsBGM", true);

    public final OptionCategory general = new OptionCategory("general");
    public final OptionCategory nametagOptions = new OptionCategory("nametagOptions");
    public final OptionCategory rendering = new OptionCategory("rendering");
    public final OptionCategory outlines= new OptionCategory("blockOutlines");
    public final OptionCategory motionBlur = new OptionCategory("motionBlur");
    public final OptionCategory timeChanger = new OptionCategory("timeChanger");

    private final List<Option> options = new ArrayList<>();
    private final List<OptionCategory> categories = new ArrayList<>();

    public final List<OptionCategory> config = new ArrayList<>();

    public void add(Option option){
        options.add(option);
    }

    public void addCategory(OptionCategory cat){
        categories.add(cat);
    }

    public List<OptionCategory> getCategories(){
        return categories;
    }

    public List<Option> getOptions(){
        return options;
    }


    public void init(){

        categories.add(general);
        categories.add(nametagOptions);
        categories.add(rendering);

        rendering.addSubCategory(outlines);

        categories.forEach(OptionCategory::clearOptions);

        nametagOptions.add(showOwnNametag);
        nametagOptions.add(useShadows);
        nametagOptions.add(nametagBackground);

        nametagOptions.add(showBadges);
        nametagOptions.add(customBadge);
        nametagOptions.add(badgeText);

        general.add(enableRPC);
        general.add(showActivity);
        general.add(loadingScreenColor);
        general.add(nightMode);
        general.add(quickToggles);
        general.add(showOptionTooltips);
        general.add(showCategoryTooltips);

        rendering.add(customSky);
        rendering.add(showSunMoon);
        rendering.add(cloudHeight);
        rendering.add(chromaSpeed);
        rendering.add(dynamicFOV);
        rendering.add(fullBright);

        motionBlur.add(motionBlurEnabled);
        motionBlur.add(motionBlurStrength);
        motionBlur.add(motionBlurInGuis);
        rendering.addSubCategory(motionBlur);

        timeChanger.add(timeChangerEnabled);
        timeChanger.add(customTime);
        rendering.addSubCategory(timeChanger);

        outlines.add(enableCustomOutlines);
        outlines.add(outlineColor);
        outlines.add(outlineChroma);
        outlines.add(outlineWidth);

        AxolotlClient.config.add(creditsBGM);

    }

}
