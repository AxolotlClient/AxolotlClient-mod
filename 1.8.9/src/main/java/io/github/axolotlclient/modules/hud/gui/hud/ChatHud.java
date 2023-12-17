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

package io.github.axolotlclient.modules.hud.gui.hud;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.mixin.ChatHudAccessor;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Texts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ChatHud extends TextHudEntry {

	public static final Identifier ID = new Identifier("axolotlclient", "chathud");
	public final BooleanOption background = new BooleanOption("background", "chathud", true);
	public final ColorOption bgColor = new ColorOption("bgcolor", "#40000000");

	public final IntegerOption chatHistory = new IntegerOption("chatHistoryLength", 100, 10, 5000);
	public final ColorOption scrollbarColor = new ColorOption("scrollbarColor", "#70CCCCCC");
	public final IntegerOption lineSpacing = new IntegerOption("lineSpacing", 0, 0, 10);

	public int ticks;
	private int lastHeight;

	public ChatHud() {
		super(320, 80, false);
	}

	@Override
	public void render(float delta) {
		int scrolledLines = ((ChatHudAccessor) client.inGameHud.getChatHud()).getScrolledLines();
		List<ChatHudLine> visibleMessages = ((ChatHudAccessor) client.inGameHud.getChatHud()).getVisibleMessages();

		if (this.client.options.chatVisibilityType != PlayerEntity.ChatVisibilityType.HIDDEN) {
			GlStateManager.pushMatrix();
			scale();
			DrawPosition pos = getPos();

			int i = getVisibleLineCount();
			int j = 0;
			int k = visibleMessages.size();
			float f = this.client.options.chatOpacity * 0.9F + 0.1F;
			if (k > 0) {
				float g = getScale();
				int l = MathHelper.ceil((float) getWidth() / g);
				GlStateManager.pushMatrix();

				for (int m = 0; m + scrolledLines < visibleMessages.size() && m < i; ++m) {
					ChatHudLine chatHudLine = visibleMessages.get(m + scrolledLines);
					if (chatHudLine != null) {
						int n = ticks - chatHudLine.getCreationTick();
						if (n < 200 || isChatFocused()) {
							double d = MathHelper.clamp((1.0 - n / 200.0) * 10.0, 0.0, 1.0);
							d *= d;
							int Opacity = isChatFocused() ? 255 : (int) (255.0 * d);

							int chatOpacity = (int) (Opacity * f);
							++j;
							if (chatOpacity > 3) {
								int y = pos.y + getHeight() - (m * (9 + lineSpacing.get()));
								if (background.get()) {
									fill(pos.x, y - (9 + lineSpacing.get()), pos.x + l + 4, y,
										bgColor.get().withAlpha(chatOpacity / 2).getAsInt());
								}
								String string = chatHudLine.getText().asFormattedString();
								GlStateManager.enableBlend();
								DrawUtil.drawString(client.textRenderer, string, pos.x, (y - 8),
									16777215 + (chatOpacity << 24), shadow.get());
								GlStateManager.disableAlphaTest();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if (isChatFocused()) {
					int m = getFontHeight();
					GlStateManager.translate(-3.0F, 0.0F, 0.0F);
					int r = k * m + k;
					int n = j * m + j;
					int y = (pos.y + getHeight()) - scrolledLines * n / k;
					if (((ChatHudAccessor) client.inGameHud.getChatHud()).getMessages()
						.size() > getVisibleLineCount()) {
						int height = n * n / r;
						fillRect(pos.x, y, 2, -height, scrollbarColor.get().getAsInt());
					}
				}

				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void renderComponent(float delta) {
	}

	@Override
	public void renderPlaceholderComponent(float delta) {
		DrawPosition pos = getPos();
		if (MinecraftClient.getInstance().player != null) {
			client.textRenderer.drawWithShadow("<" + MinecraftClient.getInstance().player.getName().asFormattedString()
				+ "> OOh! There's my Chat now!", pos.x + 1, pos.y + getHeight() - 9, -1);
		} else {
			client.textRenderer.drawWithShadow("This is where your new and fresh looking chat will be!", pos.x + 1,
				pos.y + getHeight() - 9, -1);
		}
	}

	@Override
	public boolean movable() {
		return true;
	}

	public int getVisibleLineCount() {
		return getHeight(
			this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused)
			/ 9;
	}

	public boolean isChatFocused() {
		return this.client.currentScreen instanceof ChatScreen;
	}

	protected int getFontHeight() {
		return MathHelper.floor(MinecraftClient.getInstance().textRenderer.fontHeight);
	}

	public static int getHeight(float chatHeight) {
		int i = 180;
		int j = 20;
		return MathHelper.floor(chatHeight * (float) (i - j) + (float) j);
	}

	public Text getTextAt(int x, int y) {
		List<ChatHudLine> visibleMessages = ((ChatHudAccessor) client.inGameHud.getChatHud()).getVisibleMessages();

		int offsetOnHudX = MathHelper.floor(x / getScale() - getPos().x);
		int offsetOnHudY = MathHelper.floor(-(y / getScale() - (getPos().y + height)));

		int scrolledLines = ((ChatHudAccessor) client.inGameHud.getChatHud()).getScrolledLines();

		if (offsetOnHudX >= 0 && offsetOnHudY >= 0) {
			int l = Math.min(this.getVisibleLineCount(), visibleMessages.size());
			if (offsetOnHudX <= MathHelper.floor((float) this.getWidth() / this.getScale())
				&& offsetOnHudY < (getFontHeight() + lineSpacing.get()) * l + l) {
				int m = offsetOnHudY / (getFontHeight() + lineSpacing.get()) + scrolledLines;
				if (m >= 0 && m < visibleMessages.size()) {
					ChatHudLine chatHudLine = visibleMessages.get(m);
					int n = 0;

					for (Text text : chatHudLine.getText()) {
						if (text instanceof LiteralText) {
							n += this.client.textRenderer.getStringWidth(
								Texts.getRenderChatMessage(((LiteralText) text).getRawString(), false));
							if (n > offsetOnHudX) {
								return text;
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public boolean overridesF3() {
		return true;
	}

	@Override
	public void tick() {
		//setWidth((int) (client.options.chatWidth*320));
		if (lastHeight != getHeight(client.options.chatHeightUnfocused)) {
			setHeight(getHeight(this.client.options.chatHeightUnfocused));//int) (client.options.chatHeightUnfocused*180)+11);this.isChatFocused() ? this.client.options.chatHeightFocused
			onBoundsUpdate();
			lastHeight = getHeight();
		}
	}

    /*public int getHeight() {
        return getHeight(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
    }*/

	@Override
	public double getDefaultX() {
		return 0.01;
	}

	@Override
	public double getDefaultY() {
		return 0.9;
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(background);
		options.add(bgColor);
		options.add(lineSpacing);
		options.add(scrollbarColor);
		options.add(chatHistory);
		return options;
	}
}
