package io.github.moehreag.axolotlclient.config;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.FloatOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.config.options.StringOption;

import java.util.ArrayList;
import java.util.List;

public class AxolotlclientConfig {

    public final BooleanOption showOwnNametag = new BooleanOption("showOwnNametag", false);
    public final BooleanOption useShadows = new BooleanOption("useShadows", false);

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
    public final BooleanOption decreaseSensitivity = new BooleanOption("decreaseSensitivity", true);

    public final BooleanOption enableRPC = new BooleanOption("enableRPC", true);
    public final BooleanOption showActivity = new BooleanOption("showActivity", true);

    public final BooleanOption rotateWorld = new BooleanOption("rotateWorld", false);

    private final List<Option> options = new ArrayList<>();

    public void add(Option option){
        options.add(option);
    }


    public List<Option> get(){
        return options;
    }


    public void init(){
        add(showOwnNametag);
        add(useShadows);

        add(showBadges);
        add(customBadge);
        add(badgeText);

        add(name);
        add(hideNames);
        add(hideOwnSkin);
        add(hideOtherSkins);

        add(customSky);
        add(showSunMoon);
        add(zoomDivisor);
        add(decreaseSensitivity);
    }

}
