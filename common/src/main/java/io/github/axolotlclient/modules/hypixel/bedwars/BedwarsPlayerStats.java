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

package io.github.axolotlclient.modules.hypixel.bedwars;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * @author DarkKronicle
 */

@AllArgsConstructor
public class BedwarsPlayerStats {

	@Getter
	private int finalKills;
	@Getter
	private int finalDeaths;
	@Getter
	private int bedsBroken;
	@Getter
	private int deaths;
	@Getter
	private int kills;
	@Getter
	private int gameFinalKills;
	@Getter
	private int gameFinalDeaths;
	@Getter
	private int gameBedsBroken;
	@Getter
	private int gameDeaths;
	@Getter
	private int gameKills;
	@Getter
	private final int losses;
	@Getter
	private final int wins;
	@Getter
	private final int winstreak;
	@Getter
	private final int stars;


	public static BedwarsPlayerStats generateFake(String name) {
		long seed = 0;
		for(int i = 0; i<name.length(); i++){
			seed = (seed << 2) +  name.getBytes(StandardCharsets.UTF_8)[i];
		}
		Random random = new Random(seed);
		int star = (int) getGaussian(random, 150, 30);
		double fkdr = Math.min(getGaussian(random, 1.3F, 0.5F), 0.6F);
		double bblr = (fkdr * 8) / getGaussian(random, 10, 2);
		int wins = (int) (star * (fkdr * 4) * random.nextFloat(0.95F, 1.05F));
		int losses = (int) (wins * (2/fkdr) * random.nextFloat(0.95F, 1.05F));
		int beds = (int) (bblr * losses);
		int finalDeaths = (int) (losses * random.nextFloat(1F, 1.03F));
		int deaths = (int) (finalDeaths * random.nextFloat(8, 20));
		int finalKills = (int) (deaths * fkdr);
		int kills = (int) (finalKills * random.nextFloat(1, 2));

		return new BedwarsPlayerStats(finalKills, finalDeaths, beds, deaths, kills,
			0, 0, 0, 0, 0,
			losses, wins, 0, star);
	}

	private static double getGaussian(Random random, float mean, float deviation){
		return Math.max(Math.min(random.nextGaussian()+mean*deviation, mean - deviation*3), mean + deviation*3);
	}

	@Nullable
	public static BedwarsPlayerStats fromAPI(String uuid) {
		JsonElement rawStats = HypixelAbstractionLayer.getPlayerProperty(uuid, "stats");
		if (rawStats == null || !rawStats.isJsonObject()) {
			return null;
		}
		JsonObject stats = rawStats.getAsJsonObject();
		JsonObject bedwars = getObjectSafe(stats, "Bedwars");
		if (bedwars == null) {
			return null;
		}
		int finalKills = getAsIntElse(bedwars, "final_kills_bedwars", 0);
		int finalDeaths = getAsIntElse(bedwars, "final_deaths_bedwars", 0);
		int bedsBroken = getAsIntElse(bedwars, "beds_broken_bedwars", 0);
		int deaths = getAsIntElse(bedwars, "deaths_bedwars", 0);
		int kills = getAsIntElse(bedwars, "kills_bedwars", 0);
		int losses = getAsIntElse(bedwars, "losses_bedwars", 0);
		int wins = getAsIntElse(bedwars, "wins_bedwars", 0);
		int winstreak = getAsIntElse(bedwars, "winstreak", 0);
		JsonObject achievements = HypixelAbstractionLayer.getPlayerProperty(uuid, "achievements");
		int stars = 1;
		if (achievements != null) {
			stars = getAsIntElse(achievements, "bedwars_level", 1);
		}
		return new BedwarsPlayerStats(finalKills, finalDeaths, bedsBroken, deaths, kills, 0, 0, 0, 0, 0, losses, wins, winstreak, stars);
	}

	public static int getAsIntElse(JsonObject obj, String key, int other) {
		if (obj.has(key)) {
			try {
				return obj.get(key).getAsInt();
			} catch (NumberFormatException | UnsupportedOperationException | IllegalStateException e) {
				// Not actually an int
			}
		}
		return other;
	}

	public static JsonObject getObjectSafe(JsonObject object, String key) {
		if (!object.has(key)) {
			return null;
		}
		JsonElement el = object.get(key);
		if (!el.isJsonObject()) {
			return null;
		}
		return el.getAsJsonObject();
	}

	public void addDeath() {
		deaths++;
		gameDeaths++;
	}

	public void addFinalDeath() {
		finalDeaths++;
		gameFinalDeaths++;
	}

	public void addKill() {
		kills++;
		gameKills++;
	}

	public void addFinalKill() {
		finalKills++;
		gameFinalKills++;
	}

	public void addBed() {
		bedsBroken++;
		gameBedsBroken++;
	}

	public float getFKDR() {
		return (float) finalKills / finalDeaths;
	}

	public float getBBLR() {
		return (float) bedsBroken / losses;
	}

}
