/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.util;

import com.google.gson.JsonElement;
import io.github.axolotlclient.AxolotlClient;
import net.minecraft.client.MinecraftClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkHelper {

    private static boolean loggedIn;
    private static UUID uuid;

    private static final AtomicInteger concurrentCalls = new AtomicInteger(0);
    private static final int maxCalls = 3;

    public static boolean getOnline(UUID uuid) {
        if (!AxolotlClient.playerCache.containsKey(uuid)) {
            if (concurrentCalls.get() <= maxCalls) {
                concurrentCalls.incrementAndGet();
                Runnable runnable = () -> getUser(uuid);
                ThreadExecuter.scheduleTask(runnable);
                ThreadExecuter.removeTask(runnable);
                ThreadExecuter.scheduleTask(concurrentCalls::decrementAndGet, 1, TimeUnit.MINUTES);
            }
        }
        return AxolotlClient.playerCache.get(uuid) != null ? AxolotlClient.playerCache.get(uuid) : false;
    }

    public static void getUser(UUID uuid) {
        try {
            String body = NetworkUtil.getRequest("https://moehreag.duckdns.org/axolotlclient-api/?uuid=" + uuid.toString(), HttpClients.createMinimal()).toString();
            if (body.contains("true")) {
                AxolotlClient.playerCache.put(uuid, true);
            } else {
                AxolotlClient.playerCache.put(uuid, false);
            }
        } catch (Exception ignored) {
            AxolotlClient.playerCache.put(uuid, false);
        }
    }

    public static void setOnline() {

        if(uuid == null){
            try {

                uuid = MinecraftClient.getInstance().player.getUuid();
            } catch (NullPointerException ignored){}
        }

        if(uuid != null) {
            try {
                String body = NetworkUtil.postRequest("https://moehreag.duckdns.org/axolotlclient-api/", "{\n\t\"uuid\": \"" + uuid + "\",\n\t\"online\": true\n}", HttpClients.createMinimal()).toString();
                if (body.contains("Success!")) {
                    AxolotlClient.LOGGER.info("Sucessfully logged in at AxolotlClient!");
                    loggedIn = true;
                }
            } catch (Exception e) {
                AxolotlClient.LOGGER.error("Error while logging in!");
            }
        }
    }

    public static void setOffline() {
        if (loggedIn) {
            try {
                AxolotlClient.LOGGER.info("Logging off..");
                String body = NetworkUtil.deleteRequest("https://moehreag.duckdns.org/axolotlclient-api/?uuid=" + uuid.toString(), "", HttpClients.createMinimal()).getAsString();
                if (body.contains("Success!")) {
                    AxolotlClient.LOGGER.info("Successfully logged off!");
                } else {
                    throw new Exception("Error while logging off: " + body);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                AxolotlClient.LOGGER.error("Error while logging off!");
            }
        }
    }

    // In case we ever implement more of HyCord's features...
    public static JsonElement getRequest(String site) {
        return getRequest(site, HttpClients.custom().disableAutomaticRetries().build());
    }

    public static JsonElement getRequest(String url, CloseableHttpClient client){
        try {
            return NetworkUtil.getRequest(url, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static String getUuid(String username) {
        JsonElement response = getRequest("https://api.mojang.com/users/profiles/minecraft/" + username);
        if (response == null)
            return null;
        return response.getAsJsonObject().get("id").getAsString();
    }

    public static BufferedImage getImage(String imgUrl) {
        try (CloseableHttpClient client = HttpClients.custom().disableAutomaticRetries().build()) {

            HttpGet get = new HttpGet(imgUrl);
            HttpResponse response = client.execute(get);

            client.close();
            return ImageIO.read(response.getEntity().getContent());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
