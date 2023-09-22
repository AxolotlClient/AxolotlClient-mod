package io.github.axolotlclient.modules.hypixel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.GsonHelper;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

public class HypixelMessages implements SimpleSynchronousResourceReloader {

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
		manager.findResources("assets/lang", id -> id.getPath().endsWith(".hypixel.json"))
			.forEach((id, resource) -> {
				int i = id.getPath().lastIndexOf("/")+1;
				String lang = id.getPath().substring(i, i+5);
			JsonObject lines = null;
			try {
				lines = GsonHelper.GSON.fromJson(new InputStreamReader(resource.open()), JsonObject.class);
			} catch (IOException ignored) {
			}
			if (lines == null){
				lines = new JsonObject();
			}
			languageMessageMap.computeIfAbsent(lang, l -> new HashMap<>());
			AxolotlClient.LOGGER.debug("Found message file: "+id);
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
	public @NotNull Identifier getQuiltId() {
		return new Identifier("axolotlclient", "hypixel_messages");
	}
	@Override
	public void reload(ResourceManager manager) {
		load();
	}
}
