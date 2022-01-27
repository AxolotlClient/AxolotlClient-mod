package io.github.moehreag.axolotlclient.util;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.MinecraftClient;
import java.util.Objects;

public class DiscordRPC {

	private static String rpcstate;
	private static long time;

	private static final DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
		.setReadyEventHandler((user) -> Axolotlclient.LOGGER.info("Discord RPC Connected to user "+ user.username + "#" + user.discriminator))
		.build();

	public static void startup() {
		net.arikia.dev.drpc.DiscordRPC.discordInitialize("875835666729152573", handlers, true);
		net.arikia.dev.drpc.DiscordRPC.discordRegister("875835666729152573", "");

		menu();
	}

	public static void update(){

		if (MinecraftClient.getInstance().world == null){


			menu();
		} else {

			inGame();
		}
	}

	public static void menu(){
		if (!Objects.equals(rpcstate, "menu")) time = System.currentTimeMillis()/1000L;

		DiscordRichPresence rpc = new DiscordRichPresence.Builder("In the Menu")
			.setBigImage("icon","Axolotlclient " + MinecraftClient.getInstance().getGameVersion())
			.setStartTimestamps(time)
			.build();
		net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);

		rpcstate = "menu";
	}

	public static void inGame(){

		if(MinecraftClient.getInstance().getCurrentServerEntry() != null){
			String game = Util.getGame();
			if(!Objects.equals(rpcstate, game))time = System.currentTimeMillis()/1000L;

			rpcstate = game;

			DiscordRichPresence rpc = new DiscordRichPresence.Builder(MinecraftClient.getInstance().getCurrentServerEntry().address)
				.setBigImage("icon", "Axolotlclient " + MinecraftClient.getInstance().getGameVersion())
				.setDetails(game)
				.setStartTimestamps(time)
				.build();
			net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);
		} else {

			if (!Objects.equals(rpcstate, "sp")) time = System.currentTimeMillis()/1000L;

			rpcstate = "sp";

			DiscordRichPresence rpc = new DiscordRichPresence.Builder("Having fun!")
				.setBigImage("icon", "Axolotlclient " + MinecraftClient.getInstance().getGameVersion())
				.setDetails("Singleplayer")
				.setStartTimestamps(time)
				.build();
			net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(rpc);
		}


	}

	public static void shutdown(){
		net.arikia.dev.drpc.DiscordRPC.discordShutdown();
	}
}
