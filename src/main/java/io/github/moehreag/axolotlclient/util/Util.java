package io.github.moehreag.axolotlclient.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Util {

	public static String lastgame;
	public static String game;

	public static String getGame(){

		List<String> sidebar = getSidebar();

		if(sidebar.isEmpty()) game = "";
		else if (MinecraftClient.getInstance().getCurrentServerEntry() != null && MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase().contains(sidebar.get(0).toLowerCase())){
			if ( sidebar.get(sidebar.size() -1).contains(MinecraftClient.getInstance().getCurrentServerEntry().address) || sidebar.get(sidebar.size()-1).contains("Playtime")){
				game = "In Lobby";
			}  else {
				if (sidebar.get(sidebar.size()-1).contains("--------")){
					game = "Playing Bridge Practice";
				} else {
					game = "Playing "+ sidebar.get(sidebar.size() -1);
				}
			}
		} else {
			game = "Playing "+ sidebar.get(0);
		}

		if(!Objects.equals(lastgame, game) && game.equals("")) game = lastgame;
		else lastgame = game;

		return game;
	}


	public static List<String> getSidebar() {
		List<String> lines = new ArrayList<>();
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) return lines;

		Scoreboard scoreboard = client.world.getScoreboard();
		if (scoreboard == null) return lines;
		ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
		if (sidebar == null) return lines;

		Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
		List<ScoreboardPlayerScore> list = scores.stream()
			.filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
			.collect(Collectors.toList());

		if (list.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
		} else {
			scores = list;
		}

		for (ScoreboardPlayerScore score : scores) {
			Team team = scoreboard.getPlayerTeam(score.getPlayerName());
			if (team == null) return lines;
			String text = team.getPrefix().getString() + team.getSuffix().getString();
			if (text.trim().length() > 0)
				lines.add(text);
		}

		lines.add(sidebar.getDisplayName().getString());
		Collections.reverse(lines);

		return lines;
	}
}
