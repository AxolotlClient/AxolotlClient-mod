package io.github.moehreag.axolotlclient;

import io.github.moehreag.axolotlclient.config.AxolotlclientConfig;
import io.github.moehreag.axolotlclient.config.ConfigHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


public class Axolotlclient implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("Axolotlclient");

	public static AxolotlclientConfig CONFIG;
	public static String onlinePlayers = "";
	public static String otherPlayers = "";

	public static final Identifier FONT = new Identifier("axolotlclient", "default");
	public static String badge = "✵";

	public static boolean showWarning = true;
	public static boolean TitleDisclaimer = false;
	public static boolean features = false;
	public static String badmod;

	public static Integer tickTime = 0;

	@Override
	public void onInitialize() {

		if (FabricLoader.getInstance().isModLoaded("ares")){
			badmod = "Ares Client";
		} else if (FabricLoader.getInstance().isModLoaded("inertia")) {
			badmod = "Inertia Client";
		} else if (FabricLoader.getInstance().isModLoaded("meteor-client")) {
			badmod = "Meteor Client";
		} else if (FabricLoader.getInstance().isModLoaded("wurst")) {
			badmod = "Wurst Client";
		} else if (FabricLoader.getInstance().isModLoaded("baritone")) {
			badmod = "Baritone";
		} else if (FabricLoader.getInstance().isModLoaded("xaerominimap")) {
			badmod = "Xaero's Minimap";
		} else if (FabricLoader.getInstance().isDevelopmentEnvironment() ||
				Arrays.toString(FabricLoader.getInstance().getLaunchArguments(false)).contains("Axolotlclient")){

			ConfigHandler.init();

			LOGGER.info(CONFIG);
			if (CONFIG.RPCConfig.enableRPC) io.github.moehreag.axolotlclient.util.DiscordRPC.startup();

			features = true;
			showWarning = false;
			badmod = null;

			LOGGER.info("Axolotlclient Initialized");
		}


	}
}
