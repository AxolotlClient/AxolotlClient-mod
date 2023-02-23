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

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Identifiable;
import io.github.axolotlclient.api.handlers.ChatHandler;
import io.github.axolotlclient.api.handlers.FriendHandler;
import io.github.axolotlclient.api.types.User;
import io.github.axolotlclient.api.util.AlphabeticalComparator;
import io.github.axolotlclient.mixin.ScreenAccessor;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
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
		super();
		this.parent = parent;
	}

	@Override
	public void init() {
		sidebarWidth = 70;
		sidebarAnimX = -sidebarWidth;

		if(parent != null) {
			((ScreenAccessor)parent).getButtons().stream()
					.filter(e -> e.message.equals(I18n.translate("api.friends"))).forEach(e -> e.visible = false);
		}


		FriendHandler.getInstance().getFriends(list -> this.list = new ListWidget(list, 10, 30, 50, height-70));

		buttons.add(new ButtonWidget(0, 10-sidebarWidth, height-30, 50, 20, I18n.translate("gui.back")));
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if(parent != null) {
			parent.render(mouseX, mouseY, delta);
		}
		fill(sidebarAnimX, 0, sidebarWidth + sidebarAnimX, height, 0x99000000);

		client.textRenderer.drawWithShadow(I18n.translate("api.friends"), 10+sidebarAnimX, 10, -1);
		if(list != null) {
			list.render(mouseX, mouseY, delta);
		}

		super.render(mouseX, mouseY, delta);

		if(input != null){
			input.render();
		}

		if(hasChat){
			drawVerticalLine(70+sidebarAnimX, 0, height, 0xFF000000);
			client.textRenderer.drawWithShadow(chatUser.getName(), sidebarAnimX+75, 20, -1);
			client.textRenderer.drawWithShadow(Formatting.ITALIC+chatUser.getStatus().getTitle()+":"+chatUser.getStatus().getDescription(),
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
			buttons.forEach(button -> button.x += ANIM_STEP);
		} else if (remove){
			if(sidebarAnimX < -sidebarWidth){
				close();
			}
			sidebarAnimX-=ANIM_STEP;
			if(list != null) {
				list.setX(list.getX() - ANIM_STEP);
			}
			buttons.forEach(button -> button.x -=ANIM_STEP);
		} else {
			if(list != null) {
				list.visible = true;
			}
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if(button.id == 0){
			remove();
		}
	}

	public void remove(){
		remove = true;
	}

	private void close(){
		Keyboard.enableRepeatEvents(false);
		client.setScreen(parent);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(mouseX > sidebarWidth){
			remove();
			return;
		}
		if(list != null) {
			list.mouseClicked(mouseX, mouseY, button);
		}

		if(input != null){
			input.mouseClicked(mouseX, mouseY, button);
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int i, int j, int k) {
		super.mouseReleased(i, j, k);
		if(list != null){
			list.mouseReleased(i, j, k);
		}
	}

	@Override
	protected void keyPressed(char c, int i) {
		if(input != null){
			input.keyPressed(c, i);
		}
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		if(list != null){
			list.handleMouse();
		}
	}

	private void addChat(User user){
		// TODO implement Chat
		hasChat = true;
		chatUser = user;
		sidebarWidth = Math.max(width*5/12, client.textRenderer.getStringWidth(chatUser.getStatus().getTitle()+":"+chatUser.getStatus().getDescription())+5);
		input = new TextFieldWidget(2, textRenderer, 75, height-30, sidebarWidth-80, 20){

			@Override
			public boolean keyPressed(char c, int i) {
				if(i == Keyboard.KEY_RETURN){
					// TODO send chat message
					ChatHandler.getInstance().sendMessage(chatUser, input.getText());
					input.setText("");
					return true;
				}
				return super.keyPressed(c, i);
			}
		};
	}

	@Override
	public void tick() {
		if(input != null){
			input.tick();
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return parent != null && parent.shouldPauseGame();
	}

	private class ListWidget extends EntryListWidget {
		private final List<UserButton> elements;
		private final int entryHeight = 25;
		private boolean visible;

		public ListWidget(List<User> list, int x, int y, int width, int height){
			super(MinecraftClient.getInstance(), width, height, y, FriendsSidebar.this.height - y, 25);
			xStart = x;
			xEnd = x + width;
			yStart = y;
			yEnd = y+height;
			AtomicInteger buttonY = new AtomicInteger(y);
			elements = list.stream().sorted((u1, u2) -> new AlphabeticalComparator().compare(u1.getName(), u2.getName()))
					.map(user -> new UserButton(x, buttonY.getAndAdd(entryHeight), width, entryHeight-5,
					user.getName(), buttonWidget -> addChat(user))).collect(Collectors.toList());
		}

		@Override
		protected void renderList(int x, int y, int mouseX, int mouseY) {
			Util.applyScissor(xStart, yStart, xStart+this.width, yEnd - yStart);
			super.renderList(x, y, mouseX, mouseY);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}

		@Override
		public void render(int mouseX, int mouseY, float delta) {
			if (this.visible) {
				GlStateManager.enableDepthTest();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 1F);
				this.lastMouseX = mouseX;
				this.lastMouseY = mouseY;
				this.capYPosition();
				GlStateManager.disableLighting();
				GlStateManager.disableFog();
				int k = this.width / 2;
				int l = this.yStart + 4 - (int) this.scrollAmount;
				GlStateManager.enableTexture();

				this.renderList(k, l, mouseX, mouseY);

				GlStateManager.shadeModel(7424);
				GlStateManager.enableAlphaTest();
				GlStateManager.popMatrix();
				GlStateManager.disableBlend();
			}
		}

		public void setX(int x){
			xStart = x;
			xEnd = x + width;
			elements.forEach(e -> e.x = x);
		}

		public int getX(){
			return xStart;
		}

		@Override
		protected int getEntryCount() {
			return elements.size();
		}

		@Override
		public Entry getEntry(int i) {
			return elements.get(i);
		}

		public class UserButton extends ButtonWidget implements Entry {
			private final Action action;

			public UserButton(int x, int y, int width, int height, String string, Action action) {
				super(0, x, y, width, height, string);
				this.action = action;
				visible = true;
			}

			public void mouseClicked(int mouseX, int mouseY){
				if(isMouseOver(client, mouseX, mouseY)){
					playDownSound(client.getSoundManager());
					action.onPress(this);
				}
			}

			@Override
			public void updatePosition(int i, int j, int k) {

			}

			@Override
			public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY,
							   boolean hovered) {
				this.y = y;
				render(client, mouseX, mouseY);
			}

			@Override
			public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
				if(isMouseOver(client, mouseX, mouseY)){
					playDownSound(client.getSoundManager());
					action.onPress(this);
					return true;
				}
				return false;
			}

			@Override
			public void mouseReleased(int i, int j, int k, int l, int m, int n) {

			}
		}

	}

	public interface Action {
		void onPress(ListWidget.UserButton button);
	}
}
