package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.util.Util;

import java.util.function.Consumer;

public class HypixelLocation {

	private static boolean waiting;
	private static Consumer<String> consumer;

	public static void get(Consumer<String> location){
		Util.sendChatMessage("/locraw");
		waiting = true;
		consumer = location;
	}

	public static boolean waitingForResponse(String message){
		boolean consume = waiting && message.startsWith("{") && message.endsWith("}") && message.contains("gameType") && consumer != null;
		if(consume){
			consumer.accept(message);
			consumer = null;
		}
		return consume;
	}
}
