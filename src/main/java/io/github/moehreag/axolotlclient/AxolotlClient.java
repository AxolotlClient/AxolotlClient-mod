package io.github.moehreag.axolotlclient;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.moehreag.axolotlclient.config.AxolotlClientConfig;
import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.OptionCategory;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hypixel.HypixelMods;
import io.github.moehreag.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.moehreag.axolotlclient.modules.motionblur.MotionBlur;
import io.github.moehreag.axolotlclient.modules.zoom.Zoom;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import io.github.moehreag.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class AxolotlClient implements ClientModInitializer {

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
	public static final HashMap<Identifier, AbstractModule> modules= new HashMap<>();

	public static Integer tickTime = 0;

	@Override
	public void onInitializeClient(ModContainer container) {

		CONFIG = new AxolotlClientConfig();
		config.add(someNiceBackground);
		config.add(CONFIG.rotateWorld);

		getModules();
		CONFIG.init();
		modules.forEach((identifier, abstractModule) -> abstractModule.init());

		CONFIG.config.addAll(CONFIG.getCategories());
		CONFIG.config.add(config);

		ConfigManager.load();

		if (CONFIG.enableRPC.get()) DiscordRPC.startup();

		modules.forEach((identifier, abstractModule) -> abstractModule.lateInit());

		ResourceLoader.registerBuiltinResourcePack(new Identifier("axolotlclient", "axolotlclient-ui"), container, ResourcePackActivationType.NORMAL);

		LOGGER.info("AxolotlClient Initialized");
	}

	public static void getModules(){
		modules.put(Zoom.ID, new Zoom());
		modules.put(HudManager.ID, HudManager.getINSTANCE());
		modules.put(HypixelMods.ID, HypixelMods.INSTANCE);
		modules.put(MotionBlur.ID, new MotionBlur());
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

	public static void addBadge(Entity entity, MatrixStack matrices){
		if(entity instanceof PlayerEntity){

			if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer.getWidth(
						entity.getUuid() == MinecraftClient.getInstance().player.getUuid()?
						(NickHider.Instance.hideOwnName.get() ? NickHider.Instance.hiddenNameSelf.get(): entity.getName().getString()):
						(NickHider.Instance.hideOtherNames.get() ? NickHider.Instance.hiddenNameOthers.get(): entity.getName().getString())
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
