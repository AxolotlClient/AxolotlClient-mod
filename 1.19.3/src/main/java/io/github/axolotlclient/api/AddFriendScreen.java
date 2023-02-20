package io.github.axolotlclient.api;

import io.github.axolotlclient.api.handlers.FriendHandler;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.auth.MSAccount;
import io.github.axolotlclient.util.notifications.Notifications;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

import java.util.UUID;

public class AddFriendScreen extends Screen {

	private TextFieldWidget nameInput;
	private final Screen parent;

	public AddFriendScreen(Screen parent) {
		super(Text.translatable("api.screen.friends.add"));
		this.parent = parent;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers) || nameInput.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void tick() {
		nameInput.tick();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button) || nameInput.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack matrices, int i, int j, float f) {
		renderBackground(matrices);
		super.render(matrices, i, j, f);
		textRenderer.drawWithShadow(matrices, title, width / 2F - 100, height / 2f - 20, -1);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		nameInput.render(matrices, i, j, f);
	}

	@Override
	public void init() {
		addDrawableChild(nameInput = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 10, 200, 20, Text.empty()));

		addDrawableChild(new ButtonWidget.Builder(ScreenTexts.CANCEL, button -> client.setScreen(parent)).positionAndSize(width / 2 - 155, height - 50, 150, 20).build());
		addDrawableChild(new ButtonWidget.Builder(ScreenTexts.DONE, button -> {
			if(API.getInstance().isConnected()) {
				UUID uuid;
				try {
					uuid = UUID.fromString(nameInput.getText());
				} catch (IllegalArgumentException e) {
					uuid = UUID.fromString(UUIDHelper.getUuid(nameInput.getText()));
				}
				FriendHandler.getInstance().addFriend(uuid);
				client.setScreen(parent);
			} else {
				Notifications.getInstance().addStatus("api.error.notLoggedIn", "api.error.notLoggedIn.desc");
				client.setScreen(parent);
			}
		}).positionAndSize(width / 2 + 5, height - 50, 150, 20).build());
	}
}
