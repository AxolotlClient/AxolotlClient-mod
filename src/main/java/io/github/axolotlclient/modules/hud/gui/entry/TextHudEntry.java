package io.github.axolotlclient.modules.hud.gui.entry;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public abstract class TextHudEntry extends BoxHudEntry {

    protected ColorOption textColor = new ColorOption("axolotlclient.textcolor", Color.WHITE);
    protected BooleanOption shadow = new BooleanOption("axolotlclient.shadow", getShadowDefault());

    protected boolean getShadowDefault() {
        return true;
    }

    public TextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(textColor);
        options.add(shadow);
        return options;
    }

}
