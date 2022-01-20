package io.github.moehreag.branding;

import net.minecraft.client.MinecraftClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class NetworkHelper {

	public static int maxRequests = 3;
	public static int requests;

	public static boolean getOnline(UUID uuid){

		if (Axolotlclient.onlinePlayers.contains(uuid.toString())){
			return true;
		} else if (Axolotlclient.otherPlayers.contains(uuid.toString())){
			return false;
		}else {
			new Thread(() -> {
				getUser(uuid);
				return;
			}).start();
			boolean online = Axolotlclient.onlinePlayers.contains(uuid.toString()) ? true : false;
			return online;
		}
			//return getUser(uuid);}
	}



	public static boolean getUser(UUID uuid){


			try{


				final HttpClient client = HttpClient.newBuilder().build();
				HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api/?uuid="+uuid))
					.build();

				CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

				String body = response.thenApplyAsync(HttpResponse::body).get();


				if (body.contains("true")){    //(response.toString().contains("true")){
					Axolotlclient.onlinePlayers  = Axolotlclient.onlinePlayers + " " + uuid;
					//System.out.println(Axolotlclient.onlinePlayers);
					return true;
				}

			} catch (Exception ex){
				ex.printStackTrace();
			}

		Axolotlclient.otherPlayers = Axolotlclient.otherPlayers + " " + uuid.toString();
		//System.out.println("Other Players: "+Axolotlclient.otherPlayers);
		return false;
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
				.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api?uuid="+uuid))
				.method("DELETE", HttpRequest.BodyPublishers.noBody())
				.build();

			HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

}
