package io.github.moehreag.branding;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class NetworkHelper {



	private HttpsURLConnection getHttpsClient(String url) throws Exception {

		// Security section START
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}



				@Override
				public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				}



				@Override
				public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				}

			}};



		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Security section END



		HttpsURLConnection client = (HttpsURLConnection) new URL(url).openConnection();
		//add request header

		client.setRequestProperty("User-Agent",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
		return client;

	}



	private boolean Get(UUID uuid) throws Exception {

		System.out.println("*** Sending GET ***");

		String url = "http://moehreag.duckdns.org:5000/axolotlclient-api/"+uuid;
		HttpsURLConnection client = getHttpsClient(url);

		int responseCode = client.getResponseCode();
		System.out.println("GET request to URL: " + url);
		System.out.println("Response Code     : " + responseCode);

		try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
			StringBuilder response = new StringBuilder();
			String line;



			while ((line = in.readLine()) != null) {
				response.append(line).append("\n");
			}

			System.out.println(response.toString());

			if ("true" == response.substring(2)){
				return true;
			} else {return false;}
		}
	}



	private void Put(UUID uuid, Boolean state) throws Exception {

		System.out.println("*** Sending Http PUT ***");
		String url = "https://moehreag.duckdns.org:5000/axolotlclient-api/"+uuid.toString();
		String urlParameters = "data="+state;
		byte[] putData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int putDataLength = putData.length;



		HttpsURLConnection client = getHttpsClient(url);
		client.setRequestMethod("PUT");
		client.setDoOutput(true);
		client.setInstanceFollowRedirects(false);
		client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		client.setRequestProperty("charset", "utf-8");
		client.setRequestProperty("Content-Length", Integer.toString(putDataLength));
		client.setUseCaches(false);



		try (OutputStream os = client.getOutputStream()) {
			os.write(putData);
		}



		int responseCode = client.getResponseCode();
		System.out.println("PUT request to URL: " + url);
		System.out.println("PUT Parameters    : " + urlParameters);
		System.out.println("Response Code      : " + responseCode);



		try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
			String line;
			StringBuilder response = new StringBuilder();



			while ((line = in.readLine()) != null) {
				response.append(line).append("\n");
			}
			System.out.println(response.toString());

		}

	}



	public static boolean getOnline(UUID uuid) throws Exception {

		//NetworkHelper obj = new NetworkHelper();
		return true;//obj.Get(uuid);
	}

	public static void setOnline(){
		NetworkHelper obj = new NetworkHelper();
		try {
			obj.Put(MinecraftClient.getInstance().player.getUuid(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setOffline(){
		NetworkHelper obj = new NetworkHelper();
		try {
			assert MinecraftClient.getInstance().player != null;
			//obj.Put(MinecraftClient.getInstance().player.getUuid(), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
