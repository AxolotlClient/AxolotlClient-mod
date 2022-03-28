package io.github.moehreag.axolotlclient;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.AxolotlclientConfig;
import io.github.moehreag.axolotlclient.config.ConfigHandler;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.zoom.Zoom;
import io.github.moehreag.axolotlclient.util.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.UUID;


public class Axolotlclient implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("Axolotlclient");

	public static AxolotlclientConfig CONFIG;
	public static String onlinePlayers = "";
	public static String otherPlayers = "";

	public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

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

			if (CONFIG.RPCConfig.enableRPC) io.github.moehreag.axolotlclient.util.DiscordRPC.startup();

			features = true;
			showWarning = false;
			badmod = null;

			Zoom.init();
			HudManager.init();

			LOGGER.info("Axolotlclient Initialized");
		}


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

		HudManager.tick();

		if(tickTime % 20 == 0){
			if(MinecraftClient.getInstance().getCurrentServerEntry() != null){
				Util.getRealTimeServerPing(MinecraftClient.getInstance().getCurrentServerEntry());
			}
		}

		if (tickTime >=6000){

			//System.out.println("Cleared Cache of Other Players!");
			otherPlayers = "";
			tickTime = 0;
		}
		tickTime++;

	}

	public static void addBadge(Entity entity){
		if(entity instanceof PlayerEntity){

			if(Axolotlclient.CONFIG.badgeOptions.showBadge && Axolotlclient.isUsingClient(entity.getUuid())) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(Axolotlclient.badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer.getStringWidth(
						Axolotlclient.CONFIG.NickHider.hideNames ? Axolotlclient.CONFIG.NickHider.Name: entity.getName().asFormattedString()
				)/2 + (Axolotlclient.CONFIG.badgeOptions.CustomBadge ? MinecraftClient.getInstance().textRenderer.getStringWidth(Axolotlclient.CONFIG.badgeOptions.badgeText): 10));

				GlStateManager.color4f(1, 1, 1, 1);

				if(Axolotlclient.CONFIG.badgeOptions.CustomBadge) MinecraftClient.getInstance().textRenderer.draw(Axolotlclient.CONFIG.badgeOptions.badgeText, x, 0 ,-1, Axolotlclient.CONFIG.NametagConf.useShadows);
				else DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);


			}
		}
	}
}
