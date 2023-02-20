package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.handlers.FriendHandler;
import io.github.axolotlclient.api.handlers.FriendRequestAcceptedHandler;
import io.github.axolotlclient.api.handlers.FriendRequestHandler;
import io.github.axolotlclient.api.util.RequestHandler;
import io.github.axolotlclient.api.util.StatusUpdateProvider;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.ThreadExecuter;
import io.github.axolotlclient.util.notifications.NotificationProvider;
import io.github.axolotlclient.util.translation.TranslationProvider;
import jakarta.websocket.CloseReason;
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
	private static final URI API_URL = URI.create(API_BASE + "/api/ws");
	private static final int STATUS_UPDATE_DELAY = 1; // The Delay between Status updates, in seconds.

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
	private final StatusUpdateProvider statusUpdateProvider;
	private Session session;
	@Getter
	private String uuid;

	public API(Logger logger, NotificationProvider notificationProvider, TranslationProvider translationProvider, StatusUpdateProvider statusUpdateProvider) {
		this.logger = logger;
		this.notificationProvider = notificationProvider;
		this.translationProvider = translationProvider;
		this.statusUpdateProvider = statusUpdateProvider;
		Instance = this;
		addHandler(new FriendRequestHandler());
		addHandler(new FriendRequestAcceptedHandler());
		addHandler(FriendHandler.getInstance());
	}

	public boolean isConnected() {
		return session != null && session.isOpen();
	}

	public void addHandler(RequestHandler handler) {
		handlers.add(handler);
	}

	private Session createSession(){
		try {
			return GrizzlyContainerProvider.getWebSocketContainer().connectToServer(ClientEndpoint.class,  API_URL);
		} catch (DeploymentException | IOException e) {
			logger.error("Error while starting API!", e);
			return null;
		}
	}

	public void startup(String uuid) {
		if (session == null || !session.isOpen()) {
			this.uuid = sanitizeUUID(uuid);
			logger.debug("Starting API...");
			session = createSession();

			new Thread("Status Update Thread"){
				@Override
				public void run() {
					try {
						Thread.sleep(50);
					} catch (InterruptedException ignored) {
					}
					while (API.getInstance().isConnected()) {
						send(statusUpdateProvider.getStatus());
						try {
							//noinspection BusyWait
							Thread.sleep(STATUS_UPDATE_DELAY*1000);
						} catch (InterruptedException ignored) {

						}
					}
				}
			}.start();
		} else {
			logger.warn("API is already running!");
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
		}, "uuid", uuid);
		send(request);
	}

	public boolean requestFailed(JsonObject object) {
		return !object.has("type") || (object.has("type") && object.get("type").getAsString().equals("error"));
	}

	public void shutdown() {
		try {
			if (session != null && session.isOpen()) {
				session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "API shutdown procedure"));
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
		System.out.println(session.isOpen());
		if (isConnected()) {
			requests.put(request.getId(), request);
			ThreadExecuter.scheduleTask(() -> {
				String text = request.getJson();
				logger.debug("Sending Request: " + text);
				try {
					session.getBasicRemote().sendText(text);
				} catch (IOException e) {
					logger.error("Failed to send Request! Request: ", text, e);
				}
			});
		} else {
			logger.debug("Not sending request because API is closed: "+request.getJson());
		}
	}

	public void onMessage(String message) {
		logger.debug("Handling response: "+message);
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

	public void onClose(CloseReason reason) {
		logger.debug("Session closed! Reason: "+reason.getReasonPhrase()+" Code: "+reason.getCloseCode());
		logger.debug("Restarting API session...");
		session = createSession();
		logger.debug("Restarted API session!");
	}

	public void restart() {
		startup(uuid);
	}
}
