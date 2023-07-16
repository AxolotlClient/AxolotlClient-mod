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

package io.github.axolotlclient.modules.hypixel.bedwars.upgrades;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import lombok.Getter;

@Getter
public class TextureInfo {
	private final String texture;
	private int u = 0, v = 0, width = 16, height = 16, regionWidth = width, regionHeight = height;
	private Color color = Color.WHITE.withAlpha(255);

	public TextureInfo(String texture){
		this.texture = texture;
	}

	public TextureInfo(String texture, int u, int v){
		this(texture);
		this.u = u;
		this.v = v;
	}

	public TextureInfo(String texture, int u, int v, int width, int height){
		this(texture, u, v);
		this.width = width;
		this.height = height;
		this.regionWidth = width;
		this.regionHeight = height;
	}

	public TextureInfo(String texture, int u, int v, int width, int height, int regionWidth, int regionHeight){
		this(texture, u, v, width, height);
		this.regionWidth = regionWidth;
		this.regionHeight = regionHeight;
	}

	public TextureInfo(String texture, Color color){
		this(texture);
		this.color = color;
	}

	public TextureInfo(String texture, int u, int v, Color color){
		this(texture, u, v);
		this.color = color;
	}

	public TextureInfo(String texture, int u, int v, int width, int height, Color color){
		this(texture, u, v, width, height);
		this.color = color;
	}

	public TextureInfo(String texture, int u, int v, int width, int height, int regionWidth, int regionHeight, Color color){
		this(texture, u, v, width, height, regionWidth, regionHeight);
		this.color = color;
	}
}
