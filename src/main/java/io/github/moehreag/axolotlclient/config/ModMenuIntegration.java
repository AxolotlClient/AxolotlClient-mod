package io.github.moehreag.axolotlclient.config;

import io.github.moehreag.axolotlclient.modules.hud.HudEditScreen;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return "axolotlclient";
    }
    @Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return parent -> (Screen) new HudEditScreen(parent);
	}
}
