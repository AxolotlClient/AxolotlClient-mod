package io.github.moehreag.branding;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;


public class NetworkHelper {

	public static boolean getOnline(UUID uuid){

		if (Axolotlclient.onlinePlayers.contains(uuid.toString())){
			return true;
		}
		return getUser(uuid);
	}

	public static boolean getUser(UUID uuid){

		try{
			URL url = new URL("https://moehreag.duckdns.org/axolotlclient-api?uuid="+uuid);
			HttpsURLConnection client = (HttpsURLConnection) url.openConnection();

			System.out.println("RETURN : "+client.getResponseCode());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;


				while ((line = in.readLine()) != null) {
					response.append(line).append("\n");
				}
				if (response.toString().contains("\"online\":true")){return true;}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}

	public static void setOnline(){

		UUID uuid = MinecraftClient.getInstance().player.getUuid();

		String jsonInputString = "{\"uuid\": "+uuid+", \"online\": true}";

		try{
			URL url = new URL("https://moehreag.duckdns.org/axolotlclient-api");
			HttpsURLConnection client = (HttpsURLConnection) url.openConnection();

			client.setRequestMethod("POST");
			client.setRequestProperty("Content-Type", "application/json; utf-8");
			client.setRequestProperty("Accept", "application/json");
			client.setDoOutput(true);

			System.out.println("RETURN : "+client.getResponseCode());
		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

	public static void setOffline(){

		UUID uuid = MinecraftClient.getInstance().player.getUuid();

		try{
			URL url = new URL("https://moehreag.duckdns.org/axolotlclient-api?uuid="+uuid);
			HttpsURLConnection client = (HttpsURLConnection) url.openConnection();

			client.setRequestMethod("POST");
			client.setRequestProperty("Content-Type", "application/json; utf-8");
			client.setRequestProperty("Accept", "application/json");
			client.setDoOutput(true);

			System.out.println("RETURN : "+client.getResponseCode());
		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

}
