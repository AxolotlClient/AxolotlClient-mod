package io.github.axolotlclient.api;

import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.Module;

public abstract class Options implements Module {

	public final BooleanOption enabled = new BooleanOption("enabled", true);
	public final BooleanOption friendRequestsEnabled = new BooleanOption("friendRequestsEnabled", true);

	protected final OptionCategory category = new OptionCategory("api.category");

	@Override
	public void init() {
		category.add(enabled, friendRequestsEnabled);
	}
}
