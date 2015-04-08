package net.krazyweb.cataclysm.mapeditor.map.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	public MapTile[][] tiles = new MapTile[MapEditor.SIZE][MapEditor.SIZE];
	public MapTile fillTerrain;
	public List<PlaceGroupZone> placeGroupZones = new ArrayList<>();
	public MapSettings settings = new MapSettings();

	private MapgenEntry lastSavedState;

	public MapgenEntry() {

	}

	public MapgenEntry(final MapgenEntry mapgenEntry) {
		for (int x = 0; x < MapEditor.SIZE; x++) {
			System.arraycopy(mapgenEntry.tiles[x], 0, tiles[x], 0, MapEditor.SIZE);
		}
		mapgenEntry.placeGroupZones.forEach(zone -> placeGroupZones.add(new PlaceGroupZone(zone)));
		settings = new MapSettings(mapgenEntry.settings);
		fillTerrain = mapgenEntry.fillTerrain;
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

		MapgenEntry that = (MapgenEntry) o;

		if (!Arrays.deepEquals(tiles, that.tiles)) return false;
		if (fillTerrain != null ? !fillTerrain.equals(that.fillTerrain) : that.fillTerrain != null) return false;
		if (!placeGroupZones.equals(that.placeGroupZones)) return false;
		return settings.equals(that.settings);

	}

	@Override
	public int hashCode() {
		int result = Arrays.deepHashCode(tiles);
		result = 31 * result + (fillTerrain != null ? fillTerrain.hashCode() : 0);
		result = 31 * result + placeGroupZones.hashCode();
		result = 31 * result + settings.hashCode();
		return result;
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
				row += mappings.get(tiles[x][y]);
			}
			lines.add(INDENT + INDENT + INDENT + "\"" + row + "\"" + ((y == MapEditor.SIZE - 1) ? "" : ","));
		}

		lines.add(INDENT + INDENT + "],");
		/*lines.add(INDENT + INDENT + "\"terrain\": {");

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

		lines.add(INDENT + INDENT + "]");*/
		lines.add(INDENT + "}");
		lines.add("}");

		return lines;

	}

	//TODO Clean this all up
	private Map<MapTile, Character> mapSymbols() {

		Multimap<MapTile, Character> commonMappings = ArrayListMultimap.create();

		/*InputStream tileSymbolMapStream = getClass().getResourceAsStream("/tileSymbolMap.txt");
		if (tileSymbolMapStream != null) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(tileSymbolMapStream, StandardCharsets.UTF_8))) {

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

			} catch (IOException e) {
				log.error("Error while reading tileSymbolMap.txt:", e);
			}
		} else {
			log.error("tileSymbolMap.txt not found");
		}

		List<Character> usedSymbols = new ArrayList<>();
		List<MapTile> resolveLater = new ArrayList<>();*/
		Map<MapTile, Character> mappings = new HashMap<>();
		int index = 0;

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (!mappings.containsKey(tiles[x][y])) {
					mappings.put(tiles[x][y], SYMBOLS[index++]);
				}
			}
		}
		/*for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				MapTile tile = new MapTile(tiles[x][y]);
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
		});*/

		return mappings;

	}

}
