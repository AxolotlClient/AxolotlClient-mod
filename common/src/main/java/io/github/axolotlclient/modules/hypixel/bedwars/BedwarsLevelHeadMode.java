package io.github.axolotlclient.modules.hypixel.bedwars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public enum BedwarsLevelHeadMode {
	FINAL_KILLS(stats -> "§7Final Kills (Total): §f" + stats.getFinalKills()),
	FINAL_DEATHS(stats -> "§7Final Deaths (Total): §f" + stats.getFinalDeaths()),
	BEDS_BROKEN(stats -> "§7Beds Broken (Total): §f" + stats.getBedsBroken()),
	GAME_KILLS_GAME_DEATHS(stats -> "§7Kills (Game): §f" + stats.getGameKills() + " §7Deaths (Game): §f" + stats.getGameDeaths()),
	KILLS(stats -> "§7Kills (Total): §f" + stats.getKills()),
	DEATHS(stats -> "§7Deaths (Total): §f" + stats.getDeaths()),
	GAME_FINAL_KILLS(stats -> "§7Final Kills (Game): §f" + stats.getGameFinalKills()),
	GAME_BEDS_BROKEN(stats -> "§7Beds Broken (Game): §f" + stats.getGameBedsBroken()),
	GAME_DEATHS(stats -> "§7Deaths (Game): §f" + stats.getGameDeaths()),
	GAME_KILLS(stats -> "§7Kills (Game): §f" + stats.getGameKills()),
	LOSSES(stats -> "§7Losses: §f" + stats.getLosses()),
	WINS(stats -> "§7Wins: §f" + stats.getWins()),
	WINSTREAK(stats -> "§7Winstreak: §f" + stats.getWinstreak()),
	STARS(stats -> "§7Stars: §f" + stats.getStars());

	private final Function<BedwarsPlayerStats, String> titleSupplier;

	BedwarsLevelHeadMode(Function<BedwarsPlayerStats, String> titleSupplier) {
		this.titleSupplier = titleSupplier;
	}

	public String apply(BedwarsPlayerStats stats) {
		return titleSupplier.apply(stats);
	}

	private static final HashMap<String, BedwarsLevelHeadMode> modes;

	static {
		modes = new HashMap<>();
		Arrays.stream(values()).forEach(m -> modes.put(m.toString(), m));
	}

	public static BedwarsLevelHeadMode get(String mode) {
		return modes.getOrDefault(mode, null);
	}
}
