package io.github.moehreag.axolotlclient.modules.hypixel;

import io.github.moehreag.axolotlclient.config.options.enumOptions.LevelHeadOption;
import io.github.moehreag.axolotlclient.modules.hypixel.levelhead.LevelHead;
import io.github.moehreag.axolotlclient.util.ThreadExecuter;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.reply.PlayerReply;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Based on Osmium by Intro-Dev
 * <a href="https://github.com/Intro-Dev/Osmium">https://github.com/Intro-Dev/Osmium</a>
 * License: CC0-1.0
 *
 * Provides a layer between the hypixel api and the client to obtain information with minimal api calls
 */
public class HypixelAbstractionLayer {

    private static String API_KEY;

    private static final HashMap<String, CompletableFuture<PlayerReply>> cachedPlayerData = new HashMap<>();

    private static HypixelAPI api;

    private static boolean validApiKey = false;

    private static final AtomicInteger hypixelApiCalls = new AtomicInteger(0);


    public static void loadApiKey() {
        API_KEY = HypixelMods.getInstance().hypixel_api_key.get();
        if(API_KEY == null){
            return;
        }
        if(!Objects.equals(API_KEY, "")) {
            try {
                api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(API_KEY)));
                validApiKey = true;
            } catch (Exception ignored){
                validApiKey = false;
            }
        } else {
            validApiKey = false;
        }
    }

    public static boolean hasValidAPIKey() {
        return validApiKey;
    }

    public static int getPlayerLevel(String uuid) {
        if(api == null){
            loadApiKey();
        }
        if(loadPlayerDataIfAbsent(uuid)) {
            try {
                Enum<?> mode = LevelHead.getInstance().mode.get();
                if(mode == LevelHeadOption.LevelHeadMode.NETWORK){
                    return (int) cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer().getNetworkLevel();
                } else if (mode == LevelHeadOption.LevelHeadMode.BEDWARS){
                    return cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer().getIntProperty("achievements.bedwars_level", 0);
                } else if (mode == LevelHeadOption.LevelHeadMode.SKYWARS){
                    String formattedLevel = cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer().getStringProperty("stats.SkyWars.levelFormatted", "§70⋆");
                    return Integer.parseInt(formattedLevel.substring(2, formattedLevel.length()-1));
                }
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                return -1;
            }
        }
        return 0;
    }

    public static void clearPlayerData(){
        cachedPlayerData.clear();
    }

    private static boolean loadPlayerDataIfAbsent(String uuid) {
        if(cachedPlayerData.get(uuid) == null) {
            // set at 115 to have a buffer in case of disparity between threads
            if(hypixelApiCalls.get() <= 115) {
                cachedPlayerData.put(uuid, api.getPlayerByUuid(uuid));
                hypixelApiCalls.incrementAndGet();
                ThreadExecuter.scheduleTask(hypixelApiCalls::decrementAndGet, 1, TimeUnit.MINUTES);
                return true;
            }
            return false;
        }
        return true;
    }



    private static void freePlayerData(String uuid) {
        cachedPlayerData.remove(uuid);
    }

    public static void handleDisconnectEvents(UUID uuid) {
        freePlayerData(uuid.toString());
    }

}
