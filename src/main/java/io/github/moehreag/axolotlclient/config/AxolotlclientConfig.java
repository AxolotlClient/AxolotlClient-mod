package io.github.moehreag.axolotlclient.config;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.config.options.*;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AxolotlclientConfig {

    public final BooleanOption showOwnNametag = new BooleanOption("showOwnNametag", false);
    public final BooleanOption useShadows = new BooleanOption("useShadows", false);
    public final BooleanOption nametagBackground = new BooleanOption("nametagBackground", true);

    public final BooleanOption showBadges = new BooleanOption("showBadges", true);
    public final BooleanOption customBadge = new BooleanOption("customBadge", false);
    public final StringOption badgeText = new StringOption("badgeText", "");

    public final StringOption name = new StringOption("name", "Player");
    public final BooleanOption hideNames = new BooleanOption("hideNames", false);
    public final BooleanOption hideOwnSkin = new BooleanOption("hideOwnSkin", false);
    public final BooleanOption hideOtherSkins = new BooleanOption("hideOtherSkins", false);

    public final BooleanOption customSky = new BooleanOption("customSky", true);
    public final BooleanOption showSunMoon = new BooleanOption("showSunMoon", true);
    public final FloatOption zoomDivisor = new FloatOption("zoomDivisor", 1F, 10F, 4F);
    public final IntegerOption zoomSpeed = new IntegerOption("zoomSpeed", 5, 1, 10);
    public final BooleanOption decreaseSensitivity = new BooleanOption("decreaseSensitivity", true);
    public final BooleanOption dynamicFOV = new BooleanOption("dynamicFov", true);
    public final BooleanOption fullBright = new BooleanOption("fullBright", false);
    public final IntegerOption chromaSpeed = new IntegerOption("chromaSpeed", 20, 10, 50);

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

    public final OptionCategory general = new OptionCategory(new Identifier("axolotlclient", "general"), "general");
    public final OptionCategory nametagOptions = new OptionCategory(new Identifier("axolotlclient", "nametagOptions"), "nametagOptions");
    public final OptionCategory rendering = new OptionCategory(new Identifier("axolotlclient", "rendering"), "rendering");
    public final OptionCategory zoom = new OptionCategory("zoom");
    public final OptionCategory outlines= new OptionCategory("blockOutlines");

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

        rendering.addSubCategory(zoom);
        rendering.addSubCategory(outlines);

        categories.forEach(OptionCategory::clearOptions);

        nametagOptions.add(showOwnNametag);
        nametagOptions.add(useShadows);
        nametagOptions.add(nametagBackground);

        nametagOptions.add(showBadges);
        nametagOptions.add(customBadge);
        nametagOptions.add(badgeText);

        /*add(name);
        add(hideNames);
        add(hideOwnSkin);
        add(hideOtherSkins);*/

        general.add(enableRPC);
        general.add(showActivity);
        general.add(loadingScreenColor);
        general.add(nightMode);

        rendering.add(customSky);
        rendering.add(showSunMoon);
        rendering.add(chromaSpeed);
        rendering.add(dynamicFOV);
        rendering.add(fullBright);

        zoom.add(zoomDivisor);
        zoom.add(zoomSpeed);
        zoom.add(decreaseSensitivity);

        outlines.add(enableCustomOutlines);
        outlines.add(outlineColor);
        outlines.add(outlineChroma);
        outlines.add(outlineWidth);

        Axolotlclient.config.add(creditsBGM);

    }

}
