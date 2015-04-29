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

	public static void sortByClosestMatch(final List<String> strings, final String toMatch) {

		String toFind = toMatch.replaceAll("[_\\s]", "");

		strings.sort((string1, string2) -> {

			double distance1 = getScore(string1, toFind);
			double distance2 = getScore(string2, toFind);

			if (distance1 > distance2) {
				return -1;
			} else if (distance1 < distance2) {
				return 1;
			}

			if (Math.abs(distance1 - distance2) < 0.00001) {
				int length1 = Math.abs(string1.length() - toFind.length());
				int length2 = Math.abs(string2.length() - toFind.length());
				if (length1 < length2) {
					return -1;
				} else if (length1 > length2) {
					return 1;
				}
			}

			return string1.compareTo(string2);

		});

	}

	//TODO Clean this up and optimize it
	private static double getScore(final String string, final String toFind) {

		String toTest = string.replaceAll("_", "");

		double distance = org.apache.commons.lang3.StringUtils.getJaroWinklerDistance(toTest, toFind);

		int longestMatch = -1;
		int numberOfMatches = 0;

		for (int x = 0; x < toFind.length(); x++) {
			for (@SuppressWarnings("SuspiciousNameCombination") int y = x; y <= toFind.length(); y++) {

				if (x == y) {
					continue;
				}

				String sub = toFind.substring(x, y);

				if (toTest.contains(sub) && sub.length() > longestMatch) {
					longestMatch = sub.length();
					numberOfMatches = 1;
				} else if (toTest.contains(sub) && sub.length() == longestMatch) {
					numberOfMatches++;
				}

			}
		}

		distance -= (toTest.length() / (toFind.length() + 0.000001)) * 0.2;

		double percent = (double) longestMatch / toFind.length();
		distance += (percent * (numberOfMatches * 1.5) + longestMatch);

		//% of toFind's characters in toTest
		String toButcher = toTest;
		String matches = "";
		for (int i = 0; i < toFind.length(); i++) {
			char c = toFind.charAt(i);
			if (toButcher.indexOf(c) > -1) {
				toButcher = toButcher.replaceFirst(String.valueOf(c), "");
				matches += String.valueOf(c);
			}
		}

		percent = (double) matches.length() / toFind.length();
		distance *= percent;

		int furthestIndex = 0;
		String found = "";
		for (int i = 0; i < toFind.length(); i++) {
			int index = toTest.indexOf(toFind.charAt(i), furthestIndex);
			if (index == -1) {
				index = toTest.indexOf(toFind.charAt(i));
				found = toFind.charAt(i) + found;
			} else if (index > -1) {
				found += toFind.charAt(i);
			}
			if (index > furthestIndex) {
				furthestIndex = index;
			}
		}

		furthestIndex = 0;
		double score = 1;
		String temp = toFind;

		for (int i = 0; i < found.length(); i++) {

			int index = temp.indexOf(found.charAt(i), furthestIndex);

			if (index == -1) {
				index = temp.indexOf(found.charAt(i));
			}

			if (index > furthestIndex) {
				furthestIndex = index;
			} else if (index < furthestIndex) {
				score *= 0.8;
			}

			temp = temp.substring(0, index) + temp.substring(index + 1, temp.length());

		}

		percent = (double) found.length() / toFind.length();
		distance += (percent * score);

		return distance;

	}

}
