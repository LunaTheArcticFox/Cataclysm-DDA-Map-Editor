package net.krazyweb.cataclysm.mapeditor.map.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

	private class SymbolMap {
		private MapTile tile;
		private Character character;
		private int priority;
	}

	//TODO Clean this all up
	private Map<MapTile, Character> mapSymbols() {

		Multimap<MapTile, Character> commonMappings = ArrayListMultimap.create();
		Map<MapTile, Character> mappings = new HashMap<>();
		Set<MapTile> uniqueTiles = new HashSet<>();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			uniqueTiles.addAll(Arrays.asList(tiles[x]).subList(0, MapEditor.SIZE));
		}

		for (MapTile mapTile : uniqueTiles) {

			List<String> tileTerrain = new ArrayList<>();
			List<String> tileFurniture = new ArrayList<>();
			String tileExtra = "";

			List<String> closestMatch = new ArrayList<>();
			int closestCount = Integer.MAX_VALUE;

			for (TileMapping mapping : mapTile.tileMappings) {
				if (mapping instanceof TerrainMapping) {
					tileTerrain.add(((TerrainMapping) mapping).terrain);
				}
				if (mapping instanceof FurnitureMapping) {
					tileFurniture.add(((FurnitureMapping) mapping).furniture);
				}
				if (mapping instanceof ToiletMapping) {
					tileExtra = "toilet";
				}
				if (mapping instanceof GasPumpMapping) {
					tileExtra = "gaspump";
				}
				if (mapping instanceof SignMapping) {
					tileExtra = "sign";
				}
				if (mapping instanceof VendingMachineMapping) {
					tileExtra = "vendingmachine";
				}
			}

			try {

				BufferedReader reader = new BufferedReader(new FileReader(Paths.get("data/tileMappings.txt").toFile()));
				String line;

				List<String> mappingTerrain = new ArrayList<>();
				List<String> mappingFurniture = new ArrayList<>();
				String mappingExtra = "";
				Character closestCharacter = ' ';

				while ((line = reader.readLine()) != null) {

					if (line.startsWith("t:")) {
						Collections.addAll(mappingTerrain, line.substring(2).trim().split(","));
					}

					if (line.startsWith("f:")) {
						Collections.addAll(mappingFurniture, line.substring(2).trim().split(","));
					}

					if (line.startsWith("s:")) {
						mappingExtra = line.substring(2).trim();
					}

					if (line.startsWith("	")) {

						if (!(mappingTerrain.isEmpty() && mappingFurniture.isEmpty() && mappingExtra.isEmpty())) {

							Character c = line.substring(1).charAt(0);

							Collection<String> terrainDisjunction = CollectionUtils.disjunction(tileTerrain, mappingTerrain);
							Collection<String> furnitureDisjunction = CollectionUtils.disjunction(tileFurniture, mappingFurniture);
							int score = terrainDisjunction.size() + furnitureDisjunction.size();

							Set<String> mTerrain = new HashSet<>(mappingTerrain);
							Set<String> tTerrain = new HashSet<>(tileTerrain);
							mTerrain.removeAll(tTerrain);

							Set<String> mFurniture = new HashSet<>(mappingFurniture);
							Set<String> tFurniture = new HashSet<>(tileFurniture);
							mFurniture.removeAll(tFurniture);

							score += mTerrain.size() + mFurniture.size();

							if (!(mappingExtra.isEmpty() && tileExtra.isEmpty()) && !mappingExtra.equals(tileExtra)) {
								score += 10;
							}

							if (mTerrain.size() == mappingTerrain.size() && mFurniture.size() == mappingFurniture.size() && !(!mappingExtra.isEmpty() && mappingExtra.equals(tileExtra))) {
								score += 10000;
							}

							log.trace(mTerrain);
							log.trace(mFurniture);
							log.trace(score + "\t" + mappingTerrain + " " + mappingFurniture + " " + mappingExtra);

							if (score <= closestCount && score < 10000) {
								closestCount = score;
								closestMatch.clear();
								closestCharacter = c;
								closestMatch.addAll(mappingTerrain);
								closestMatch.addAll(mappingFurniture);
								closestMatch.add(mappingExtra);
							}

						}

						mappingTerrain = new ArrayList<>();
						mappingFurniture = new ArrayList<>();
						mappingExtra = "";

					}

				}

				mappings.put(mapTile, closestCharacter);

				log.debug("===========");
				log.debug(mapTile);
				log.debug(closestMatch);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return mappings;

	}

}
