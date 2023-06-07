package io.github.axolotlclient.api;

import java.util.ArrayList;
import java.util.List;

public class Keyword {
	public static String get(String formatString) {
		StringBuilder result = new StringBuilder();

		StringBuilder keyword = new StringBuilder();

		StringBuilder replacement = new StringBuilder();
		List<String> replacements = new ArrayList<>();

		boolean inSection = false;
		boolean escape = false;
		boolean inKeywords = false;
		for (char c : formatString.toCharArray()) {

			if (c == '\\' && !escape) {
				escape = true;
				continue;
			} else if (inSection) {
				if (c == ']' && !escape) {
					result.append(
						API.getInstance().getTranslationProvider()
							.translate(keyword.toString(), (Object[]) replacements.toArray(new String[0])));
					inSection = false;
				} else if (c == ':' && !escape) {
					inKeywords = true;
					if (replacement.length() != 0) {
						replacements.add(replacement.toString());
						replacement = new StringBuilder();
					}
				} else if (!inKeywords) {
					keyword.append(c);
				} else {
					replacement.append(c);
				}
			} else {
				if (c == '[' && !escape) {
					inSection = true;
				}
				result.append(c);
			}
			escape = false;
		}
		return result.toString();
	}
}
