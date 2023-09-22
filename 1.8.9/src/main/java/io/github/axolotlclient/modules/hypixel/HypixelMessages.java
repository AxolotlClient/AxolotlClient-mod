package io.github.axolotlclient.modules.hypixel;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.GsonHelper;
import io.github.moehreag.searchInResources.SearchableResourceManager;
import lombok.Getter;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.legacyfabric.fabric.api.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;

public class HypixelMessages implements IdentifiableResourceReloadListener {

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
		((SearchableResourceManager) manager).findResources("axolotlclient", "lang",
			identifier -> identifier.getPath().endsWith(".hypixel.json")).values().forEach(resource -> {
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
	public void reload(ResourceManager resourceManager) {
		load();
	}
}
