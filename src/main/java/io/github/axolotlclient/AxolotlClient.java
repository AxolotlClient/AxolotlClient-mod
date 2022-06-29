package io.github.axolotlclient;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.config.AxolotlClientConfig;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.axolotlclient.modules.motionblur.MotionBlur;
import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.DiscordRPC;
import io.github.axolotlclient.util.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.*;


public class AxolotlClient implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("AxolotlClient");

	public static AxolotlClientConfig CONFIG;
	public static String onlinePlayers = "";
	public static String otherPlayers = "";

	public static List<ResourcePack> packs = new ArrayList<>();
	public static HashMap<Identifier, Resource> runtimeResources = new HashMap<>();

	public static boolean initalized = false;

	public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

	public static final OptionCategory config = new OptionCategory(new Identifier("storedOptions"), "storedOptions");
	public static final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
	public static final HashMap<Identifier, AbstractModule> modules = new HashMap<>();

	public static Integer tickTime = 0;

	@Override
	public void onInitialize() {

		CONFIG = new AxolotlClientConfig();
		config.add(someNiceBackground);
		config.add(CONFIG.rotateWorld);

		getModules();
		CONFIG.init();
		modules.forEach((identifier, abstractModule) -> abstractModule.init());

		CONFIG.config.addAll(CONFIG.getCategories());
		CONFIG.config.add(config);

		ConfigManager.load();

		if (CONFIG.enableRPC.get()) io.github.axolotlclient.util.DiscordRPC.startup();

		modules.forEach((identifier, abstractModule) -> abstractModule.lateInit());

		FabricLoader.getInstance().getModContainer("axolotlclient").ifPresent(modContainer -> {
			Optional<Path> optional = modContainer.findPath("resourcepacks/AxolotlClientUI.zip");
			optional.ifPresent(path -> MinecraftClient.getInstance().getResourcePackLoader().method_10366(path.toFile()));

		});

		LOGGER.info("AxolotlClient Initialized");
	}

	public static void getModules(){
		modules.put(Zoom.ID, new Zoom());
		modules.put(HudManager.ID, HudManager.getINSTANCE());
		modules.put(HypixelMods.ID, HypixelMods.INSTANCE);
		modules.put(MotionBlur.ID, new MotionBlur());
		modules.put(ScrollableTooltips.ID, ScrollableTooltips.Instance);
	}

	public static boolean isUsingClient(UUID uuid){
		assert MinecraftClient.getInstance().player != null;
		if (uuid == MinecraftClient.getInstance().player.getUuid()){
			return true;
		} else {
			return NetworkHelper.getOnline(uuid);
		}
	}


	public static void tickClient(){

		DiscordRPC.update();
		Zoom.tick();
		Color.tickChroma();
		HudManager.tick();
		HypixelMods.getInstance().tick();

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

			if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer.getStringWidth(
						entity.getUuid() == MinecraftClient.getInstance().player.getUuid()?
						(NickHider.Instance.hideOwnName.get() ? NickHider.Instance.hiddenNameSelf.get(): entity.method_6344().asFormattedString()):
						(NickHider.Instance.hideOtherNames.get() ? NickHider.Instance.hiddenNameOthers.get(): entity.method_6344().asFormattedString())
				)/2 + (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer.getStringWidth(AxolotlClient.CONFIG.badgeText.get()): 10));

				GlStateManager.color4f(1, 1, 1, 1);

				if(AxolotlClient.CONFIG.customBadge.get()) MinecraftClient.getInstance().textRenderer.draw(AxolotlClient.CONFIG.badgeText.get(), x, 0 ,-1, AxolotlClient.CONFIG.useShadows.get());
				else DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);


			}
		}
	}
}
