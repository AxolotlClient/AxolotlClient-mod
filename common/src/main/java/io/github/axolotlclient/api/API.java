/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.api;

import com.google.gson.JsonObject;
import io.github.axolotlclient.api.handlers.*;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
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

	private static final String API_BASE = "wss://axolotlclient.xyz";
	private static final URI API_URL = URI.create(API_BASE + "/api/ws");
	private static final int STATUS_UPDATE_DELAY = 15; // The Delay between Status updates, in seconds. Discord uses 15 seconds so we will as well.
	private static final boolean TESTING = false;

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
	@Getter
	private final Options apiOptions;
	private Session session;
	@Getter
	private String uuid;
	@Getter
	private User self;

	public API(Logger logger, NotificationProvider notificationProvider, TranslationProvider translationProvider, StatusUpdateProvider statusUpdateProvider, Options apiOptions) {
		this.logger = logger;
		this.notificationProvider = notificationProvider;
		this.translationProvider = translationProvider;
		this.statusUpdateProvider = statusUpdateProvider;
		this.apiOptions = apiOptions;
		Instance = this;
		addHandler(new FriendRequestHandler());
		addHandler(new FriendRequestAcceptedHandler());
		addHandler(new FriendRequestDeniedHandler());
		addHandler(FriendHandler.getInstance());
		addHandler(new StatusUpdateHandler());
		addHandler(ChatHandler.getInstance());
	}

	public void addHandler(RequestHandler handler) {
		handlers.add(handler);
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
				if (apiOptions.detailedLogging.get()) {
					notificationProvider.addStatus("api.error.handshake", APIError.fromResponse(object));
				}
				shutdown();
			} else {
				logger.debug("Handshake successful!");
				if (apiOptions.detailedLogging.get()) {
					notificationProvider.addStatus("api.success.handshake", "api.success.handshake.desc");
				}
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
				session = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(Request request) {
		if (isConnected()) {
			if (!request.equals(Request.DUMMY) && !TESTING) {
				requests.put(request.getId(), request);
				ThreadExecuter.scheduleTask(() -> {
					String text = request.getJson();
					logDetailed("Sending Request: " + text);
					try {
						session.getBasicRemote().sendText(text);
					} catch (IOException e) {
						logger.error("Failed to send Request! Request: ", text, e);
					}
				});
			}
		} else {
			logger.warn("Not sending request because API is closed: " + request.getJson());
		}
	}

	public boolean isConnected() {
		return session != null && session.isOpen();
	}

	public void logDetailed(String message, Object... args) {
		if (apiOptions.detailedLogging.get()) {
			logger.debug("[DETAIL] " + message, args);
		}
	}

	public void onMessage(String message) {
		logDetailed("Handling response: " + message);
		handleResponse(message);
	}

	private void handleResponse(String response) {
		try {
			JsonObject object = GsonHelper.fromJson(response);

			String id = "";
			if (object.has("id") && !object.get("id").isJsonNull()) id = object.get("id").getAsString();

			if (requests.containsKey(id)) {
				requests.get(id).getHandler().accept(object);
				requests.remove(id);

			} else if (id == null || id.isEmpty()) {
				handlers.stream().filter(handler -> handler.isApplicable(object)).forEach(handler -> handler.handle(object));
			} else {
				logger.error("Unknown response: " + response);
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error("Invalid response: " + response);
		}
	}

	public void onError(Throwable throwable) {
		logger.error("Error while handling API traffic!", throwable);
	}

	public void onClose(CloseReason reason) {
		logDetailed("Session closed! Reason: " + reason.getReasonPhrase() + " Code: " + reason.getCloseCode());
		logDetailed("Restarting API session...");
		session = createSession();
		logDetailed("Restarted API session!");
	}

	private Session createSession() {
		try {
			return GrizzlyContainerProvider.getWebSocketContainer().connectToServer(ClientEndpoint.class, API_URL);
		} catch (DeploymentException | IOException e) {
			logger.error("Error while starting API!", e);
			return null;
		}
	}

	public void restart() {
		if(isConnected()) {
			shutdown();
		}
		if(uuid != null) {
			startup(uuid);
		} else {
			apiOptions.enabled.set(false);
		}
	}

	public void startup(String uuid){
		this.uuid = uuid;
		switch (apiOptions.privacyAccepted.get()) {
			case "unset":
				apiOptions.openPrivacyNoteScreen.accept(v -> {
					if (v) startupAPI();
				});
				break;
			case "accepted":
				startupAPI();
				break;
			default:
				break;
		}
	}

	void startupAPI() {
		if (!isConnected()) {
			self = new User(this.uuid, Status.UNKNOWN);
			logger.debug("Starting API...");
			session = createSession();

			new Thread("Status Update Thread") {
				@Override
				public void run() {
					try {
						Thread.sleep(50);
					} catch (InterruptedException ignored) {
					}
					while (API.getInstance().isConnected()) {
						Request statusUpdate = statusUpdateProvider.getStatus();
						send(statusUpdate);
						try {
							//noinspection BusyWait
							Thread.sleep(STATUS_UPDATE_DELAY * 1000);
						} catch (InterruptedException ignored) {

						}
					}
				}
			}.start();
		} else {
			logger.warn("API is already running!");
		}
	}

	public String sanitizeUUID(String uuid) {
		if (uuid.contains("-")) {
			return uuid.replace("-", "");
		}
		return uuid;
	}
}
