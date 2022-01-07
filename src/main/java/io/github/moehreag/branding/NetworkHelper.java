package io.github.moehreag.branding;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;


public class NetworkHelper {

	public static boolean getOnline(UUID uuid){

		if (Axolotlclient.onlinePlayers.contains(uuid.toString())){
			return true;
		} else if (Axolotlclient.otherPlayers.contains(uuid.toString())){
			return false;
		}
		return getUser(uuid);
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
					Axolotlclient.onlinePlayers.concat(uuid.toString());

					return true;
				}
			}
			System.out.println("Online Players: "+ Axolotlclient.onlinePlayers);
			System.out.println(Axolotlclient.onlinePlayers);
		} catch (Exception ex){
			ex.printStackTrace();
		}

		Axolotlclient.otherPlayers.concat(uuid.toString());
		System.out.println("Other Players: "+Axolotlclient.otherPlayers);
		return false;
	}

	public static void setOnline(){




		final String USER_AGENT = "Mozilla/5.0 (X11; Linux 3.5.4-1-ARCH i686; es) KHTML/4.9.1 (like Gecko) Konqueror/4.9";

		UUID uuid = MinecraftClient.getInstance().player.getUuid();

		String jsonInputString = "{\"uuid\": "+uuid+", \"online\": \"true\"}";


		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://moehreag.duckdns.org/axolotlclient-api/"))
				.header("Content-Type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString("{\n\t\"uuid\": "+uuid+",\n\t\"online\": true\n}"))
				.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (Exception e){System.out.println("Exception");}
		/*

		try{
			URL url = new URL("https://moehreag.duckdns.org/axolotlclient-api?login=true");
			byte[] putData = jsonInputString.getBytes(StandardCharsets.UTF_8);
			int putDataLength = putData.length;

			HttpsURLConnection client = (HttpsURLConnection) url.openConnection();

			client.setRequestMethod("POST");
			client.setRequestProperty("Content-Type", "application/json; utf-8");
			client.setRequestProperty("Accept", "application/json");
			//client.setRequestProperty("User-Agent", USER_AGENT);
			client.setDoOutput(true);
			//client.setRequestProperty("Content-Length", Integer.toString(putDataLength));
			client.setUseCaches(false);


			client.getOutputStream().write(putData);

			client.getOutputStream().close();

			client.connect();

			System.out.println("RETURN : "+client.getRequestMethod() +  client.getResponseCode());
			//System.out.println(client.getContent());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;


				while ((line = in.readLine()) != null) {
					response.append(line).append("\n");
				}
				System.out.println(response);
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}

		try {
			postUser(uuid);
		}
		catch (Exception e){
			e.printStackTrace();
		}*/

	}

	public static boolean postUser(UUID uuid) throws IOException {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			HttpPost httpPost = new HttpPost("https://moehreag.duckdns.org/axolotlclient-api?login=true");
			List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("uuid", uuid.toString()));
			nvps.add(new BasicNameValuePair("online", "true"));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				System.out.println(response.getCode() + " " + response.getReasonPhrase());
				HttpEntity entity2 = response.getEntity();

				try (BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));) {
					// Read in all of the post results into a String.
					String output = "";
					Boolean keepGoing = true;
					while (keepGoing) {
						String currentLine = br.readLine();

						if (currentLine == null) {
							keepGoing = false;
						} else {
							output += currentLine;
						}
					}
					System.out.println(output);

					//System.out.println(entity2.getContent());

					if (entity2.toString().contains("true")) {
						return true;
					}
					// do something useful with the response body
					// and ensure it is fully consumed
					EntityUtils.consume(entity2);
				}
			}
			return false;
		}
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

			System.out.println("RETURN : "+client.getResponseCode());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;


				while ((line = in.readLine()) != null) {
					response.append(line).append("\n");
				}
				System.out.println(response);
			}

		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

}
