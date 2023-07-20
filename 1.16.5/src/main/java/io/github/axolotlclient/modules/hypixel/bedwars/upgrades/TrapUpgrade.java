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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.axolotlclient.AxolotlClientConfig.Color;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author DarkKronicle
 */

public class TrapUpgrade extends TeamUpgrade {

	private final static Pattern[] REGEX = {
		Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap.?\\s*$"),
		Pattern.compile("Trap was set (off)!"),
	};

	private final List<TrapType> traps = new ArrayList<>(3);

	public TrapUpgrade() {
		super("trap", REGEX);
	}

	@Override
	protected void onMatch(TeamUpgrade upgrade, Matcher matcher) {
		if (matcher.group(1).equals("off")) {
			// Trap went off
			traps.remove(0);
			return;
		}
		traps.add(TrapType.getFuzzy(matcher.group(1)));
	}

	public boolean canPurchase() {
		return traps.size() < 3;
	}

	@Override
	public int getPrice(BedwarsMode mode) {
		switch (traps.size()) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 4;
		}
		;
		return 0;
	}

	@Override
	public boolean isPurchased() {
		return traps.size() > 0;
	}

	@Override
	public TextureInfo[] getTexture() {
		if (traps.size() == 0) {
			return new TextureInfo[]{new TextureInfo("textures/items/barrier.png", Color.DARK_GRAY)};
		}
		TextureInfo[] trapTextures = new TextureInfo[traps.size()];
		for (int i = 0; i < traps.size(); i++) {
			TrapType type = traps.get(i);
			trapTextures[i] = type.getTexInfo();
		}
		return trapTextures;
	}

	@Override
	public boolean isMultiUpgrade() {
		return true;
	}

	@AllArgsConstructor
	public enum TrapType {
		ITS_A_TRAP(new TextureInfo("textures/mob_effect/blindness.png", 0, 0, 18, 18)),
		COUNTER_OFFENSIVE(new TextureInfo("textures/mob_effect/speed.png", 0, 0, 18, 18)),
		ALARM(new TextureInfo("textures/item/ender_eye.png")),
		MINER_FATIGUE(new TextureInfo("textures/mob_effect/mining_fatigue.png", 0, 0, 18, 18));

		@Getter
		private final TextureInfo texInfo;

		public static TrapType getFuzzy(String s) {
			s = s.toLowerCase(Locale.ROOT);
			if (s.contains("miner")) {
				return MINER_FATIGUE;
			}
			if (s.contains("alarm")) {
				return ALARM;
			}
			if (s.contains("counter")) {
				return COUNTER_OFFENSIVE;
			}
			return ITS_A_TRAP;
		}
	}
}
