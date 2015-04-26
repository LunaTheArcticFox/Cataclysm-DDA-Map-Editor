package net.krazyweb.cataclysm.mapeditor.map.data;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.*;
import net.krazyweb.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapgenEntry implements Jsonable {

	private static final char[] SYMBOLS = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!',
			'@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', '{', ']', '}', ':', ';', '"', '\'',
			'<', ',', '>', '.', '?', '/', '`', '~', '\\', '|', ' '
	};

	private static Logger log = LogManager.getLogger(MapgenEntry.class);

	public MapTile[][] tiles = new MapTile[MapEditor.SIZE][MapEditor.SIZE];
	public String fillTerrain;
	private MapTile fillTerrainPlaceholder = new MapTile(new TerrainMapping("t_CATACLYSM_MAP_EDITOR_PLACEHOLDER"));
	public List<PlaceGroupZone> placeGroupZones = new ArrayList<>();
	public MapSettings settings = new MapSettings();

	private MapgenEntry lastSavedState;

	public MapgenEntry() {

	}

	public MapgenEntry(final MapgenEntry mapgenEntry) {
		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (mapgenEntry.tiles[x][y] != null) {
					tiles[x][y] = new MapTile(mapgenEntry.tiles[x][y]);
				}
			}
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
		Map<MapTile, Character> mappings = mapSymbols();

		lines.add("{");

		settings.getJsonLines().forEach(line -> lines.add(INDENT + line + ","));

		lines.add(INDENT + "\"object\": {");

		if (fillTerrain != null) {
			lines.add(INDENT + INDENT + "\"fill_ter\": \"" + fillTerrain + "\",");
		}

		lines.addAll(getRows(mappings));

		addMappingSection(lines, mappings, this::getTerrainLines);
		addMappingSection(lines, mappings, this::getFurnitureLines);
		addMappingSection(lines, mappings, this::getGasPumpLines);
		addMappingSection(lines, mappings, this::getVendingMachineLines);
		addMappingSection(lines, mappings, this::getFieldLines);
		addMappingSection(lines, mappings, this::getSignLines);
		addMappingSection(lines, mappings, this::getMonsterGroupLines);
		addMappingSection(lines, mappings, this::getToiletLines);
		addMappingSection(lines, mappings, this::getNPCLines);
		addMappingSection(lines, mappings, this::getItemGroupLines);
		addMappingSection(lines, mappings, this::getVehicleLines);
		addMappingSection(lines, mappings, this::getItemLines);
		addMappingSection(lines, mappings, this::getTrapLines);
		addMappingSection(lines, mappings, this::getMonsterLines);

		lines.add(INDENT + "}");
		lines.add("}");

		return lines;

	}

	private void addMappingSection(final List<String> lines, Map<MapTile, Character> mappings, final MappingLines mappingLines) {
		List<String> tempLines = mappingLines.getLines(mappings);
		if (!tempLines.isEmpty()) {
			lines.set(lines.size() - 1, lines.get(lines.size() - 1) + ",");
			lines.addAll(tempLines);
		}
	}

	private List<String> getRows(final Map<MapTile, Character> mappings) {

		List<String> lines = new ArrayList<>();

		lines.add(INDENT + INDENT + "\"rows\": [");

		for (int y = 0; y < MapEditor.SIZE; y++) {
			String row = "";
			for (int x = 0; x < MapEditor.SIZE; x++) {
				if (tiles[x][y] == null) {
					row += mappings.get(fillTerrainPlaceholder);
				} else {
					row += mappings.get(tiles[x][y]);
				}
			}
			lines.add(INDENT + INDENT + INDENT + "\"" + row + "\"" + ((y == MapEditor.SIZE - 1) ? "" : ","));
		}

		lines.add(INDENT + INDENT + "]");

		return lines;

	}

	@FunctionalInterface
	private interface MappingLines {
		List<String> getLines(final Map<MapTile, Character> mappings);
	}

	private List<String> getTerrainLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "terrain", mapping -> mapping instanceof TerrainMapping && !((TerrainMapping) mapping).terrain.equals(fillTerrainPlaceholder.getDisplayTerrain()));
	}

	private List<String> getFurnitureLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "furniture", mapping -> mapping instanceof FurnitureMapping);
	}

	private List<String> getGasPumpLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "gaspumps", mapping -> mapping instanceof GasPumpMapping);
	}

	private List<String> getVendingMachineLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "vendingmachines", mapping -> mapping instanceof VendingMachineMapping);
	}

	private List<String> getFieldLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "fields", mapping -> mapping instanceof FieldMapping);
	}

	private List<String> getSignLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "signs", mapping -> mapping instanceof SignMapping);
	}

	private List<String> getMonsterGroupLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "monsters", mapping -> mapping instanceof MonsterGroupMapping);
	}

	private List<String> getToiletLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "toilets", mapping -> mapping instanceof ToiletMapping);
	}

	private List<String> getNPCLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "npcs", mapping -> mapping instanceof NPCMapping);
	}

	private List<String> getItemGroupLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "items", mapping -> mapping instanceof ItemGroupMapping);
	}

	private List<String> getVehicleLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "vehicles", mapping -> mapping instanceof VehicleMapping);
	}

	private List<String> getItemLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "item", mapping -> mapping instanceof ItemMapping);
	}

	private List<String> getTrapLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "traps", mapping -> mapping instanceof TrapMapping);
	}

	private List<String> getMonsterLines(final Map<MapTile, Character> mappings) {
		return getMappingLines(mappings, "monster", mapping -> mapping instanceof MonsterMapping);
	}

	private List<String> getMappingLines(final Map<MapTile, Character> mappings, final String tagName, final Predicate<? super TileMapping> filter) {

		List<String> lines = new ArrayList<>();
		List<String> tempLines = new ArrayList<>();

		mappings.entrySet().forEach(entry -> {

			List<String> tileMappings = entry.getKey().tileMappings
					.stream()
					.filter(filter)
					.map(TileMapping::getJson)
					.collect(Collectors.toList());

			if (!tileMappings.isEmpty()) {

				String line = INDENT + INDENT + INDENT + "\"" + entry.getValue() + "\": ";

				if (tileMappings.size() > 1) {
					line += "[ ";
				}

				line += StringUtils.join(", ", tileMappings);

				if (tileMappings.size() > 1) {
					line += " ]";
				}

				tempLines.add(line + ",");

			}

		});

		if (tempLines.size() > 0) {

			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));

			lines.add(INDENT + INDENT + "\"" + tagName + "\": {");
			lines.addAll(tempLines);
			lines.add(INDENT + INDENT + "}");

		}

		return lines;

	}

	private class CharacterMapping {

		private Character character;
		private int priority;

		private CharacterMapping(final Character character, final int priority) {
			this.character = character;
			this.priority = priority;
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CharacterMapping that = (CharacterMapping) o;

			return !(character != null ? !character.equals(that.character) : that.character != null);

		}

		@Override
		public int hashCode() {
			return character != null ? character.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "[" + priority + ", '" + character + "']";
		}

	}

	//TODO Clean this all up and optimize where possible if needed
	private Map<MapTile, Character> mapSymbols() {

		TreeMap<MapTile, List<CharacterMapping>> commonMappings = new TreeMap<>((o1, o2) -> {
			if (o1.hashCode() > o2.hashCode()) {
				return -1;
			} else if (o1.hashCode() < o2.hashCode()) {
				return 1;
			}
			return 0;
		});

		Map<MapTile, Character> mappings = new HashMap<>();
		Map<MapTile, Integer> mapTileUsageCount = new HashMap<>();
		Set<MapTile> uniqueTiles = new HashSet<>();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (tiles[x][y] != null) {
					uniqueTiles.add(tiles[x][y]);
					if (!mapTileUsageCount.containsKey(tiles[x][y])) {
						mapTileUsageCount.put(tiles[x][y], 1);
					} else {
						mapTileUsageCount.put(tiles[x][y], mapTileUsageCount.get(tiles[x][y]) + 1);
					}
				}
			}
		}

		log.debug(uniqueTiles.size());

		if (fillTerrain != null) {
			//Fill Terrain should monopolize the " " mapping for ease of map reading.
			List<CharacterMapping> fillTerrainMapping = new ArrayList<>();
			fillTerrainMapping.add(new CharacterMapping(' ', Integer.MAX_VALUE));
			commonMappings.put(fillTerrainPlaceholder, fillTerrainMapping);
		}

		for (MapTile mapTile : uniqueTiles) {

			commonMappings.put(mapTile, new ArrayList<>());
			log.debug(commonMappings.size());
			List<String> tileTerrain = new ArrayList<>();
			List<String> tileFurniture = new ArrayList<>();
			String tileExtra = "";

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

			List<String> closestMatch = new ArrayList<>();

			Map<String, Integer> terrainCounts = new HashMap<>();
			for (String terrain : tileTerrain) {
				if (terrainCounts.containsKey(terrain)) {
					terrainCounts.put(terrain, terrainCounts.get(terrain) + 1);
				} else {
					terrainCounts.put(terrain, 1);
				}
			}

			Map<String, Integer> furnitureCounts = new HashMap<>();
			for (String furniture : tileFurniture) {
				if (furnitureCounts.containsKey(furniture)) {
					furnitureCounts.put(furniture, furnitureCounts.get(furniture) + 1);
				} else {
					furnitureCounts.put(furniture, 1);
				}
			}

			try {

				BufferedReader reader = new BufferedReader(new FileReader(Paths.get("data/tileMappings.txt").toFile()));
				String line;

				List<String> mappingTerrain = new ArrayList<>();
				List<String> mappingFurniture = new ArrayList<>();
				String mappingExtra = "";
				boolean inDefinition = true;
				boolean inMatch = false;

				int score = 0;

				while ((line = reader.readLine()) != null) {

					if (line.startsWith("t:")) {
						Collections.addAll(mappingTerrain, line.substring(2).trim().split(","));
						inDefinition = true;
						inMatch = false;
					}

					if (line.startsWith("f:")) {
						Collections.addAll(mappingFurniture, line.substring(2).trim().split(","));
						inDefinition = true;
						inMatch = false;
					}

					if (line.startsWith("s:")) {
						mappingExtra = line.substring(2).trim();
						inDefinition = true;
						inMatch = false;
					}

					if (line.startsWith("	")) {

						Character character = line.charAt(1);
						int rank = Integer.parseInt(line.substring(3));

						int scale = 0;

						if (inDefinition) {

							inDefinition = false;
							boolean reject = false;

							Collection<String> terrainDisjunction = CollectionUtils.disjunction(tileTerrain, mappingTerrain);
							Collection<String> furnitureDisjunction = CollectionUtils.disjunction(tileFurniture, mappingFurniture);
							score = terrainDisjunction.size() + furnitureDisjunction.size();

							for (String terrain : new HashSet<>(mappingTerrain)) {
								if (terrainCounts.containsKey(terrain)) {
									scale -= 1;
									scale += terrainCounts.get(terrain);
								}
							}

							for (String furniture : new HashSet<>(mappingFurniture)) {
								if (furnitureCounts.containsKey(furniture)) {
									scale -= 1;
									scale += furnitureCounts.get(furniture);
								}
							}

							Set<String> mTerrain = new HashSet<>(mappingTerrain);
							Set<String> tTerrain = new HashSet<>(tileTerrain);
							mTerrain.removeAll(tTerrain);

							Set<String> mFurniture = new HashSet<>(mappingFurniture);
							Set<String> tFurniture = new HashSet<>(tileFurniture);
							mFurniture.removeAll(tFurniture);

							if (mTerrain.size() == mappingTerrain.size() && mFurniture.size() == mappingFurniture.size() && !(!mappingExtra.isEmpty() && mappingExtra.equals(tileExtra))) {
								reject = true;
							}

							score += mTerrain.size() + mFurniture.size();

							if (!(mappingExtra.isEmpty() && tileExtra.isEmpty()) && !mappingExtra.equals(tileExtra)) {
								score += 10;
							}

							log.trace(mTerrain + ", " + mFurniture);
							log.trace(score + "\t" + mappingTerrain + " " + mappingFurniture + " " + mappingExtra);
							log.trace("===");

							if (!reject) {
								inMatch = true;
								closestMatch.clear();
								//commonMappings.get(mapTile).clear();
								closestMatch.addAll(mappingTerrain);
								closestMatch.addAll(mappingFurniture);
								closestMatch.add(mappingExtra);
							}

						}

						if (inMatch) {
							//Subtract score here so that the closest matched tiles win
							//(ex: [t_grass] vs. [t_grass, t_grass, t_dirt])
							//[t_grass] should win since it's the most specific
							scale = Math.max(scale, 1);
							commonMappings.get(mapTile).add(new CharacterMapping(character, rank * scale - score));
						}

						mappingTerrain = new ArrayList<>();
						mappingFurniture = new ArrayList<>();
						mappingExtra = "";

					}

				}

				log.debug("===========");
				log.debug(mapTile);
				log.debug(closestMatch);
				log.debug(commonMappings.get(mapTile));
				log.debug("===========");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		List<CharacterMapping> usedCharacters = new ArrayList<>();

		while (!commonMappings.isEmpty()) {

			commonMappings.values().forEach(characterMapping -> {
				characterMapping.removeAll(usedCharacters);
				Collections.sort(characterMapping, (o1, o2) -> {
					if (o1.priority > o2.priority) {
						return -1;
					} else if (o1.priority < o2.priority) {
						return 1;
					}
					return 0;
				});
			});

			Stream<Map.Entry<MapTile, List<CharacterMapping>>> sorted = commonMappings.entrySet().stream()
					.sorted((entry1, entry2) -> {

						List<CharacterMapping> charMap1 = entry1.getValue();
						List<CharacterMapping> charMap2 = entry2.getValue();

						if (!charMap1.isEmpty() && charMap2.isEmpty()) {
							return -1;
						} else if (charMap1.isEmpty() && !charMap2.isEmpty()) {
							return 1;
						} else if (charMap1.isEmpty()) {
							return 0;
						}

						if (charMap1.get(0).priority > charMap2.get(0).priority) {
							return -1;
						} else if (charMap1.get(0).priority < charMap2.get(0).priority) {
							return 1;
						}

						if (mapTileUsageCount.get(entry1.getKey()) > mapTileUsageCount.get(entry2.getKey())) {
							return -1;
						} else if (mapTileUsageCount.get(entry1.getKey()) < mapTileUsageCount.get(entry2.getKey())) {
							return 1;
						}

						return 0;

					});

			sorted.limit(1).forEach(mapTileListEntry -> {

				log.debug(mapTileListEntry);

				if (mapTileListEntry.getValue().isEmpty()) {

					//TODO Match closer to characters present in terrain/furniture/extras instead of iterating all symbols
					symbolLoop:
					for (char symbol : SYMBOLS) {
						for (CharacterMapping characterMapping : usedCharacters) {
							if (characterMapping.character == symbol) {
								continue symbolLoop;
							}
						}
						mappings.put(mapTileListEntry.getKey(), symbol);
						usedCharacters.add(new CharacterMapping(symbol, 0));
						break;
					}

				} else {
					mappings.put(mapTileListEntry.getKey(), mapTileListEntry.getValue().get(0).character);
					usedCharacters.add(mapTileListEntry.getValue().get(0));
				}

				commonMappings.remove(mapTileListEntry.getKey());

			});

		}

		return mappings;

	}

}
