package io.github.axolotlclient.modules.hud.gui.component;

import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;

import java.util.List;

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
