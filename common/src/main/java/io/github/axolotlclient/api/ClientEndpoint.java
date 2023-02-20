package io.github.axolotlclient.api;

import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@jakarta.websocket.ClientEndpoint
public class ClientEndpoint {

	@OnMessage
	public void onMessage(String message) {
		API.getInstance().onMessage(message);
	}

	@OnOpen
	public void onOpen(Session session) {
		API.getInstance().onOpen(session);
	}

	@OnError
	public void onError(Throwable throwable) {
		API.getInstance().onError(throwable);
	}
}
