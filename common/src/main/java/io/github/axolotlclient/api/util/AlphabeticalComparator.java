package io.github.axolotlclient.api.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class AlphabeticalComparator implements Comparator<String> {
	@Override
	public int compare(String s1, String s2) {
		if (s1.equals(s2))
			return 0;
		String[] strings = {s1, s2};
		Arrays.sort(strings, Collections.reverseOrder());

		if (strings[0].equals(s1))
			return 1;
		else
			return -1;
	}
}
