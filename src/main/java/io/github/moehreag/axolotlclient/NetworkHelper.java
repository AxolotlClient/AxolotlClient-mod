package io.github.moehreag.axolotlclient;

import net.minecraft.client.MinecraftClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.UUID;


public class NetworkHelper {

    public static boolean getOnline(UUID uuid){

        if (Axolotlclient.onlinePlayers.contains(uuid.toString())){
            return true;
        } else if (Axolotlclient.otherPlayers.contains(uuid.toString())){
            return false;
        }else {
            final Thread get = new Thread(() -> getUser(uuid));
            get.start();
            return Axolotlclient.onlinePlayers.contains(uuid.toString());
        }
    }

    public static void getUser(UUID uuid){
        try{
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet("https://moehreag.duckdns.org/axolotlclient-api/?uuid="+uuid.toString());
            HttpResponse response= client.execute(get);
            client.close();
            if (response.getEntity().getContent().toString().contains("true")){
                Axolotlclient.onlinePlayers  = Axolotlclient.onlinePlayers + " " + uuid;
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }

        Axolotlclient.otherPlayers = Axolotlclient.otherPlayers + " " + uuid.toString();
    }

    public static void setOnline() {

        try {
            UUID uuid = MinecraftClient.getInstance().player.getUuid();

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://moehreag.duckdns.org/axolotlclient-api/");
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity("{\n\t\"uuid\": \""+uuid.toString()+"\",\n\t\"online\": true\n}"));
            HttpResponse response = client.execute(post);

            if(!response.getEntity().getContent().toString().contains("Success!")){
                Axolotlclient.LOGGER.info("Sucessfully logged in at Axolotlclient!");
            }
            client.close();
        } catch (Exception e) {Axolotlclient.LOGGER.error("Error while logging in!");}
    }

    public static void setOffline(){



        try{
            UUID uuid = MinecraftClient.getInstance().player.getUuid();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpDelete delete = new HttpDelete("https://moehreag.duckdns.org/axolotlclient-api/?uuid="+uuid.toString());
            HttpResponse response= client.execute(delete);
            client.close();
            if (response.getEntity().getContent().toString().contains("Success!")){
                Axolotlclient.LOGGER.info("Successfully logged off!");
            }

        } catch (Exception ex){
            Axolotlclient.LOGGER.error("Error while logging off!");
        }

    }

}
