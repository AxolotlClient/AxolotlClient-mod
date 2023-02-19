package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.Logger;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class API {

	private static final String API_BASE = "https://axo.gart.sh";
	private static final String API_URL = API_BASE+"/api/ws";
	@Getter
	private static API Instance;
	private final HashMap<String, Request> requests = new HashMap<>();
	private final Set<RequestHandler> handlers = new HashSet<>();
	private final Logger logger;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public API(Logger logger) {
		this.logger = logger;
		Instance = this;
	}

	public void addHandler(RequestHandler handler) {
		handlers.add(handler);
	}

	public void startup(String uuid) {
		try {
			logger.debug("Starting API...");
			socket = new Socket(API_URL, 433);
			socket.setKeepAlive(true);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			new Thread("API response thread") {
				@Override
				public void run() {
					while (!socket.isClosed()) {
						try {
							int c = in.read();
							if (c != -1) {
								StringBuilder response = new StringBuilder();
								response.append((char) c);
								while ((c = (char) in.read()) != 0) {
									response.append(c);
								}
								handleResponse(response.toString());
							}
						} catch (IOException ignored) {

						}
					}
				}
			}.start();

			sendHandshake(uuid);
		} catch (IOException e) {
			logger.error("Error while starting API!", e);
		}
	}

	private void handleResponse(String response) {
		try {
			JsonObject object = GsonHelper.GSON.fromJson(response, JsonObject.class);

			String id = object.get("id").getAsString();
			if (requests.containsKey(id)) {
				requests.get(id).getHandler().accept(object);
				requests.remove(id);

			} else if (id == null) {
				handlers.stream().filter(handler -> handler.isApplicable(object)).forEach(handler -> handler.handle(object));
			} else {
				logger.error("Unknown response: " + response);
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error("Invalid response: " + response);
		}
	}

	private void sendHandshake(String uuid) {
		/*id = "client-"+ UUID.randomUUID().toString().split("-")[0]+"-handshake";
		JsonObject handshake = new JsonObject();
		handshake.add("id", new JsonPrimitive(id));
		handshake.add("type", new JsonPrimitive("handshake"));
		JsonObject data = new JsonObject();
		data.add("uuid", new JsonPrimitive(uuid));
		handshake.add("data", data);*/
		logger.debug("Starting Handshake");
		Request request = new Request("handshake", System.out::println, new Request.Data("uuid", uuid.replace("-", "")));
		send(request);
	}

	public void send(Request request) {
		if(!socket.isClosed()) {
			requests.put(request.getId(), request);
			out.println(request.getJson());
		}
	}

	public void shutdown() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
