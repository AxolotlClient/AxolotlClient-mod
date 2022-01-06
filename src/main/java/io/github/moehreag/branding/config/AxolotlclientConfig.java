package io.github.moehreag.branding.config;

import com.sun.jna.StringArray;
import draylar.omegaconfig.api.Config;

public class AxolotlclientConfig implements Config {
	public boolean showOwnNametag = false;
	public boolean showBadge = true;

	@Override
	public String getName() {
		return "Axolotlclient";
	}

	@Override
	public String getModid() {
		return "axolotlclient";
	}
}
