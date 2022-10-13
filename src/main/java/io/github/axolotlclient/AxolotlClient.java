package io.github.axolotlclient;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.ConfigManager;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.config.AxolotlClientConfig;
import io.github.axolotlclient.config.AxolotlClientConfigManager;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.ModuleLoader;
import io.github.axolotlclient.modules.freelook.Freelook;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.axolotlclient.modules.motionblur.MotionBlur;
import io.github.axolotlclient.modules.particles.Particles;
import io.github.axolotlclient.modules.rpc.DiscordRPC;
import io.github.axolotlclient.modules.screenshotUtils.ScreenshotUtils;
import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.axolotlclient.modules.sky.SkyResourceManager;
import io.github.axolotlclient.modules.tnttime.TntTime;
import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.FeatureDisabler;
import io.github.axolotlclient.util.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.*;


public class AxolotlClient implements ClientModInitializer {

    public static String modid = "AxolotlClient";

    public static AxolotlClientConfig CONFIG;
    public static ConfigManager configManager;
    public static HashMap<UUID, Boolean> playerCache = new HashMap<>();

    public static HashMap<Identifier, Resource> runtimeResources = new HashMap<>();

    public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

    public static final OptionCategory config = new OptionCategory("storedOptions");
    public static final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
    public static final List<AbstractModule> modules = new ArrayList<>();

    public static Integer tickTime = 0;

    @Override
    public void onInitializeClient() {
        CONFIG = new AxolotlClientConfig();
        config.add(someNiceBackground);

        getModules();
        addExternalModules();
        CONFIG.init();
        modules.forEach(AbstractModule::init);

        CONFIG.config.addAll(CONFIG.getCategories());
        CONFIG.config.add(config);

        io.github.axolotlclient.AxolotlclientConfig.AxolotlClientConfigManager.registerConfig(modid, CONFIG, configManager = new AxolotlClientConfigManager());

        modules.forEach(AbstractModule::lateInit);

        FabricLoader.getInstance().getModContainer("axolotlclient").ifPresent(modContainer -> {
            Optional<Path> optional = modContainer.findPath("resourcepacks/AxolotlClientUI.zip");
            optional.ifPresent(path -> MinecraftClient.getInstance().getResourcePackLoader().method_10366(path.toFile()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> tickClient());

        FeatureDisabler.init();

        if(CONFIG.debugLogOutput.get()){
            Logger.debug("Debug Output enabled, Logs will be quite verbose!");
        }

        Logger.info("AxolotlClient Initialized");
    }

    private static void getModules(){
        modules.add(SkyResourceManager.getInstance());
        modules.add(Zoom.getInstance());
        modules.add(HudManager.getInstance());
        modules.add(HypixelMods.getInstance());
        modules.add(MotionBlur.getInstance());
        modules.add(ScrollableTooltips.getInstance());
        modules.add(DiscordRPC.getInstance());
        modules.add(Freelook.getInstance());
        modules.add(TntTime.getInstance());
        modules.add(Particles.getInstance());
        modules.add(ScreenshotUtils.getInstance());
    }

    private static void addExternalModules(){
        modules.addAll(ModuleLoader.loadExternalModules());
    }

    public static boolean isUsingClient(UUID uuid){
        if(uuid==null){
            return false;
        }

        assert MinecraftClient.getInstance().player != null;
        if (uuid == MinecraftClient.getInstance().player.getUuid()){
            return true;
        } else {
            return NetworkHelper.getOnline(uuid);
        }
    }

    public static void tickClient(){
        modules.forEach(AbstractModule::tick);

        if (tickTime >=6000){

            //System.out.println("Cleared Cache of Other Players!");
            if(playerCache.values().size()>500){
                playerCache.clear();
            }
            tickTime = 0;
        }
        tickTime++;

    }

    public static void addBadge(Entity entity){
        if(entity instanceof PlayerEntity && !entity.isSneaking()){

            if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableDepthTest();
                GlStateManager.enableAlphaTest();
                MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

                int x = -(MinecraftClient.getInstance().textRenderer.getStringWidth(
                        entity.getUuid() == MinecraftClient.getInstance().player.getUuid()?
                                (NickHider.Instance.hideOwnName.get() ? NickHider.Instance.hiddenNameSelf.get(): entity.getName().asFormattedString()):
                                (NickHider.Instance.hideOtherNames.get() ? NickHider.Instance.hiddenNameOthers.get(): entity.getName().asFormattedString())
                )/2 + (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer.getStringWidth(" "+AxolotlClient.CONFIG.badgeText.get()): 10));

                GlStateManager.color4f(1, 1, 1, 1);

                if(AxolotlClient.CONFIG.customBadge.get()) MinecraftClient.getInstance().textRenderer.draw(AxolotlClient.CONFIG.badgeText.get(), x, 0 ,-1, AxolotlClient.CONFIG.useShadows.get());
                else DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);


            }
        }
    }
}
