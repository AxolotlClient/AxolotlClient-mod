package io.github.axolotlclient.modules.rpc;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventHandler;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.DisableReason;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.rpc.gameSdk.GameSdkDownloader;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.OSUtil;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;

import java.time.Instant;

/**
 * This DiscordRPC module is derived from <a href="https://github.com/DeDiamondPro/HyCord">HyCord</a>.
 * @license GPL-3.0
 * @author DeDiamondPro
 */

public class DiscordRPC extends AbstractModule {

    private static DiscordRPC Instance;

    public OptionCategory category = new OptionCategory("rpc");

    public BooleanOption enabled = new BooleanOption("enabled", true);
    public BooleanOption showActivity = new BooleanOption("showActivity", true);

    public BooleanOption showTime = new BooleanOption("showTime", true);

    public static Activity currentActivity;
    public static Core discordRPC;
    Instant time = Instant.now();

    private static boolean running;

    public static DiscordRPC getInstance(){
        if(Instance == null) Instance = new DiscordRPC();
        return Instance;
    }

    public void init() {

        category.add(enabled, showTime, showActivity);

        AxolotlClient.CONFIG.addCategory(category);

        if(OSUtil.getOS() == OSUtil.OperatingSystem.OTHER){
            enabled.setForceOff(true, DisableReason.CRASH);
        }
    }

    @SuppressWarnings("BusyWait")
    public void initRPC(){

        if(enabled.get()){
            GameSdkDownloader.downloadSdk();
        }

        if(enabled.get()) {

            CreateParams params = new CreateParams();

            params.setClientID(875835666729152573L);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

            DiscordEventHandler handler = new DiscordEventHandler();
            params.registerEventHandler(handler);

            try {
                discordRPC = new Core(params);

                running = true;
                Thread callBacks = new Thread(() -> {
                    while (enabled.get() && running) {

                        discordRPC.runCallbacks();

                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    discordRPC.close();
                    Thread.currentThread().interrupt();
                });
                callBacks.start();
                Logger.info("Started RPC Core");
            } catch (Exception e) {
                if(!e.getMessage().contains("INTERNAL_ERROR")) {
                    Logger.error("An error occured: ");
                    e.printStackTrace();
                } else {
                    enabled.set(false);
                }
            }
        }
    }

    public void updateActivity() {

        Activity activity = new Activity();

        String state = MinecraftClient.getInstance().world == null ?
                "In the menu" : (MinecraftClient.getInstance().getCurrentServerEntry() == null ?
                "Singleplayer" : MinecraftClient.getInstance().getCurrentServerEntry().address);

        if (showActivity.get() && MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            activity.setDetails(Util.getGame());
        } else if (showActivity.get() && currentActivity!=null && MinecraftClient.getInstance().world != null){
            activity.setDetails(currentActivity.getDetails());
        } else if (!showActivity.get() && currentActivity != null && currentActivity.getDetails().equals("")) {
            currentActivity.setDetails("");
        }

        activity.setState(state);
        activity.setType(ActivityType.PLAYING);

        if (showTime.get()) {
            activity.timestamps().setStart(Instant.ofEpochMilli(time.toEpochMilli()));
        }

        if(currentActivity != null) {
            currentActivity.close();
        }

        activity.assets().setLargeText("AxolotlClient " + MinecraftClient.getInstance().getGameVersion());
        activity.assets().setLargeImage("icon");
        discordRPC.activityManager().updateActivity(activity);
        currentActivity = activity;
    }

    public static void setWorld(String world){
        if(running) {
            if (currentActivity == null) {
                DiscordRPC.getInstance().updateRPC();
            }
            currentActivity.setDetails("World: " + world);
            if (discordRPC.isOpen()) {
                discordRPC.activityManager().updateActivity(currentActivity);
            }
        }
    }

    public void updateRPC(){

        if(discordRPC != null && discordRPC.isOpen()) {
            updateActivity();
        }
    }

    public void tick(){

        if (!running && enabled.get()){
            initRPC();
        }

        if(running) {
            updateRPC();
        }

    }

    public static void shutdown(){
        running = false;
    }
}
