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

package io.github.axolotlclient.modules.hud.util;

import lombok.experimental.Accessors;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

/*
 * Stores a basic rectangle.
 */

@Accessors(fluent = true)
public class Rectangle extends io.github.axolotlclient.AxolotlClientConfig.common.util.Rectangle {

	public Rectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public Rectangle offset(DrawPosition offset) {
		return new Rectangle(x + offset.x(), y + offset.y(), width, height);
	}

	public Rectangle offset(int x, int y) {
		return new Rectangle(this.x + x, this.y + y, width, height);
	}

	public int x(){
		return x;
	}

	public int y(){
		return y;
	}

	public int width(){
		return width;
	}

	public int height(){
		return height;
	}
}
