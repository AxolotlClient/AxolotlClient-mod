package io.github.axolotlclient.api.chat;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.modules.auth.Auth;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Getter
public class ChatLine extends DrawableHelper {
	private final OrderedText content;
	private final ChatMessage origin;
	protected final MinecraftClient client = MinecraftClient.getInstance();

	public ChatLine(OrderedText content, ChatMessage origin) {
		this.content = content;
		this.origin = origin;
	}

	public void render(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY){
		MinecraftClient.getInstance().textRenderer.draw(matrices, content, x, y, color);
		renderExtras(matrices, x, y, color, mouseX, mouseY);
	}

	protected void renderExtras(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY){}

	public static class NameChatLine extends ChatLine {

		public NameChatLine(ChatMessage message) {
			super(Text.of(message.getSender().getName()).asOrderedText(), message);
		}

		@Override
		protected void renderExtras(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY) {
			RenderSystem.setShaderTexture(0, Auth.getInstance().getSkinTexture(getOrigin().getSender().getUuid(),
					getOrigin().getSender().getName()));
			drawTexture(matrices, x-20, y, 18, 18, 8, 8, 8, 8, 64, 64);
			drawTexture(matrices, x-20, y, 18, 18, 40, 8, 8, 8, 64, 64);
		}
	}
}
