package io.github.moehreag.axolotlclient.modules.levelhead;

import io.github.moehreag.axolotlclient.Axolotlclient;
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
 * https://github.com/Intro-Dev/Osmium
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
        API_KEY = LevelHead.getInstance().hypixel_api_key.get();
        if(API_KEY == null){
            LevelHead.getInstance().hypixel_api_key.setDefaults();
            validApiKey=false;
            return;
        }
        if(!Objects.equals(API_KEY, "")) {
            api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(API_KEY)));
            validApiKey = true;
        } else {
            validApiKey = false;
        }
    }

    public static boolean hasValidAPIKey() {
        return validApiKey;
    }

    public static int getPlayerLevel(String uuid) {
        if(loadPlayerDataIfAbsent(uuid)) {
            try {
                return (int) cachedPlayerData.get(uuid).get(1, TimeUnit.MICROSECONDS).getPlayer().getNetworkLevel();
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                return -1;
            }
        }
        return 0;
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
