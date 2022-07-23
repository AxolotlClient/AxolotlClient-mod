package io.github.axolotlclient;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;


public class NetworkHelper {

    private static boolean loggedIn;
    private static UUID uuid;

    public static boolean getOnline(UUID uuid){

        if (AxolotlClient.onlinePlayers.contains(uuid.toString())){
            return true;
        } else if (AxolotlClient.otherPlayers.contains(uuid.toString())){
            return false;
        }else {
            ThreadExecuter.scheduleTask(() -> getUser(uuid));
            return AxolotlClient.onlinePlayers.contains(uuid.toString());
        }
    }

    public static void getUser(UUID uuid){
        try{
            CloseableHttpClient client = HttpClients.custom().disableAutomaticRetries().build();
            HttpGet get = new HttpGet("https://moehreag.duckdns.org/axolotlclient-api/?uuid="+uuid.toString());
            HttpResponse response= client.execute(get);
            String body = EntityUtils.toString(response.getEntity());
            client.close();
            if (body.contains("true")){
                AxolotlClient.onlinePlayers  = AxolotlClient.onlinePlayers + " " + uuid;
            } else {
                AxolotlClient.otherPlayers = AxolotlClient.otherPlayers + " " + uuid;
            }

        } catch (Exception ignored){
            AxolotlClient.otherPlayers = AxolotlClient.otherPlayers + " " + uuid;
        }


    }

    public static void setOnline() {

        try {
            uuid = MinecraftClient.getInstance().player.getUuid();

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://moehreag.duckdns.org/axolotlclient-api/");
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity("{\n\t\"uuid\": \""+uuid.toString()+"\",\n\t\"online\": true\n}"));
            HttpResponse response = client.execute(post);
            String body = EntityUtils.toString(response.getEntity());
            if(body.contains("Success!")){
                AxolotlClient.LOGGER.info("Sucessfully logged in at AxolotlClient!");
                loggedIn=true;
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            AxolotlClient.LOGGER.error("Error while logging in!");
        }
    }

    public static void setOffline(){

        if(loggedIn) {
            try {
                AxolotlClient.LOGGER.info("Logging off..");
                CloseableHttpClient client = HttpClients.createDefault();
                HttpDelete delete = new HttpDelete("https://moehreag.duckdns.org/axolotlclient-api/?uuid=" + uuid.toString());
                delete.setHeader("Accept", "application/json");
                delete.setHeader("Content-type", "application/json");
                HttpResponse response = client.execute(delete);
                String body = EntityUtils.toString(response.getEntity());
                if (body.contains("Success!")) {
                    AxolotlClient.LOGGER.info("Successfully logged off!");
                } else {
                    throw new Exception("Error while logging off: " + body);
                }
                client.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                AxolotlClient.LOGGER.error("Error while logging off!");
            }
        }
    }

    public static JsonElement getRequest(String site) {
        try {

            CloseableHttpClient client = HttpClients.custom().disableAutomaticRetries().build();
            HttpGet get = new HttpGet(site);
            HttpResponse response = client.execute(get);

            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                AxolotlClient.LOGGER.warn("API request failed, status code " + status);
                return null;
            }

            String body = EntityUtils.toString(response.getEntity());
            client.close();

            JsonParser parser = new JsonParser();
            return parser.parse(new StringReader(body));

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
    }

}
