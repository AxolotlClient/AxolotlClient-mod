package io.github.axolotlclient.api;

import io.github.axolotlclient.api.handlers.FriendHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

public class FriendsScreen extends Screen {

	private final Screen parent;

	private UserListWidget widget;

	private ButtonWidget chatButton, removeButton;

	protected FriendsScreen(Screen parent) {
		super(Text.translatable("api.screen.friends"));
		this.parent = parent;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.widget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected void init() {
		addSelectableChild(widget = new UserListWidget(this, client,  width, height, 32, height - 64, 35));
		FriendHandler.getInstance().getFriends(list -> widget.setUsers(list));

		/*addDrawableChild(loginButton = new ButtonWidget.Builder(Text.translatable("auth.login"),
				buttonWidget -> login()).positionAndSize(this.width / 2 - 154, this.height - 52, 150, 20).build());*/

		this.addDrawableChild(ButtonWidget.builder(Text.translatable("api.friends.add"),
						button -> {
							client.setScreen(new AddFriendScreen(this));
						})
				.positionAndSize(this.width / 2 + 4, this.height - 52, 150, 20).build());

		this.removeButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("api.friends.remove"), button -> {
			UserListWidget.UserListEntry entry = this.widget.getSelectedOrNull();
			if (entry != null) {
				FriendHandler.getInstance().removeFriend(entry.getUser());
				refresh();
			}
		}).positionAndSize(this.width / 2 - 50, this.height - 28, 100, 20).build());


		this.addDrawableChild(chatButton = ButtonWidget.builder(Text.translatable("api.friends.chat"), button -> openChat())
				.positionAndSize(this.width / 2 - 154, this.height - 28, 100, 20)
				.build()
		);

		this.addDrawableChild(
				ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
						.positionAndSize(this.width / 2 + 4 + 50, this.height - 28, 100, 20)
						.build()
		);
		updateButtonActivationStates();
	}

	private void openChat() {

	}

	private void refresh(){
		client.setScreen(new FriendsScreen(parent));
	}

	private void updateButtonActivationStates() {
		UserListWidget.UserListEntry entry = widget.getSelectedOrNull();
		if (client.world == null && entry != null) {
			chatButton.active = removeButton.active = true;
		} else {
			chatButton.active = removeButton.active = false;
		}
	}

	public void select(UserListWidget.UserListEntry entry) {
		this.widget.setSelected(entry);
		this.updateButtonActivationStates();
	}
}
