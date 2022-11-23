/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient;

import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.ThreadExecuter;
import net.minecraft.client.MinecraftClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
            CloseableHttpClient client = HttpClients.createMinimal();
            HttpGet get = new HttpGet("https://moehreag.duckdns.org/axolotlclient-api/?uuid=" + uuid.toString());
            HttpResponse response = client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            client.close();
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
        if (uuid == null) {
            try {
                uuid = MinecraftClient.getInstance().player.getUuid();
            } catch (NullPointerException ignored) {}
        }
        if (uuid != null) {
            try {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = new HttpPost("https://moehreag.duckdns.org/axolotlclient-api/");
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                post.setEntity(new StringEntity("{\n\t\"uuid\": \"" + uuid.toString() + "\",\n\t\"online\": true\n}"));
                HttpResponse response = client.execute(post);
                String body = EntityUtils.toString(response.getEntity());
                if (body.contains("Success!")) {
                    Logger.info("Sucessfully logged in at AxolotlClient!");
                    loggedIn = true;
                }
                client.close();
            } catch (Exception e) {
                //e.printStackTrace();
                Logger.error("Error while logging in!");
            }
        }
    }

    public static void setOffline() {
        if (loggedIn) {
            try {
                Logger.info("Logging off..");
                CloseableHttpClient client = HttpClients.createDefault();
                HttpDelete delete = new HttpDelete(
                        "https://moehreag.duckdns.org/axolotlclient-api/?uuid=" + uuid.toString());
                delete.setHeader("Accept", "application/json");
                delete.setHeader("Content-type", "application/json");
                HttpResponse response = client.execute(delete);
                String body = EntityUtils.toString(response.getEntity());
                if (body.contains("Success!")) {
                    Logger.info("Successfully logged off!");
                } else {
                    throw new Exception("Error while logging off: " + body);
                }
                client.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.error("Error while logging off!");
            }
        }
    }

    // In case we ever implement more of HyCord's features...
    /*public static JsonElement getRequest(String site) {
        try {

            CloseableHttpClient client = HttpClients.custom().disableAutomaticRetries().build();
            HttpGet get = new HttpGet(site);
            HttpResponse response = client.execute(get);

            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                Logger.warn("API request failed, status code " + status);
                return null;
            }

            String body = EntityUtils.toString(response.getEntity());
            client.close();

            JsonParser parser = new JsonParser();
            return parser.parse(body);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUuid(String username) {
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
