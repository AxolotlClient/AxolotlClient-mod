package io.github.moehreag.branding;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import static com.google.gson.JsonParser.parseReader;

import draylar.omegaconfig.OmegaConfig;
import draylar.omegaconfiggui.OmegaConfigGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import io.github.moehreag.branding.config.AxolotlclientConfig;
import net.minecraft.client.MinecraftClient;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;


public class Axolotlclient implements ClientModInitializer {

	public static final AxolotlclientConfig CONFIG = OmegaConfig.register(AxolotlclientConfig.class);
	public static String onlinePlayers = "";
	public static String otherPlayers = "";


	public static boolean showWarning = true;
	public static boolean TitleDisclaimer = false;
	public static String badmod;

	public static Integer tickTime = 0;

	@Override
	public void onInitializeClient(){
		if(Files.exists(FabricLoader.getInstance().getConfigDir().resolve("Axolotlclient.json"))) recoverOldConfig();


		if (FabricLoader.getInstance().isModLoaded("ares")){
			badmod = "Ares Client";
		} else if (FabricLoader.getInstance().isModLoaded("inertia")) {
			badmod = "Inertia Client";
		} else if (FabricLoader.getInstance().isModLoaded("meteor-client")) {
			badmod = "Meteor Client";
		} else if (FabricLoader.getInstance().isModLoaded("wurst")) {
			badmod = "Wurst Client";
		} else {
			OmegaConfigGui.registerConfigScreen(Axolotlclient.CONFIG);
			showWarning = false;
			badmod = null;
		}
	}

	public void recoverOldConfig() {

		try {
			JsonReader reader = new JsonReader(new FileReader("config/Axolotlclient.json"));
			JsonObject conf = parseReader(reader).getAsJsonObject();

			CONFIG.showOwnNametag = conf.get("showOwnNametag").getAsBoolean();
			CONFIG.showBadge = conf.get("showBadge").getAsBoolean();

		} catch (IOException e) {
			throw new RuntimeException("Can't read settings for Axolotlclient!");
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
