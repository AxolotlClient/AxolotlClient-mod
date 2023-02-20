package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.handlers.FriendRequestAcceptedHandler;
import io.github.axolotlclient.api.handlers.FriendRequestHandler;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.notifications.NotificationProvider;
import io.github.axolotlclient.util.translation.TranslationProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import lombok.Getter;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyContainerProvider;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class API {

	private static final String API_BASE = "wss://axo.gart.sh";
	private static final String API_URL = API_BASE + "/api/ws";
	@Getter
	private static API Instance;
	private final HashMap<String, Request> requests = new HashMap<>();
	private final Set<RequestHandler> handlers = new HashSet<>();
	@Getter
	private final Logger logger;
	@Getter
	private final NotificationProvider notificationProvider;
	@Getter
	private final TranslationProvider translationProvider;
	private Session session;
	@Getter
	private String uuid;

	public API(Logger logger, NotificationProvider notificationProvider, TranslationProvider translationProvider) {
		this.logger = logger;
		this.notificationProvider = notificationProvider;
		this.translationProvider = translationProvider;
		Instance = this;
		addHandler(new FriendRequestHandler());
		addHandler(new FriendRequestAcceptedHandler());
	}

	public boolean isConnected() {
		return session != null && session.isOpen();
	}

	public void addHandler(RequestHandler handler) {
		handlers.add(handler);
	}

	public void startup(String uuid) {
		try {
			if (session == null || !session.isOpen()) {
				this.uuid = uuid;
				logger.debug("Starting API...");
				session = GrizzlyContainerProvider.getWebSocketContainer().connectToServer(ClientEndpoint.class, URI.create(API_URL));
			} else {
				logger.warn("API is already running!");
			}
		} catch (IOException | DeploymentException e) {
			logger.error("Error while starting API!", e);
		}
	}

	public void onOpen(Session session) {
		this.session = session;
		logger.debug("API connected!");
		sendHandshake(uuid);
	}

	private void sendHandshake(String uuid) {
		logger.debug("Starting Handshake");
		Request request = new Request("handshake", object -> {
			if (requestFailed(object)) {
				logger.error("Handshake failed, closing API!");
				notificationProvider.addStatus("api.error.handshake", APIError.fromResponse(object));
				shutdown();
			} else {
				logger.debug("Handshake successful!");
				notificationProvider.addStatus("api.success.handshake", "api.success.handshake.desc");
			}
		}, "uuid", sanitizeUUID(uuid));
		send(request);
	}

	public boolean requestFailed(JsonObject object) {
		return !object.has("type") || (object.has("type") && object.get("type").getAsString().equals("error"));
	}

	public void shutdown() {
		try {
			if (session != null && session.isOpen()) {
				session.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String sanitizeUUID(String uuid) {
		if (uuid.contains("-")) {
			return uuid.replace("-", "");
		}
		return uuid;
	}

	public void send(Request request) {
		if (session.isOpen()) {
			requests.put(request.getId(), request);
			String text = request.getJson();
			try {
				session.getBasicRemote().sendText(text);
			} catch (IOException e) {
				logger.error("Failed to send Request! Request: ", text, e);
			}
		}
	}

	public void onMessage(String message) {
		handleResponse(message);
	}

	public void onError(Throwable throwable) {
		logger.error("Error while handling API traffic!", throwable);
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
}
