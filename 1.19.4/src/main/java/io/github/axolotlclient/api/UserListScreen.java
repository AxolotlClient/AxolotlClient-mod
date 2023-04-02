package io.github.axolotlclient.api;

import java.util.UUID;

import io.github.axolotlclient.api.chat.ChatScreen;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.api.types.Status;
import io.github.axolotlclient.api.types.User;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class UserListScreen extends Screen {
	protected UserListScreen(Text title) {
		super(title);
	}

	protected abstract UserListWidget getWidget();

	public void openChat() {
		UserListWidget.UserListEntry entry = getWidget().getSelectedOrNull();
		if (entry != null) {
			User u1 = new User("u1", UUID.randomUUID().toString(), Status.UNKNOWN);
			User u2 = new User("U2", UUID.randomUUID().toString(), Status.UNKNOWN);
			User self = API.getInstance().getSelf();
			client.setScreen(new ChatScreen(this, new Channel.Group("aaaa",
				new User[]{self, u1, u2}, "Group!!", new ChatMessage[]{new ChatMessage(u1, "AHHHHHHHHH!!", 16835345), new ChatMessage(self, "hhhhhh", 16835348)})));
			//API.getInstance().send(ChannelRequest.getDM(c -> client.setScreen(new ChatScreen(this, c)),
			//		entry.getUser().getUuid(), ChannelRequest.Include.MESSAGES));
		}
	}

	public void select(UserListWidget.UserListEntry userListEntry) {
	}
}
