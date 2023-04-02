package io.github.axolotlclient.api.chat;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.modules.auth.Auth;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Getter
public class ChatLine extends DrawableHelper {
	protected final MinecraftClient client = MinecraftClient.getInstance();
	private final OrderedText content;
	private final ChatMessage origin;

	public ChatLine(OrderedText content, ChatMessage origin) {
		this.content = content;
		this.origin = origin;
	}

	public int render(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY) {
		MinecraftClient.getInstance().textRenderer.draw(matrices, content, x, y, color);
		return renderExtras(matrices, x, y, color, mouseX, mouseY) + MinecraftClient.getInstance().textRenderer.fontHeight;
	}

	protected int renderExtras(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY) {
		return y;
	}

	public static class NameChatLine extends ChatLine {

		public NameChatLine(ChatMessage message) {
			super(Text.literal(message.getSender().getName()).setStyle(Style.EMPTY.withBold(true)).asOrderedText(), message);
		}

		@Override
		public int render(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY) {
			return super.render(matrices, x, y + 4, color, mouseX, mouseY);
		}

		@Override
		protected int renderExtras(MatrixStack matrices, int x, int y, int color, int mouseX, int mouseY) {
			RenderSystem.setShaderTexture(0, Auth.getInstance().getSkinTexture(getOrigin().getSender().getUuid(),
				getOrigin().getSender().getName()));
			drawTexture(matrices, x - 20, y, 18, 18, 8, 8, 8, 8, 64, 64);
			drawTexture(matrices, x - 20, y, 18, 18, 40, 8, 8, 8, 64, 64);
			return y;
		}
	}
}
