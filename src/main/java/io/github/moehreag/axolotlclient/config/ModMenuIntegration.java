package io.github.moehreag.axolotlclient.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.prospector.modmenu.gui.ModListScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

import java.util.function.Function;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return "axolotlclient";
    }
    @Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {

        if(MinecraftClient.getInstance().player != null)return parent -> (Screen) new AxolotlclientConfigScreen(new ModListScreen(new GameMenuScreen()));
        else return parent -> (Screen) new AxolotlclientConfigScreen(new ModListScreen(new TitleScreen()));
	}


}
