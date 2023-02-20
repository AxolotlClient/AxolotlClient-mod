package io.github.axolotlclient.api.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.api.Request;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StatusUpdate extends Request {

	protected StatusUpdate(Type updateType, String uuid, JsonObject updateData) {
		super("statusUpdate", object -> {
			// not yet implemented, the response is unclear
		}, new Data("updateType", updateType.getIdentifier(), "uuid", uuid).addElement("update", updateData));
	}

	public static StatusUpdate online(String uuid, MenuId menuId) {
		JsonObject data = new JsonObject();
		data.add("location", new JsonPrimitive(menuId.getIdentifier()));
		return new StatusUpdate(Type.ONLINE, uuid, data);
	}

	public static StatusUpdate inGame(String uuid, String server, String gameType, String gameMode, String map, int players, int maxPlayers, long elapsedSecs) {
		JsonObject object = new JsonObject();
		SupportedServer serv = Arrays.stream(SupportedServer.values()).filter(s -> s.adress.matcher(server).matches()).collect(Collectors.toList()).get(0);
		object.addProperty("server", serv.name);
		object.addProperty("gameType", gameType);
		object.addProperty("gameMode", gameMode);
		object.addProperty("map", map);
		object.addProperty("players", players);
		object.addProperty("maxPlayers", maxPlayers);
		object.addProperty("elapsed", elapsedSecs);
		return new StatusUpdate(Type.IN_GAME, uuid, object);
	}

	@RequiredArgsConstructor
	public enum Type {
		ONLINE("online"),
		OFFLINE("offline"),
		IN_GAME("inGame"),
		IN_GAME_UNKNOWN("inGameUnknown");

		@Getter
		private final String identifier;
	}

	@RequiredArgsConstructor
	public enum MenuId {
		MAIN_MENU("MAIN_MENU"),
		SERVER_LIST("SERVER_LIST"),
		SETTINGS("SETTINGS");
		@Getter
		private final String identifier;
	}

	@RequiredArgsConstructor
	public enum SupportedServer {
		HYPIXEL("HYPIXEL", Pattern.compile("^hypixel.$"));
		private final String name;
		private final Pattern adress;
	}

	@RequiredArgsConstructor
	public enum GameType {
		BLOCKING_DEAD("The Blocking Dead"),
		BOUNTY_HUNTERS("Bounty Hunters"),
		CREEPER_ATTACK("Creeper Attack"),
		CAPTURE_THE_WOOL("Capture The Wool"),
		DRAGON_WARS("Dragon Wars"),
		ENDER_SPLEEF("Ender Spleef"),
		FARM_HUNT("Farm Hunt"),
		FOOTBALL("Football"),
		GALAXY_WARS("Galaxy Wars"),
		HIDE_AND_SEEK("Hide and Seek"),
		HOLE_IN_THE_WALL("Hole in the Wall"),
		HYPIXEL_SAYS("Hypixel Says"),
		MINI_WALLS("Mini Walls"),
		PARTY_GAMES("Party Games"),
		PIXEL_PAINTERS("Pixel Painters"),
		PIXEL_PARTY("Pixel Party"),
		THROW_OUT("Throw Out"),
		ZOMBIES("Zombies"),
		BED_WARS("Bed Wars"),
		BLITZ_SG("Blitz SG"),
		BUILD_BATTLE("Build Battle"),
		ARENA_BRAWL("Arena Brawl"),
		PAINTBALL("Paintball Warfare"),
		QUAKECRAFT("Quakecraft"),
		THE_WALLS("The Walls"),
		TURBO_KART_RACERS("Turbo Kart Racers"),
		VAMPIREZ("VampireZ"),
		COPS_AND_CRIMS("Cops and Crims"),
		BLITZ_DUELS("Blitz Duels"),
		BOW_DUELS("Bow Duels"),
		BOXING_DUELS("Boxing Duels"),
		CLASSIC_DUELS("Classic Duels"),
		COMBO_DUELS("Combo Duels"),
		DUEL_ARENA("Duel Arena"),
		MEGA_WALLS_DUELS("Mega Walls Duels"),
		NODEBUFF_DUELS("NoDebuff Duels"),
		OP_DUELS("OP Duels"),
		PARKOUR_DUELS("Parkour Duels"),
		SKYWARS_DUELS("SkyWars Duels"),
		SUMO_DUELS("Sumo Duels"),
		THE_BRIDGE("The Bridge"),
		TNT_GAMNES_DUELS("TNT Games Duels"),
		UHC_DUELS("UHC Duels"),
		HOUSING("Housing"),
		HYPIXEL_SMP("Hypixel SMP"),
		MEGA_WALLS("Mega Walls"),
		MURDER_MYSTERY("Murder Mystery"),
		DROPPER("Dropper"),
		SKYBLOCK("Skyblock"),
		SKYWARS("SkyWars"),
		SMASH_HEROES("Smash Heroes"),
		HYPIXEL_PIT("The Hypixel Pit"),
		BOW_SPLEEF("Bow Spleef"),
		PVP_RUN("PVP Run"),
		TNT_RUN("TNT Run"),
		TNT_TAG("TNT Tag"),
		WIZARDS("Wizards"),
		UHC_CHAMPIONS("UHC Champions"),
		SPEED_UHC("Speed UHC"),
		CAPTURE_THE_FLAG("Capture The Flag"),
		WOOL_WARS("Wool Wars");
		@Getter
		private final String name;
	}
}
