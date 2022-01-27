package io.github.moehreag.axolotlclient;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import io.github.moehreag.axolotlclient.config.AxolotlclientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.UUID;


public class Axolotlclient implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("Axolotlclient");

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
	public void onInitializeClient(){

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

			AutoConfig.register(AxolotlclientConfig.class, JanksonConfigSerializer::new);
			CONFIG = AutoConfig.getConfigHolder(AxolotlclientConfig.class).getConfig();

			io.github.moehreag.axolotlclient.util.DiscordRPC.startup();

			features = true;
			showWarning = false;
			badmod = null;

			LOGGER.info("Axolotlclient Initialized");
		}
	}

	public static boolean showOwnNametag() {
		return CONFIG.showOwnNametag;
	}

	public static boolean isUsingClient(UUID uuid){
		assert MinecraftClient.getInstance().player != null;
		if (uuid == MinecraftClient.getInstance().player.getUuid()){
			return true;
		} else {
			return NetworkHelper.getOnline(uuid);
		}
	}


	public static void TickClient(){

		if (tickTime >=6000){

			//System.out.println("Cleared Cache of Other Players!");
			otherPlayers = "";
			tickTime = 0;
		}
		tickTime++;

	}

}
