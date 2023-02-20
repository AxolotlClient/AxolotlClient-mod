package io.github.axolotlclient.util.translation;

public interface TranslationProvider {

	String translate(String key, Object... args);
}
