package net.krazyweb.util;

import java.util.*;

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

		subStrings.clear();

		String toFind = toMatch.replaceAll("[_\\s]", "").toLowerCase();

		Map<String, Double> scores = new HashMap<>();

		strings.sort((string1, string2) -> {

			if (!scores.containsKey(string1)) {
				scores.put(string1, getScore(string1, toFind));
			}

			if (!scores.containsKey(string2)) {
				scores.put(string2, getScore(string2, toFind));
			}

			double distance1 = scores.get(string1);
			double distance2 = scores.get(string2);

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

	private static List<String> subStrings = new ArrayList<>();
	private static String[] subs;
	private static int[] subLengths;

	//TODO Clean this up
	private static double getScore(final String string, final String toFind) {

		String toTest = string.replaceAll("_", "");

		double distance = 1;

		int longestMatch = -1;
		int numberOfMatches = 0;

		int toTestLength = toTest.length();
		int toFindLength = toFind.length();

		if (subStrings.isEmpty()) {

			for (int x = 0; x < toFindLength; x++) {
				for (@SuppressWarnings("SuspiciousNameCombination") int y = x; y <= toFindLength; y++) {

					if (x == y) {
						continue;
					}

					String sub = toFind.substring(x, y);
					if (toFindLength < 4 || sub.length() > 1) {
						subStrings.add(sub);
					}

				}
			}

			subStrings.sort((o1, o2) -> {
				if (o1.length() > o2.length()) {
					return -1;
				} else if (o1.length() < o2.length()) {
					return 1;
				}
				return 0;
			});

			subs = new String[subStrings.size()];
			subLengths = new int[subStrings.size()];
			for (int i = 0; i < subStrings.size(); i++) {
				subs[i] = subStrings.get(i);
				subLengths[i] = subs[i].length();
			}

		}

		boolean foundSubstring = false;
		int matchIndex, bestIndex = Integer.MAX_VALUE;

		for (int i = 0; i < subStrings.size(); i++) {

			String sub = subs[i];
			int subLength = subLengths[i];

			if (subLength > toTestLength) {
				continue;
			} if (subLength > longestMatch && (matchIndex = toTest.indexOf(sub)) > -1) {
				longestMatch = subLength;
				numberOfMatches = 1;
				foundSubstring = true;
				if (matchIndex < bestIndex) {
					bestIndex = matchIndex;
				}
			} else if (subLength == longestMatch && (matchIndex = toTest.indexOf(sub)) > -1) {
				numberOfMatches++;
				if (matchIndex < bestIndex) {
					bestIndex = matchIndex;
				}
			} else if (foundSubstring) {
				break;
			}

			//if (attempts++ > 0) {
			//	break;
			//}

		}

		distance -= (double) bestIndex / 35.0;
		distance -= (toTestLength / (toFindLength + 0.000001)) * 0.2;

		double percent = (double) longestMatch / toFindLength;
		distance += (percent * (numberOfMatches * 1.5) + longestMatch);

		//% of toFind's characters in toTest
		List<Character> toFindChars = new ArrayList<>();
		for (char c : toFind.toCharArray()) {
			toFindChars.add(c);
		}

		int foundCount = 0;
		for (char c : toTest.toCharArray()) {
			if (toFindChars.remove((Character) c)) {
				foundCount++;
				if (foundCount == toFindLength) {
					break;
				}
			}
		}

		distance *= ((double) foundCount / toFindLength);

		int furthestIndex = 0;
		String found = "";
		for (int i = 0; i < toFindLength; i++) {
			int index = toTest.indexOf(toFind.charAt(i), furthestIndex);
			char c = toFind.charAt(i);
			if (index == -1) {
				index = toTest.indexOf(c);
				found = c + found;
			} else if (index > -1) {
				found += c;
			}
			if (index > furthestIndex) {
				furthestIndex = index;
			}
		}

		furthestIndex = 0;
		double score = 1;

		for (int i = 0; i < found.length(); i++) {

			int index = toFind.indexOf(found.charAt(i), furthestIndex);

			if (index == -1) {
				index = toFind.indexOf(found.charAt(i));
			}

			if (index > furthestIndex) {
				furthestIndex = index;
			} else if (index < furthestIndex) {
				score *= 0.5;
			}

		}

		percent = (double) found.length() / toFindLength;
		distance += (percent * score);

		return distance;

	}

}
