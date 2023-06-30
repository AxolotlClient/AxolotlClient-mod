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

package io.github.axolotlclient.modules.hypixel.bedwars.stats;

import io.github.axolotlclient.AxolotlClientConfig.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hud.gui.component.HudEntry;
import net.minecraft.util.Identifier;

import java.util.List;


public class LobbyStatsHud implements HudEntry {

    public LobbyStatsHud() {

    }

    public void update() {

    }

	@Override
	public void setX(int x) {

	}

	@Override
    public float getScale() {
        return 0;
    }

	@Override
	public int getRawX() {
		return 0;
	}

	@Override
	public int getRawY() {
		return 0;
	}

	@Override
	public void setY(int y) {

	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void setHeight(int height) {

	}

	@Override
	public void setWidth(int width) {

	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		return null;
	}

	@Override
	public OptionCategory getOptionsAsCategory() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void setEnabled(boolean value) {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void renderPlaceholder(float delta) {

	}

	@Override
	public void setHovered(boolean hovered) {

	}

	@Override
	public Identifier getId() {
		return null;
	}
}
