/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.hypixel;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/*
 * Yes, this looked a lot cleaner as just records.
 * However, the gson versions shipped by 1.8.9 as well as 1.16_combat-6 do not support deserialization to records
 * (it was added in gson 2.10.0).
 * Therefore, the only alternative would be to ship gson with those versions.
 */

@SuppressWarnings("ClassCanBeRecord")

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
public final class PlayerData {
	private final String name;
	private final Bedwars bedwars;
	private final Skywars skywars;
	private final DuelsData duels;
	private final String rank;
	private final String rankFormatted;
	private final double level;
	private final int karma;

	public String formattedName() {
		StringBuilder builder = new StringBuilder();
		builder.append(rankFormatted());
		if (rankFormatted().length() > 2) {
			builder.append(" ");
		}
		return builder.append(name()).append("§r").toString();
	}

	@AllArgsConstructor
	@Getter
	@Accessors(fluent = true)
	@ToString
	@EqualsAndHashCode
	public static final class Bedwars {
		private final int level;
		private final GameData all;
		private final CombinedGameData core;
		private final GameData solo;
		private final GameData doubles;
		private final GameData trios;
		private final GameData fours;
		private final GameData fourVFour;
		private final CombinedGameData dreams;
		private final GameData castle;
		private final GameData doublesLucky;
		private final GameData foursLucky;
		private final GameData doublesUltimate;
		private final GameData foursUltimate;
		private final GameData doublesArmed;
		private final GameData foursArmed;
		private final GameData doublesRush;
		private final GameData foursRush;
		private final GameData doublesSwap;
		private final GameData foursSwap;

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class GameData implements BedwarsGameData {
			private final int kills;
			private final int deaths;
			private final int wins;
			private final int losses;
			private final int winstreak;
			private final int finalKills;
			private final int finalDeaths;
			private final int bedsBroken;
			private final int bedsLost;
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class CombinedGameData implements BedwarsGameData {
			private final int kills;
			private final int deaths;
			private final int wins;
			private final int losses;
			private final int finalKills;
			private final int finalDeaths;
			private final int bedsBroken;
			private final int bedsLost;
		}

		public interface BedwarsGameData extends WLR, KDR {
			int bedsBroken();

			int bedsLost();

			int finalDeaths();

			int finalKills();

			default float fkdr() {
				return (float) finalKills() / finalDeaths();
			}

			default float bblr() {
				return (float) bedsBroken() / bedsLost();
			}
		}
	}

	@AllArgsConstructor
	@Getter
	@Accessors(fluent = true)
	@ToString
	@EqualsAndHashCode
	public static final class Skywars {
		private final String level;
		private final int exp;
		private final GameData all;
		private final GameData core;
		private final ModeData solo;
		private final ModeData team;
		private final MegaModeData mega;
		private final GameData ranked;
		private final int winstreak;

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class ModeData {
			private final GameData normal;
			private final GameData insane;
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class MegaModeData {
			private final GameData normal;
			private final GameData doubles;
		}

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class GameData implements KDR, WLR {
			private final int kills;
			private final int deaths;
			private final int wins;
			private final int losses;
		}
	}

	@AllArgsConstructor
	@Getter
	@Accessors(fluent = true)
	@ToString
	@EqualsAndHashCode
	public static final class DuelsData {
		private final Map<String, DuelsGameData> modes;

		@AllArgsConstructor
		@Getter
		@Accessors(fluent = true)
		@ToString
		@EqualsAndHashCode
		public static final class DuelsGameData implements KDR, WLR {
			private final int kills;
			private final int deaths;
			private final int wins;
			private final int losses;
			private final int winstreak;
		}
	}

	public interface KDR {
		int kills();

		int deaths();

		default float kdr() {
			return (float) kills() / deaths();
		}
	}

	public interface WLR {
		int wins();

		int losses();

		default float wlr() {
			return (float) wins() / losses();
		}
	}
}
