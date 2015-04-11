package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TileSymbolGenerator {

	public static void main(String[] args) throws IOException {
		new TileSymbolGenerator().generate();
	}

	@FunctionalInterface
	private interface MapTileMapper {
		TileMapping parse(final JsonNode root);
	}

	private class MappedMapTile {

		private Character character;
		private MapTile mapTile;
		private int count = 0;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MappedMapTile that = (MappedMapTile) o;

			return character.equals(that.character) && mapTile.equals(that.mapTile);
		}

		@Override
		public int hashCode() {
			int result = character.hashCode();
			result = 31 * result + mapTile.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return character + ", " + mapTile;
		}

	}

	public void generate() throws IOException {

		ApplicationSettings appSettings = ApplicationSettings.getInstance();

		final Map<MappedMapTile, Integer> idCounts = new HashMap<>();

		Files.list(appSettings.getPath(ApplicationSettings.Preference.GAME_FOLDER).resolve("data").resolve("json").resolve("mapgen")).forEach(mapgenFile -> {

			if (mapgenFile.getFileName().toString().equals("mapgen-test.json")) {
				return;
			}

			Map<Character, MapTile> allTiles = new HashMap<>();

			try {

				List<JsonNode> mapgensToParse =  new ArrayList<>();

				new ObjectMapper().readTree(mapgenFile.toFile()).forEach(rootNode -> {

					if (!rootNode.has("object") && !rootNode.has("mapgen")) {
						return;
					}

					if (rootNode.has("object")) {
						mapgensToParse.add(rootNode.get("object"));
					} else {
						if (rootNode.get("mapgen").isArray()) {
							for (JsonNode node : rootNode.get("mapgen")) {
								mapgensToParse.add(node.get("object"));
							}
						} else {
							mapgensToParse.add(rootNode.get("mapgen").get("object"));
						}
					}

				});

				mapgensToParse.forEach(mapgenNode -> {

					Map<Character, MapTile> tiles = new HashMap<>();
					Iterator<Map.Entry<String, JsonNode>> nodeIterator = mapgenNode.fields();

					while (nodeIterator.hasNext()) {

						Map.Entry<String, JsonNode> node = nodeIterator.next();

						switch (node.getKey()) {

							case "terrain":
								mapTiles(tiles, node.getValue(), this::parseTerrain);
								break;

							case "furniture":
								mapTiles(tiles, node.getValue(), this::parseFurniture);
								break;

							case "gaspumps":
								mapTiles(tiles, node.getValue(), this::parseGasPumps);
								break;

							case "vendingmachines":
								mapTiles(tiles, node.getValue(), this::parseVendingMachines);
								break;

							case "fields":
								mapTiles(tiles, node.getValue(), this::parseFields);
								break;

							case "signs":
								mapTiles(tiles, node.getValue(), this::parseSigns);
								break;

							case "monsters":
								mapTiles(tiles, node.getValue(), this::parseMonsters);
								break;

							case "toilets":
								mapTiles(tiles, node.getValue(), this::parseToilets);
								break;

							case "npcs":
								mapTiles(tiles, node.getValue(), this::parseNPCs);
								break;

							case "items":
								mapTiles(tiles, node.getValue(), this::parseItems);
								break;

							case "vehicles":
								mapTiles(tiles, node.getValue(), this::parseVehicles);
								break;

							case "item":
								mapTiles(tiles, node.getValue(), this::parseItem);
								break;

							case "traps":
								mapTiles(tiles, node.getValue(), this::parseTraps);
								break;

							case "monster":
								mapTiles(tiles, node.getValue(), this::parseMonster);
								break;

							//TODO Support
							//case "mapping":
							//	mapAllPurposeMapping();
							//	break;

							default:
								break;

						}

					}

					tiles.entrySet().stream().filter(tile -> !allTiles.containsKey(tile.getKey())).forEach(tile -> allTiles.put(tile.getKey(), tile.getValue()));

				});

			} catch (IOException e) {
				e.printStackTrace();
			}

			allTiles.entrySet().forEach(characterMapTileEntry -> {

				MappedMapTile mappedMapTile = new MappedMapTile();
				mappedMapTile.character = characterMapTileEntry.getKey();
				mappedMapTile.mapTile = characterMapTileEntry.getValue();

				if (!idCounts.containsKey(mappedMapTile)) {
					idCounts.put(mappedMapTile, 1);
				} else {
					idCounts.put(mappedMapTile, idCounts.get(mappedMapTile) + 1);
				}
			});

		});

		List<MappedMapTile> countedTiles = new ArrayList<>();

		idCounts.entrySet().forEach(entry -> {
			entry.getKey().count = entry.getValue();
			countedTiles.add(entry.getKey());
		});

		Collections.sort(countedTiles, (o1, o2) -> {

			List<TileMapping> tempMappings1 = new ArrayList<>();
			List<TileMapping> tempMappings2 = new ArrayList<>();

			tempMappings1.addAll(
					o1.mapTile.tileMappings.stream()
					.filter(mapping -> mapping instanceof TerrainMapping || mapping instanceof FurnitureMapping || mapping instanceof ToiletMapping)
					.collect(Collectors.toList())
			);

			tempMappings2.addAll(
					o2.mapTile.tileMappings.stream()
					.filter(mapping -> mapping instanceof TerrainMapping || mapping instanceof FurnitureMapping || mapping instanceof ToiletMapping)
					.collect(Collectors.toList())
			);

			if (tempMappings1.hashCode() == tempMappings2.hashCode()) {
				if (o1.count > o2.count) {
					return -1;
				} else if (o1.count < o2.count) {
					return 1;
				}
				return 0;
			}

			if (tempMappings1.hashCode() > tempMappings2.hashCode()) {
				return -1;
			} else if (tempMappings1.hashCode() < tempMappings2.hashCode()) {
				return 1;
			}

			if (o1.mapTile.hashCode() > o2.mapTile.hashCode()) {
				return -1;
			} else if (o1.mapTile.hashCode() < o2.mapTile.hashCode()) {
				return 1;
			}

			return 0;
		});

		List<String> tempTerrain = new ArrayList<>();
		List<String> tempFurniture = new ArrayList<>();
		Value<String> tempSpecial = new Value<>();
		tempSpecial.value = "";

		FileWriter writer = new FileWriter(Paths.get("data/tileMappings.txt").toFile());

		countedTiles.forEach(t -> {

			List<String> terrain = new ArrayList<>();
			List<String> furniture = new ArrayList<>();
			String special = "";

			for (TileMapping mapping : t.mapTile.tileMappings) {
				if (mapping instanceof TerrainMapping) {
					if (!((TerrainMapping) mapping).terrain.equals("t_null")) {
						terrain.add(((TerrainMapping) mapping).terrain);
					}
				}
				if (mapping instanceof FurnitureMapping) {
					if (!((FurnitureMapping) mapping).furniture.equals("f_null")) {
						furniture.add(((FurnitureMapping) mapping).furniture);
					}
				}
				if (mapping instanceof SignMapping) {
					special = "sign";
				}
				if (mapping instanceof GasPumpMapping) {
					special = "gaspump";
				}
				if (mapping instanceof VendingMachineMapping) {
					special = "vendingmachine";
				}
				if (mapping instanceof ToiletMapping) {
					special = "toilet";
				}
			}

			try {

				if (tempTerrain.hashCode() != terrain.hashCode() || tempFurniture.hashCode() != furniture.hashCode() || !special.equals(tempSpecial.value)) {
					tempTerrain.clear();
					tempFurniture.clear();
					tempTerrain.addAll(terrain);
					tempFurniture.addAll(furniture);
					tempSpecial.value = special;
					if (!terrain.isEmpty()) {
						String s = "t:";
						for (String terr : terrain) {
							s += terr + ",";
						}
						s = s.substring(0, s.lastIndexOf(","));
						writer.append(s).append("\n");
					}
					if (!furniture.isEmpty()) {
						String s = "f:";
						for (String furn : furniture) {
							s += furn + ",";
						}
						s = s.substring(0, s.lastIndexOf(","));
						writer.append(s).append("\n");
					}
					if (!special.isEmpty()) {
						writer.append("s:").append(special);
						writer.append("\n");
					}
				}

				writer.append("\t").append(t.character).append(" ").append(String.valueOf(t.count)).append("\n");

			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		writer.close();

	}

	private class Value<T> {
		T value;
	}

	//TODO Hook into DataFileReader to not duplicate all this
	private void mapTiles(final Map<Character, MapTile> tiles, final JsonNode root, final MapTileMapper mapper) {

		Iterator<Map.Entry<String, JsonNode>> nodeIterator = root.fields();

		while (nodeIterator.hasNext()) {

			Map.Entry<String, JsonNode> mapping = nodeIterator.next();

			MapTile tile = getTileForCharacter(tiles, mapping.getKey().charAt(0));

			List<JsonNode> nodes = new ArrayList<>();

			if (mapping.getValue().isArray()) {
				mapping.getValue().forEach(nodes::add);
			} else {
				nodes.add(mapping.getValue());
			}

			nodes.forEach(node -> {
				TileMapping tileMapping = mapper.parse(node);
				if (tileMapping != null) {
					tile.add(tileMapping);
				}
			});

		}

	}

	private TileMapping parseTerrain(final JsonNode node) {
		if (node.isObject()) {
			if (!node.get("ter").asText().equals("t_null")) {
				return new TerrainMapping(node.get("ter").asText());
			}
		} else {
			if (!node.asText().equals("t_null")) {
				return new TerrainMapping(node.asText());
			}
		}
		return null;
	}

	private TileMapping parseFurniture(final JsonNode node) {
		if (node.isObject()) {
			if (!node.get("furn").asText().equals("f_null")) {
				return new FurnitureMapping(node.get("furn").asText());
			}
		} else {
			if (!node.asText().equals("f_null")) {
				return new FurnitureMapping(node.asText());
			}
		}
		return null;
	}

	private TileMapping parseGasPumps(final JsonNode node) {
		if (node.has("amount")) {
			JsonNode amount = node.get("amount");
			return new GasPumpMapping(amount.get(0).asInt(), amount.get(1).asInt());
		} else {
			return new GasPumpMapping();
		}
	}

	private TileMapping parseVendingMachines(final JsonNode node) {
		return new VendingMachineMapping(node.get("item_group").asText());
	}

	private TileMapping parseFields(final JsonNode node) {
		return new FieldMapping(node.get("field").asText(), node.get("age").asInt(), node.get("density").asInt());
	}

	private TileMapping parseSigns(final JsonNode node) {
		return new SignMapping(node.get("signage").asText());
	}

	private TileMapping parseMonsters(final JsonNode node) {
		return new MonstersMapping(node.get("monster").asText(), node.get("density").asDouble(), node.get("chance").asInt());
	}

	private TileMapping parseToilets(final JsonNode node) {
		if (node.has("amount")) {
			JsonNode amount = node.get("amount");
			return new ToiletMapping(amount.get(0).asInt(), amount.get(1).asInt());
		} else {
			return new ToiletMapping();
		}
	}

	private TileMapping parseNPCs(final JsonNode node) {
		return new NPCMapping(node.get("class").asText());
	}

	private TileMapping parseItems(final JsonNode node) {
		return new ItemsMapping(node.get("item").asText(), node.get("chance").asInt());
	}

	private TileMapping parseVehicles(final JsonNode node) {
		int fuel = node.has("fuel") ? node.get("fuel").asInt() : 0;
		return new VehicleMapping(node.get("vehicle").asText(), node.get("chance").asInt(), node.get("status").asInt(), fuel);
	}

	private TileMapping parseItem(final JsonNode node) {
		return new ItemMapping(node.get("item").asText(), node.get("chance").asInt());
	}

	private TileMapping parseTraps(final JsonNode node) {
		if (node.isObject()) {
			return new TrapMapping(node.get("trap").asText());
		} else {
			return new TrapMapping(node.asText());
		}
	}

	private TileMapping parseMonster(final JsonNode node) {
		return new MonsterMapping(node.get("monster").asText(), node.get("friendly").asBoolean(), node.get("name").asText());
	}

	private static MapTile getTileForCharacter(final Map<Character, MapTile> tiles, final Character character) {

		if (tiles.containsKey(character)) {
			return tiles.get(character);
		} else {
			MapTile tile = new MapTile();
			tiles.put(character, tile);
			return tile;
		}

	}

}
