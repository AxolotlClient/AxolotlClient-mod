package io.github.axolotlclient;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class AxolotlClient implements ClientModInitializer {

	public static Logger LOGGER = LoggerFactory.getLogger("AxolotlClient");

	public static AxolotlClientConfig CONFIG;
	public static String onlinePlayers = "";
	public static String otherPlayers = "";

	public static List<ResourcePack> packs = new ArrayList<>();
	public static HashMap<Identifier, Resource> runtimeResources = new HashMap<>();

	public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

	public static final OptionCategory config = new OptionCategory("storedOptions");
	public static final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
	public static final HashMap<Identifier, AbstractModule> modules= new HashMap<>();

	public static Integer tickTime = 0;

	public static String badmod="";
	public static boolean titleDisclaimer = false;
	public static boolean showWarning = true;

	@Override
	public void onInitializeClient() {

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
		} else {
			showWarning = false;
		}

		CONFIG = new AxolotlClientConfig();
		config.add(someNiceBackground);

		getModules();
		CONFIG.init();
		modules.forEach((identifier, abstractModule) -> abstractModule.init());

		CONFIG.config.addAll(CONFIG.getCategories());
		CONFIG.config.add(config);

		ConfigManager.load();

		if (CONFIG.enableRPC.get()) DiscordRPC.startup();

		modules.forEach((identifier, abstractModule) -> abstractModule.lateInit());

        if(FabricLoader.getInstance().getModContainer("axolotlclient").isPresent()) {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("axolotlclient", "axolotlclient-ui"),
                FabricLoader.getInstance().getModContainer("axolotlclient").get(), ResourcePackActivationType.NORMAL);
        }
		ClientTickEvents.START_CLIENT_TICK.register(client -> tickClient());

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

		HudManager.tick();
		HypixelMods.getInstance().tick();
		DiscordRPC.update();
		Color.tickChroma();
		Zoom.tick();

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

	public static void addBadge(Entity entity, MatrixStack matrices){
		if(entity instanceof PlayerEntity){

			if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
				RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer.getWidth(
						entity.getUuid() == MinecraftClient.getInstance().player.getUuid()?
						(NickHider.Instance.hideOwnName.get() ? NickHider.Instance.hiddenNameSelf.get(): Team.decorateName(entity.getScoreboardTeam(), entity.getName()).getString()):
						(NickHider.Instance.hideOtherNames.get() ? NickHider.Instance.hiddenNameOthers.get(): Team.decorateName(entity.getScoreboardTeam(), entity.getName()).getString())
				)/2 + (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer.getWidth(AxolotlClient.CONFIG.badgeText.get()): 10));

				RenderSystem.setShaderColor(1, 1, 1, 1);

				if(AxolotlClient.CONFIG.customBadge.get()) {
					if(AxolotlClient.CONFIG.useShadows.get()) {
						MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, AxolotlClient.CONFIG.badgeText.get(), x, 0, -1);
					} else {
						MinecraftClient.getInstance().textRenderer.draw(matrices, AxolotlClient.CONFIG.badgeText.get(), x, 0, -1);
					}
				}
				else DrawableHelper.drawTexture(matrices, x, 0, 0, 0, 8, 8, 8, 8);


			}
		}
	}
}
