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

import io.github.axolotlclient.api.types.User;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.List;

public class UserListWidget extends AlwaysSelectedEntryListWidget<UserListWidget.UserListEntry> {

	private final FriendsScreen screen;

	public UserListWidget(FriendsScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
		this.screen = screen;
	}

	public void setUsers(List<User> users) {
		users.forEach(user -> addEntry(new UserListEntry(user)));
	}

	public int addEntry(UserListEntry entry) {
		return super.addEntry(entry.init(screen));
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
		private long time;

		private final MinecraftClient client;

		private Text note;
		private FriendsScreen screen;

		public UserListEntry(User user) {
			this.client = MinecraftClient.getInstance();
			this.user = user;
		}

		public UserListEntry(User user, MutableText note) {
			this(user);
			this.note = note.formatted(Formatting.ITALIC);
		}

		public UserListEntry init(FriendsScreen screen) {
			this.screen = screen;
			return this;
		}


		@Override
		public Text getNarration() {
			return Text.of(user.getName());
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, user.getName(), x + 3 + 33, y + 1, -1);
			client.textRenderer.draw(matrices, user.getStatus().getTitle(), x + 3 + 33, y + 12, 8421504);
			if (user.getStatus().isOnline()) {
				client.textRenderer.draw(matrices, user.getStatus().getDescription(), x + 3 + 40, y + 23, 8421504);
			}

			if (note != null) {
				client.textRenderer.draw(matrices, note, x + entryWidth - client.textRenderer.getWidth(note) - 2, y + entryHeight - 10, 8421504);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.screen.select(this);
			if (Util.getMeasuringTimeMs() - this.time < 250L && client.world == null) {
				screen.openChat();
			}

			this.time = Util.getMeasuringTimeMs();
			return false;
		}
	}
}
