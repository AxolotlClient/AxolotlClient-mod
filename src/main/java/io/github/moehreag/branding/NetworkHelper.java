package io.github.moehreag.branding;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;


public class NetworkHelper {

	public static boolean getOnline(UUID uuid){

		if (Axolotlclient.onlinePlayers.contains(uuid.toString())){
			return true;
		} else if (Axolotlclient.otherPlayers.contains(uuid.toString())){
			return false;
		}else {return getUser(uuid);}
	}

	public static boolean getUser(UUID uuid){

		try{
			URL url = new URL("https://moehreag.duckdns.org/axolotlclient-api?uuid="+uuid);
			HttpsURLConnection client = (HttpsURLConnection) url.openConnection();

			System.out.println("RETURN : "+client.getRequestMethod() + client.getResponseCode());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;


				while ((line = in.readLine()) != null) {
					response.append(line).append("\n");
				}
				System.out.println(response);
				if (response.toString().contains("\"online\":true")){
					Axolotlclient.onlinePlayers  = Axolotlclient.onlinePlayers + " " + uuid.toString();
					System.out.println(Axolotlclient.onlinePlayers);

					return true;
				}
			}
			System.out.println("Online Players: "+ Axolotlclient.onlinePlayers);
			System.out.println(Axolotlclient.onlinePlayers);
		} catch (Exception ex){
			ex.printStackTrace();
		}

		Axolotlclient.otherPlayers = Axolotlclient.otherPlayers + " " + uuid.toString();
		System.out.println("Other Players: "+Axolotlclient.otherPlayers);
		return false;
	}

	public static void setOnline() {

		UUID uuid = MinecraftClient.getInstance().player.getUuid();

		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api/"))
				.header("Content-Type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString("{\n\t\"uuid\": \""+uuid.toString()+"\",\n\t\"online\": true\n}"))
				.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			//System.out.println(response.body());
			if(!response.body().contains("Success!")){
			}
		} catch (Exception e) {System.out.println("Exception");}
	}

	public static void setOffline(){

		UUID uuid = MinecraftClient.getInstance().player.getUuid();

		try{
			URL url = new URL("https://moehreag.duckdns.org/axolotlclient-api?uuid="+uuid);
			HttpsURLConnection client = (HttpsURLConnection) url.openConnection();

			client.setRequestMethod("DELETE");
			client.setRequestProperty("Content-Type", "application/json; utf-8");
			client.setRequestProperty("Accept", "application/json");
			client.setDoOutput(true);

			//System.out.println("RETURN : "+client.getResponseCode());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;


				while ((line = in.readLine()) != null) {
					response.append(line).append("\n");
				}
				//System.out.println(response);
			}

		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

}
