package io.github.moehreag.axolotlclient;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.config.AxolotlclientConfig;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.levelhead.LevelHead;
import io.github.moehreag.axolotlclient.modules.zoom.Zoom;
import io.github.moehreag.axolotlclient.util.Util;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Axolotlclient implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("Axolotlclient");

	public static AxolotlclientConfig CONFIG;
	public static String onlinePlayers = "";
	public static String otherPlayers = "";

	public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

	public static final OptionCategory config = new OptionCategory(new Identifier("storedOptions"), "storedOptions");
	public static final List<AbstractModule> modules= new ArrayList<>();

	public static Integer tickTime = 0;

	@Override
	public void onInitialize() {

		CONFIG = new AxolotlclientConfig();


		getModules();
		CONFIG.init();
		modules.forEach(AbstractModule::init);

		CONFIG.config.addAll(CONFIG.getCategories());
		CONFIG.config.add(config);

		ConfigManager.load();

		if (CONFIG.enableRPC.get()) io.github.moehreag.axolotlclient.util.DiscordRPC.startup();

		modules.forEach(AbstractModule::lateInit);



		LOGGER.info("Axolotlclient Initialized");
	}

	public static void getModules(){
		modules.add(new Zoom());
		modules.add(HudManager.getINSTANCE());
		modules.add(LevelHead.getInstance());
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

			if(Axolotlclient.CONFIG.showBadges.get() && Axolotlclient.isUsingClient(entity.getUuid())) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(Axolotlclient.badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer.getStringWidth(
						Axolotlclient.CONFIG.hideNames.get() ? Axolotlclient.CONFIG.name.get(): entity.getName().asFormattedString()
				)/2 + (Axolotlclient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer.getStringWidth(Axolotlclient.CONFIG.badgeText.get()): 10));

				GlStateManager.color4f(1, 1, 1, 1);

				if(Axolotlclient.CONFIG.customBadge.get()) MinecraftClient.getInstance().textRenderer.draw(Axolotlclient.CONFIG.badgeText.get(), x, 0 ,-1, Axolotlclient.CONFIG.useShadows.get());
				else DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);


			}
		}
	}
}
