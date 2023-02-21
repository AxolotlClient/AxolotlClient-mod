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

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.api.handlers.ChatHandler;
import io.github.axolotlclient.api.handlers.FriendHandler;
import io.github.axolotlclient.api.types.User;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FriendsSidebar extends Screen {

	private final Screen parent;

	private int sidebarAnimX;
	private static final int ANIM_STEP = 5;
	private int sidebarWidth;
	private boolean remove;
	private boolean hasChat;
	private User chatUser;
	private ListWidget list;
	private TextFieldWidget input;

	public FriendsSidebar(Screen parent) {
		super(Text.translatable("api.friends.sidebar"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		sidebarWidth = 70;
		sidebarAnimX = -sidebarWidth;

		if(parent != null) {
			parent.children().stream().filter(element -> element instanceof ClickableWidget)
					.map(e -> (ClickableWidget) e).filter(e -> e.getMessage().equals(Text.translatable("api.friends"))).forEach(e -> e.visible = false);
		}


		FriendHandler.getInstance().getFriends(list -> addDrawableChild(this.list = new ListWidget(list, 10, 30, 50, height-60)));

		addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, buttonWidget -> remove()).positionAndSize(10-sidebarWidth, height-30, 50, 20).build());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if(parent != null) {
			parent.render(matrices, mouseX, mouseY, delta);
		}
		fill(matrices, sidebarAnimX, 0, sidebarWidth + sidebarAnimX, height, 0x99000000);

		client.textRenderer.drawWithShadow(matrices, Text.translatable("api.friends"), 10+sidebarAnimX, 10, -1);

		super.render(matrices, mouseX, mouseY, delta);

		if(hasChat){
			drawVerticalLine(matrices, 70+sidebarAnimX, 0, height, 0xFF000000);
			client.textRenderer.drawWithShadow(matrices, chatUser.getName(), sidebarAnimX+75, 20, -1);
			client.textRenderer.drawWithShadow(matrices, Formatting.ITALIC+chatUser.getStatus().getTitle()+":"+chatUser.getStatus().getDescription(),
					sidebarAnimX+80, 30, 8421504);
		}

		animate();
	}

	private void animate(){
		if(sidebarAnimX<0 && !remove){
			if(sidebarAnimX > -ANIM_STEP){
				sidebarAnimX = -ANIM_STEP;
			}
			sidebarAnimX+=ANIM_STEP;
			if(list != null) {
				list.visible = false;
			}
			getButtons().forEach(button -> button.setX(button.getX()+ANIM_STEP));
		} else if (remove){
			if(sidebarAnimX < -sidebarWidth){
				close();
			}
			sidebarAnimX-=ANIM_STEP;
			if(list != null) {
				list.setX(list.getX() - ANIM_STEP);
			}
			getButtons().forEach(button -> button.setX(button.getX()-ANIM_STEP));
		} else {
			if(list != null) {
				list.visible = true;
			}
		}
	}

	public void remove(){
		remove = true;

	}

	private void close(){
		client.setScreen(parent);
	}

	public List<ClickableWidget> getButtons(){
		return children().stream().filter(element -> element instanceof ClickableWidget).map(element -> (ClickableWidget) element).collect(Collectors.toList());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(mouseX > sidebarWidth){
			remove();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void addChat(User user){
		// TODO implement Chat
		hasChat = true;
		chatUser = user;
		sidebarWidth = Math.max(width*5/12, client.textRenderer.getWidth(chatUser.getStatus().getTitle()+":"+chatUser.getStatus().getDescription())+5);
		addDrawableChild(input = new TextFieldWidget(textRenderer, 75, height-30, sidebarWidth-80, 20, Text.translatable("api.friends.chat.input")){
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				if(keyCode == InputUtil.KEY_ENTER_CODE){
					// TODO send chat message
					ChatHandler.getInstance().sendMessage(chatUser, input.getText());
					input.setText("");
					return true;
				}
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		});
	}

	@Override
	public void tick() {
		if(input != null){
			input.tick();
		}
	}

	@Override
	public boolean isPauseScreen() {
		return parent != null && parent.isPauseScreen();
	}

	private class ListWidget extends AbstractParentElement implements Drawable, Element, Selectable {
		private final List<ClickableWidget> elements;
		private int x;
		private final int y;
		private final int width;
		private final int height;
		private int scrollAmount;
		protected boolean hovered;
		private final int entryHeight = 25;
		private boolean visible;

		public ListWidget(List<User> list, int x, int y, int width, int height){
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			AtomicInteger buttonY = new AtomicInteger(y);
			elements = list.stream().map(user -> ButtonWidget.builder(Text.of(user.getName()), buttonWidget -> addChat(user))
					.positionAndSize(x, buttonY.getAndAdd(entryHeight), width, entryHeight-5).build()).collect(Collectors.toList());
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			if(visible) {
				matrices.push();
				FriendsSidebar.enableScissor(x, y, x + width, y + height);

				matrices.translate(0, -scrollAmount, 0);

				elements.forEach(e -> e.render(matrices, mouseX, mouseY, delta));

				FriendsSidebar.disableScissor();
				matrices.pop();
			}
		}

		@Override
		public List<? extends Element> children() {
			return elements;
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return hovered = visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height);
		}

		public void setX(int x){
			this.x = x;
			elements.forEach(e -> e.setX(x));
		}

		public int getX(){
			return x;
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
			if(this.isMouseOver(mouseX, mouseY)){
				if(elements.size()*entryHeight>height) {
					int a = scrollAmount;
					a += amount * (entryHeight/2);
					scrollAmount = MathHelper.clamp(a, 0, -elements.size()*entryHeight);
					return true;
				}
			}
			return super.mouseScrolled(mouseX, mouseY, amount);
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {

		}

		@Override
		public Selectable.SelectionType getType() {
			return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
		}
	}
}
