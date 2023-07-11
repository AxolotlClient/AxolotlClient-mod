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


import java.util.regex.Pattern;

import io.github.axolotlclient.AxolotlClientConfig.Color;

/**
 * @author DarkKronicle
 */

public class BedwarsTeamUpgrades {

	public final TrapUpgrade trap = new TrapUpgrade();

	public final TeamUpgrade sharpness = new BinaryUpgrade(
		"sharp", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Sharpened Swords"),
		8, 4, new TextureInfo("textures/items/stone_sword.png"), new TextureInfo("textures/items/diamond_sword.png")
	);

	public final TeamUpgrade dragonBuff = new BinaryUpgrade(
		"dragonbuff", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Dragon Buff\\s*$"),
		5, 5, new TextureInfo("textures/items/end_crystal.png", Color.DARK_GRAY),
		new TextureInfo("textures/items/end_crystal.png")
	);

	public final TeamUpgrade healPool = new BinaryUpgrade(
		"healpool", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Heal Pool\\s*$"),
		3, 1, new TextureInfo("textures/gui/container/inventory.png", 7*18, 198, 256, 256, 18, 18, Color.DARK_GRAY),
		new TextureInfo("textures/gui/container/inventory.png", 7*18, 198, 256, 256, 18, 18)
	);

	public final TeamUpgrade protection = new TieredUpgrade(
		"prot", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Reinforced Armor .{1,3}\\s*$"),
		new int[]{5, 10, 20, 30}, new int[]{2, 4, 8, 16}, new TextureInfo[]{
		new TextureInfo("textures/gui/container/inventory.png", 6*18, 198 + 18, 256, 256, 18, 18, Color.DARK_GRAY.withAlpha(100)),
		new TextureInfo("textures/gui/container/inventory.png", 6*18, 198 + 18, 256, 256, 18, 18),
		new TextureInfo("textures/gui/container/inventory.png", 6*18, 198 + 18, 256, 256, 18, 18, Color.parse("#FFFF00")),
		new TextureInfo("textures/gui/container/inventory.png", 6*18, 198 + 18, 256, 256, 18, 18, Color.parse("#00FF00")),
		new TextureInfo("textures/gui/container/inventory.png", 6*18, 198 + 18, 256, 256, 18, 18, Color.parse("#FF0000")),
	}
	);

	public final TeamUpgrade maniacMiner = new TieredUpgrade(
		"haste", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Maniac Miner .{1,3}\\s*$"),
		new int[]{2, 4}, new int[]{4, 6}, new TextureInfo[]{
		new TextureInfo("textures/gui/container/inventory.png", 2*18, 198, 256, 256, 18, 18, Color.DARK_GRAY),
		new TextureInfo("textures/gui/container/inventory.png", 2*18, 198, 256, 256, 18, 18, Color.GRAY),
		new TextureInfo("textures/gui/container/inventory.png", 2*18, 198, 256, 256, 18, 18),
	}
	);

	public final TeamUpgrade forge = new TieredUpgrade(
		"forge", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (?:Iron|Golden|Emerald|Molten) Forge\\s*$"),
		new int[]{2, 4}, new int[]{4, 6}, new TextureInfo[]{
		new TextureInfo("textures/blocks/furnace_front_off.png", 6*18, 198 + 18, 18, 18, Color.DARK_GRAY),
		new TextureInfo("textures/blocks/furnace_front_on.png", 6*18, 198 + 18, 18, 18),
		new TextureInfo("textures/blocks/furnace_front_on.png", 6*18, 198 + 18, 18, 18, Color.parse("#FFFF00")),
		new TextureInfo("textures/blocks/furnace_front_on.png", 6*18, 198 + 18, 18, 18, Color.parse("#00FF00")),
		new TextureInfo("textures/blocks/furnace_front_on.png", 6*18, 198 + 18, 18, 18, Color.parse("#FF0000")),
	}
	);

	public final TeamUpgrade[] upgrades = {trap, sharpness, dragonBuff, healPool, protection, maniacMiner, forge};

	public BedwarsTeamUpgrades() {

	}

	public void onMessage(String rawMessage) {
		for (TeamUpgrade upgrade : upgrades) {
			if (upgrade.match(rawMessage)) {
				return;
			}
		}
	}

}
