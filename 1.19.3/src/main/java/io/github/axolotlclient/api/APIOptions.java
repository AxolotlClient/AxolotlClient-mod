package io.github.axolotlclient.api;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public class APIOptions extends Options {

	@Getter
	private static final Options Instance = new APIOptions();

	@Override
	public void init() {
		super.init();

		category.add(new GenericOption("viewFriends", "clickToOpen", (mX, mY) -> MinecraftClient.getInstance().setScreen(new FriendsScreen(MinecraftClient.getInstance().currentScreen))));
		AxolotlClient.CONFIG.addCategory(category);
	}
}
