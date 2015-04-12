package net.krazyweb.util;

import java.util.List;

public class StringUtils {

	public static String join(final String separator, final String... strings) {

		String output = "";

		for (int i = 0; i < strings.length; i++) {
			String s = strings[i];
			output += s;
			if (i < strings.length - 1) {
				output += separator;
			}
		}

		return output;

	}

	public static String join(String separator, List<String> strings) {
		return join(separator, strings.toArray(new String[strings.size()]));
	}

}
