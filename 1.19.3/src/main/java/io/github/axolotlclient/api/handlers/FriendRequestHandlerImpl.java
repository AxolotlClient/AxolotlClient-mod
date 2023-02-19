package io.github.axolotlclient.api.handlers;

import io.github.axolotlclient.util.notifications.Notifications;
import net.minecraft.text.Text;

public class FriendRequestHandlerImpl implements FriendRequestHandler {

	@Override
	public void handleIncomingRequest(String fromUUID, String fromUsername) {
		Notifications.getInstance().addStatus(Text.translatable("friendRequests.title"), Text.translatable("friendRequests.new", fromUsername));
	}
}
