package io.github.axolotlclient.config.modmenu;

import io.github.axolotlclient.modules.hud.HudEditScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuCompat implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (HudEditScreen::new);
	}
}
