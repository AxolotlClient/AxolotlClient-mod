package io.github.axolotlclient.modules.hud.gui.component;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public interface Configurable {

    /**
     * Returns a list of options that will be configured
     *
     * @return List of configurable options
     */
    List<OptionBase<?>> getConfigurationOptions();

    /**
     * Returns a list of options that should be saved. By default, this includes {@link #getConfigurationOptions()}
     *
     * @return Options to save within a config
     */
    default List<OptionBase<?>> getSaveOptions() {
        return getConfigurationOptions();
    }

    OptionCategory getOptionsAsCategory();
}
