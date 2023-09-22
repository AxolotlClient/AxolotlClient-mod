package io.github.axolotlclient.modules.hypixel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.GsonHelper;
import lombok.Getter;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class HypixelMessages implements SimpleSynchronousResourceReloadListener {

	@Getter()
	private static final HypixelMessages Instance = new HypixelMessages();

	@Getter
	private final Map<String, Map<String, Pattern>> languageMessageMap = new HashMap<>();
	private final Map<String, Map<String, Pattern>> messageLanguageMap = new HashMap<>();

	public void load(){
		languageMessageMap.clear();
		messageLanguageMap.clear();

		AxolotlClient.LOGGER.debug("Loading Hypixel Messages");
		ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
		manager.findResources(ResourceType.CLIENT_RESOURCES.toString(), id -> id.endsWith(".hypixel.json")).stream()
			.map(id -> {
				try {
					return manager.getResource(id);
				} catch (IOException e) {
					return null;
				}
			}).filter(Objects::nonNull).forEach(resource -> {
				int i = resource.getId().getPath().lastIndexOf("/")+1;
				String lang = resource.getId().getPath().substring(i, i+5);
			JsonObject lines = GsonHelper.GSON.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
			AxolotlClient.LOGGER.debug("Found message file: "+resource.getId());
			languageMessageMap.computeIfAbsent(lang, l -> new HashMap<>());
			Map<String, Pattern> map = languageMessageMap.get(lang);
			lines.entrySet().forEach(entry -> {
				Pattern pattern = Pattern.compile(entry.getValue().getAsString());
				map.computeIfAbsent(entry.getKey(), s -> pattern);
				messageLanguageMap.computeIfAbsent(entry.getKey(), s -> new HashMap<>());
				messageLanguageMap.get(entry.getKey()).put(lang, pattern);
			});
		});
	}

	public boolean matchesAnyLanguage(String key, String message){
		return messageLanguageMap.get(key).values().stream().map(pattern -> pattern.matcher(message)).anyMatch(Matcher::matches);
	}

	public boolean matchesAnyMessage(String lang, String message){
		return languageMessageMap.get(lang).values().stream().map(pattern -> pattern.matcher(message)).anyMatch(Matcher::matches);
	}

	public boolean matchesAny(String message){
		return languageMessageMap.values().stream().map(Map::values).anyMatch(patterns -> patterns.stream()
			.map(pattern -> pattern.matcher(message)).anyMatch(Matcher::matches));
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("axolotlclient", "hypixel_messages");
	}
	@Override
	public void reload(ResourceManager manager) {
		load();
	}
}
