package io.github.moehreag.axolotlclient.config;

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

    public final BooleanOption rotateWorld = new BooleanOption("rotateWorld", false);

    public final OptionCategory general = new OptionCategory(new Identifier("axolotlclient", "general"), "general");
    public final OptionCategory nametagOptions = new OptionCategory(new Identifier("axolotlclient", "nametagOptions"), "nametagOptions");
    public final OptionCategory badgeOptions = new OptionCategory(new Identifier("axolotlclient", "badgeOptions"), "badgeOptions");
    public final OptionCategory rpcOptions = new OptionCategory(new Identifier("axolotlclient", "rpcOptions"), "rpcConf");

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
        categories.add(badgeOptions);
        categories.add(rpcOptions);

        categories.forEach(OptionCategory::clearOptions);

        nametagOptions.add(showOwnNametag);
        nametagOptions.add(useShadows);
        nametagOptions.add(nametagBackground);

        badgeOptions.add(showBadges);
        badgeOptions.add(customBadge);
        badgeOptions.add(badgeText);

        /*add(name);
        add(hideNames);
        add(hideOwnSkin);
        add(hideOtherSkins);*/

        rpcOptions.add(enableRPC);
        rpcOptions.add(showActivity);

        general.add(customSky);
        general.add(showSunMoon);
        general.add(zoomDivisor);
        general.add(zoomSpeed);
        general.add(decreaseSensitivity);
        general.add(dynamicFOV);
        general.add(fullBright);
        general.add(chromaSpeed);
    }

}
