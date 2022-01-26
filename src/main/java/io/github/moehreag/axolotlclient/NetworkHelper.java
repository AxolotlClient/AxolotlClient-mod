package io.github.moehreag.axolotlclient;

import net.minecraft.client.MinecraftClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;


public class NetworkHelper {

	public static boolean getOnline(UUID uuid){

		if (Axolotlclient.onlinePlayers.contains(uuid.toString())){
			return true;
		} else if (Axolotlclient.otherPlayers.contains(uuid.toString())){
			return false;
		}else {
			final Thread get = new Thread(() -> {
				while (!Thread.interrupted()) {
					getUser(uuid);
					break;
				}
				});
			get.start();
			return Axolotlclient.onlinePlayers.contains(uuid.toString());
		}
	}

	public static void getUser(UUID uuid){


			try{


				final HttpClient client = HttpClient.newBuilder().build();
				HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api/?uuid="+uuid))
					.build();

				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

				if (response.body().contains("true")){
					Axolotlclient.onlinePlayers  = Axolotlclient.onlinePlayers + " " + uuid;
				}

			} catch (Exception ex){
				ex.printStackTrace();
			}

		Axolotlclient.otherPlayers = Axolotlclient.otherPlayers + " " + uuid.toString();
	}

	public static void setOnline() {

		UUID uuid = Objects.requireNonNull(MinecraftClient.getInstance().player).getUuid();

		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api/"))
				.header("Content-Type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString("{\n\t\"uuid\": \""+uuid.toString()+"\",\n\t\"online\": true\n}"))
				.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			//System.out.println(response.body());
			if(!response.body().contains("Success!")){
				System.out.println("Sucessfully logged in at Axolotlclient!");
			}
		} catch (Exception e) {System.out.println("Exception");}
	}

	public static void setOffline(){

		UUID uuid = Objects.requireNonNull(MinecraftClient.getInstance().player).getUuid();

		try{
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api/?uuid="+uuid))
				.method("DELETE", HttpRequest.BodyPublishers.noBody())
				.build();

			HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

}
