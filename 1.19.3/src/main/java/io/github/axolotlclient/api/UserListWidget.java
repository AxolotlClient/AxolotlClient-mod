package io.github.axolotlclient.api;

import io.github.axolotlclient.api.types.User;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class UserListWidget extends AlwaysSelectedEntryListWidget<UserListWidget.UserListEntry> {

	private final FriendsScreen screen;

	public UserListWidget(FriendsScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
		this.screen = screen;
	}

	public void setUsers(List<User> users){
		users.forEach(user -> addEntry(new UserListEntry(user)));
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}

	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 30;
	}

	@Override
	protected boolean isFocused() {
		return this.screen.getFocused() == this;
	}

	public static class UserListEntry extends AlwaysSelectedEntryListWidget.Entry<UserListEntry> {

		@Getter
		private final User user;

		public UserListEntry(User user){
			this.user = user;
		}


		@Override
		public Text getNarration() {
			return Text.of(user.getName());
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

		}
	}
}
