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
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class UserListWidget extends EntryListWidget {

	private final FriendsScreen screen;
	private int selectedEntry = -1;

	private final List<UserListEntry> entries = new ArrayList<>();

	public UserListWidget(FriendsScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
		this.screen = screen;
	}

	public void setUsers(List<User> users) {
		users.forEach(user -> addEntry(new UserListEntry(user)));
	}

	public int addEntry(UserListEntry entry) {
		entries.add(entry.init(screen));
		return entries.indexOf(entry);
	}

	@Override
	protected int getEntryCount() {
		return entries.size();
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 30;
	}

	public void setSelected(int i) {
		this.selectedEntry = i;
	}

	@Override
	protected boolean isEntrySelected(int i) {
		return i == this.selectedEntry;
	}

	public int getSelected() {
		return this.selectedEntry;
	}

	@Override
	public Entry getEntry(int i) {
		return entries.get(i);
	}

	public UserListEntry getSelectedEntry() {
		if (getSelected() < 0) {
			return null;
		}
		return entries.get(getSelected());
	}

	public static class UserListEntry implements EntryListWidget.Entry {

		@Getter
		private final User user;
		private long time;

		private final MinecraftClient client;

		private String note;
		private FriendsScreen screen;

		public UserListEntry(User user) {
			this.client = MinecraftClient.getInstance();
			this.user = user;
		}

		public UserListEntry(User user, String note) {
			this(user);
			this.note = Formatting.ITALIC + note;
		}

		public UserListEntry init(FriendsScreen screen) {
			this.screen = screen;
			return this;
		}

		@Override
		public void updatePosition(int i, int j, int k) {

		}

		@Override
		public void render(int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered) {
			client.textRenderer.draw(user.getName(), x + 3 + 33, y + 1, -1);
			client.textRenderer.draw(user.getStatus().getTitle(), x + 3 + 33, y + 12, 8421504);
			if (user.getStatus().isOnline()) {
				client.textRenderer.draw(user.getStatus().getText(), x + 3 + 40, y + 23, 8421504);
			}

			if (note != null) {
				client.textRenderer.draw(note, x + entryWidth - client.textRenderer.getStringWidth(note) - 2, y + entryHeight - 10, 8421504);
			}
		}

		@Override
		public boolean mouseClicked(int i, int j, int k, int l, int m, int n) {
			this.screen.select(i);
			if (MinecraftClient.getTime() - this.time < 250L && client.world == null) {
				screen.openChat();
			}

			this.time = MinecraftClient.getTime();
			return false;
		}

		@Override
		public void mouseReleased(int i, int j, int k, int l, int m, int n) {

		}
	}
}
