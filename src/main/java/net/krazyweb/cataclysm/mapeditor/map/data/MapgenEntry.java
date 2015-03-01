package net.krazyweb.cataclysm.mapeditor.map.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import net.krazyweb.util.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MapgenEntry implements Jsonable {

	private static final char[] SYMBOLS = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!',
			'@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', '{', ']', '}', ':', ';', '"', '\'',
			'<', ',', '>', '.', '?', '/', '`', '~', '\\', '|', ' ',
	};

	private static Logger log = LogManager.getLogger(MapgenEntry.class);

	public String[][] terrain = new String[MapEditor.SIZE][MapEditor.SIZE];
	public String[][] furniture = new String[MapEditor.SIZE][MapEditor.SIZE];
	public List<PlaceGroupZone> placeGroupZones = new ArrayList<>();
	public MapSettings settings = new MapSettings();

	private MapgenEntry lastSavedState;

	public MapgenEntry() {

	}

	public MapgenEntry(final MapgenEntry mapgenEntry) {
		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				terrain[x][y] = mapgenEntry.terrain[x][y];
				furniture[x][y] = mapgenEntry.furniture[x][y];
			}
		}
		mapgenEntry.placeGroupZones.forEach(zone -> placeGroupZones.add(new PlaceGroupZone(zone)));
		settings = new MapSettings(mapgenEntry.settings);
	}

	public boolean isSaved() {
		return this.equals(lastSavedState);
	}

	public void markSaved() {
		lastSavedState = new MapgenEntry(this);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapgenEntry mapgenEntry = (MapgenEntry) o;

		if (placeGroupZones != null ? !placeGroupZones.equals(mapgenEntry.placeGroupZones) : mapgenEntry.placeGroupZones != null) {
			return false;
		}

		if (settings != null ? !settings.equals(mapgenEntry.settings) : mapgenEntry.settings != null) {
			return false;
		}

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (!mapgenEntry.terrain[x][y].equals(terrain[x][y]) || !mapgenEntry.furniture[x][y].equals(furniture[x][y])) {
					return false;
				}
			}
		}

		return true;

	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(terrain);
		result = 31 * result + Arrays.hashCode(furniture);
		result = 31 * result + placeGroupZones.hashCode();
		result = 31 * result + settings.hashCode();
		return result;
	}

	private List<String> createRandomGrass() {

		boolean[][] grassArray = new boolean[MapEditor.SIZE][MapEditor.SIZE];

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				grassArray[x][y] = terrain[x][y].equals("t_grass") && furniture[x][y].equals("f_null");
			}
		}

		List<Rectangle> grassRectangles = new ArrayList<>();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {

				if (!grassArray[x][y]) {
					continue;
				}

				int y2 = getGrassY2(x, y, grassArray);
				int x2 = getGrassX2(x, y, y2, grassArray);

				Rectangle r = new Rectangle();
				r.x1 = x;
				r.y1 = y;
				r.x2 = x2;
				r.y2 = y2;

				for (int ix = r.x1; ix <= r.x2; ix++) {
					for (int iy = r.y1; iy <= r.y2; iy++) {
						grassArray[ix][iy] = false;
					}
				}

				grassRectangles.add(r);

			}
		}

		List<String> grassEntries = new ArrayList<>();

		for (Rectangle r : grassRectangles) {

			String grassEntry = "{ \"point\": \"terrain\", \"id\": \"t_dirt\", ";

			if (r.x1 == r.x2) {
				grassEntry += "\"x\": " + r.x1 + ", ";
			} else {
				grassEntry += "\"x\": [ " + r.x1 + ", " + r.x2 + " ], ";
			}

			if (r.y1 == r.y2) {
				grassEntry += "\"y\": " + r.y1 + ", ";
			} else {
				grassEntry += "\"y\": [ " + r.y1 + ", " + r.y2 + " ], ";
			}

			int area = r.getArea();
			int repeatMin = Math.max(Math.min((int) (area / 3.5), 8), 0);
			int repeatMax = Math.min(Math.max((int) (area / 2.5), 1), 14);

			grassEntry += "\"repeat\": [ " + repeatMin + ", " + repeatMax + " ] },";
			grassEntries.add(grassEntry);

		}

		return grassEntries;

	}

	private int getGrassY2(final int x, final int y, final boolean[][] grassArray) {
		int y2 = y;
		for (int iy = y; iy < MapEditor.SIZE; iy++) {
			if (grassArray[x][iy]) {
				y2 = iy;
			} else {
				break;
			}
		}
		return y2;
	}

	private int getGrassX2(final int x, final int y, final int y2, final boolean[][] grassArray) {
		int x2 = x;
		for (int ix = x; ix < MapEditor.SIZE; ix++) {
			boolean nonGrassFound = false;
			for (int iy = y; iy <= y2; iy++) {
				if (!grassArray[ix][iy]) {
					nonGrassFound = true;
				}
			}
			if (!nonGrassFound) {
				x2 = ix;
			} else {
				break;
			}
		}
		return x2;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		lines.add("{");
		settings.getJsonLines().forEach(line -> lines.add(INDENT + line + ","));
		lines.add(INDENT + "\"object\": {");
		lines.add(INDENT + INDENT + "\"rows\": [");

		Map<MapTile, Character> mappings = mapSymbols();

		for (int y = 0; y < MapEditor.SIZE; y++) {
			String row = "";
			for (int x = 0; x < MapEditor.SIZE; x++) {
				row += mappings.get(new MapTile(terrain[x][y], furniture[x][y]));
			}
			lines.add(INDENT + INDENT + INDENT + "\"" + row + "\"" + ((y == MapEditor.SIZE - 1) ? "" : ","));
		}

		lines.add(INDENT + INDENT + "],");
		lines.add(INDENT + INDENT + "\"terrain\": {");

		List<String> tempLines = new ArrayList<>();

		mappings.entrySet().forEach(entry -> {
			if (entry.getKey().terrain != null) {
				tempLines.add(INDENT + INDENT + INDENT + "\"" + entry.getValue() + "\": \"" + entry.getKey().terrain + "\",");
			}
		});

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "},");
		lines.add(INDENT + INDENT + "\"furniture\": {");

		tempLines.clear();

		mappings.entrySet().forEach(entry -> {
			if (entry.getKey().furniture != null) {
				tempLines.add(INDENT + INDENT + INDENT + "\"" + entry.getValue() + "\": \"" + entry.getKey().furniture + "\",");
			}
		});

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "},");
		lines.add(INDENT + INDENT + "\"place_specials\": [");

		tempLines.clear();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (furniture[x][y].equals("f_toilet")) {
					tempLines.add(INDENT + INDENT + INDENT + "{ \"type\": \"toilet\", \"x\": " + x + ", \"y\": " + y + " },");
				}
			}
		}

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "],");
		lines.add(INDENT + INDENT + "\"set\": [");

		tempLines.clear();

		createRandomGrass().forEach(line -> tempLines.add(INDENT + INDENT + INDENT + line));

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "],");
		lines.add(INDENT + INDENT + "\"place_groups\": [");

		tempLines.clear();

		placeGroupZones.forEach(zone -> tempLines.add(INDENT + INDENT + INDENT + zone.getJsonLines().get(0) + ","));

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "]");
		lines.add(INDENT + "}");
		lines.add("}");

		return lines;

	}

	//TODO Clean this all up
	private Map<MapTile, Character> mapSymbols() {

		Multimap<MapTile, Character> commonMappings = ArrayListMultimap.create();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(Resources.getResource("tileSymbolMap.txt").toURI()))) {

			String line;

			while ((line = reader.readLine()) != null) {

				String[] mapping = line.split("(?<=[ \\S]) ");
				MapTile tile = new MapTile();

				if (mapping.length == 3) {
					tile.terrain = mapping[1];
					tile.furniture = mapping[2];
				} else {
					if (mapping[1].startsWith("t_")) {
						tile.terrain = mapping[1];
					} else {
						tile.furniture = mapping[1];
					}
				}

				commonMappings.put(tile, mapping[0].charAt(0));

			}

			reader.close();

		} catch (IOException | URISyntaxException e) {
			log.error("Error while reading tileSymbolMap.txt:", e);
		}

		List<Character> usedSymbols = new ArrayList<>();
		List<MapTile> resolveLater = new ArrayList<>();
		Map<MapTile, Character> mappings = new HashMap<>();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				MapTile tile = new MapTile(terrain[x][y], furniture[x][y]);
				if (!mappings.containsKey(tile)) {
					if (commonMappings.containsKey(tile)) {
						boolean found = false;
						for (Character symbol : commonMappings.get(tile)) {
							if (!usedSymbols.contains(symbol)) {
								usedSymbols.add(symbol);
								mappings.put(tile, symbol);
								found = true;
								break;
							}
						}
						if (!found) {
							resolveLater.add(tile);
						}
					} else {
						resolveLater.add(tile);
					}
				}
			}
		}

		resolveLater.forEach(tile -> {
			for (char symbol : SYMBOLS) {
				if (!usedSymbols.contains(symbol)) {
					mappings.put(tile, symbol);
					break;
				}
			}
		});

		return mappings;

	}

}
