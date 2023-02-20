package io.github.axolotlclient.util.translation;

import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class Translations implements TranslationProvider {

	@Getter
	private static final TranslationProvider Instance = new Translations();

	@Override
	public String translate(String key, Object... args) {
		return I18n.translate(key, args);
	}
}
