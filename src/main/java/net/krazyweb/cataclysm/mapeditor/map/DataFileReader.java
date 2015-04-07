package net.krazyweb.cataclysm.mapeditor.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.krazyweb.cataclysm.mapeditor.map.data.*;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class DataFileReader extends Service<Boolean> {

	private static Logger log = LogManager.getLogger(DataFileReader.class);
	private static final DecimalFormat FORMATTER = new DecimalFormat("0.##");

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private Path path;

	private List<MapgenEntry> maps = new ArrayList<>();
	private List<ItemGroupEntry> itemGroupEntries = new ArrayList<>();
	private List<MonsterGroupEntry> monsterGroupEntries = new ArrayList<>();
	private List<OvermapEntry> overmapEntries = new ArrayList<>();

	public DataFileReader(final Path path) {
		this.path = path;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				load();
				return Boolean.TRUE;
			}
		};
	}

	public List<MapgenEntry> getMaps() {
		return maps;
	}

	public List<ItemGroupEntry> getItemGroupEntries() {
		return itemGroupEntries;
	}

	public List<MonsterGroupEntry> getMonsterGroupEntries() {
		return monsterGroupEntries;
	}

	public List<OvermapEntry> getOvermapEntries() {
		return overmapEntries;
	}

	private class Value<T> {
		T value;
	}

	private List<PlaceGroupZone> loadPlaceGroupZones(final JsonNode placeGroupZones) {

		List<PlaceGroupZone> zones = new ArrayList<>();

		placeGroupZones.forEach(placeGroupDef -> {
			Map.Entry<String, JsonNode> id = placeGroupDef.fields().next();
			String type = id.getKey();
			String group = id.getValue().asText();
			PlaceGroup placeGroup = new PlaceGroup();
			placeGroup.type = PlaceGroup.Type.valueOf(type);
			placeGroup.group = group;
			PlaceGroupZone zone = new PlaceGroupZone(placeGroup);
			placeGroupDef.fields().forEachRemaining(field -> {
				switch (field.getKey()) {
					case "x":

						if (field.getValue().isArray()) {

							int x1 = field.getValue().get(0).asInt();
							int x2 = field.getValue().get(1).asInt();

							if (x1 > x2) {
								int temp = x1;
								x1 = x2;
								x2 = temp;
							}

							zone.bounds.x1 = x1;
							zone.bounds.x2 = x2;

						} else {
							zone.bounds.x1 = field.getValue().asInt();
							zone.bounds.x2 = field.getValue().asInt();
						}

						break;

					case "y":
						if (field.getValue().isArray()) {

							int y1 = field.getValue().get(0).asInt();
							int y2 = field.getValue().get(1).asInt();

							if (y1 > y2) {
								int temp = y1;
								y1 = y2;
								y2 = temp;
							}

							zone.bounds.y1 = y1;
							zone.bounds.y2 = y2;

						} else {
							zone.bounds.y1 = field.getValue().asInt();
							zone.bounds.y2 = field.getValue().asInt();
						}
						break;

					case "chance":
						zone.group.chance = field.getValue().asInt();
						break;

					case "repeat":
						if (field.getValue().isArray()) {
							zone.repeatMin = field.getValue().get(0).asInt();
							zone.repeatMax = field.getValue().get(1).asInt();
						} else {
							zone.repeatMin = field.getValue().asInt();
						}
						break;

				}
			});
			zones.add(zone);
		});

		return zones;

	}

	private void loadMapgenSection(final JsonNode root) {
		loadMapgenSection(root, root.has("om_terrain") ? root.get("om_terrain").get(0).asText() : "No OM Terrain");
	}



	@FunctionalInterface
	private interface MapTileMapper {
		TileMapping parse(final JsonNode root);
	}

	protected void loadMapgenSection(final JsonNode root, final String omTerrain) {

		log.info("Loading mapgen section.");
		long startTime = System.nanoTime();

		MapgenEntry map = new MapgenEntry();
		map.settings.weight = root.get("weight").asInt();
		map.settings.overmapTerrain = omTerrain;

		Map<Character, MapTile> tiles = new HashMap<>();

		for (JsonNode node : root.get("object")) {

			log.trace("Parsing node: " + node.asText());

			switch (node.asText()) {

				case "fill_ter":
					//map.fillTerrain = node.asText();
					log.debug("Fill Terrain: " + node.asText());
					break;

				case "terrain":
					mapTiles(tiles, node, this::parseTerrain);
					break;

				case "furniture":
					mapTiles(tiles, node, this::parseFurniture);
					break;

				case "gaspumps":
					mapTiles(tiles, node, this::parseGasPumps);
					break;

				case "vendingmachines":
					mapTiles(tiles, node, this::parseVendingMachines);
					break;

				case "fields":
					mapTiles(tiles, node, this::parseFields);
					break;

				case "signs":
					mapTiles(tiles, node, this::parseSigns);
					break;

				case "monsters":
					mapTiles(tiles, node, this::parseMonsters);
					break;

				case "toilets":
					mapTiles(tiles, node, this::parseToilets);
					break;

				case "npcs":
					mapTiles(tiles, node, this::parseNPCs);
					break;

				case "items":
					mapTiles(tiles, node, this::parseItems);
					break;

				case "vehicles":
					mapTiles(tiles, node, this::parseVehicles);
					break;

				case "item":
					mapTiles(tiles, node, this::parseItem);
					break;

				case "traps":
					mapTiles(tiles, node, this::parseTraps);
					break;

				case "monster":
					mapTiles(tiles, node, this::parseMonster);
					break;

				//case "mapping":
				//	mapAllPurposeMapping();
				//	break;

				default:
					log.info("Unrecognized mapgen field: '" + node.asText() + "'");
					break;

			}

		}

		Value<Integer> y = new Value<>();
		y.value = 0;
		root.get("object").get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				map.tiles[i][y.value] = tiles.get(rowString.charAt(i));
			}
			y.value++;
		});

		log.info("Loaded mapgen section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}

	private void mapTiles(final Map<Character, MapTile> tiles, final JsonNode root, final MapTileMapper mapper) {

		root.forEach(mapping -> {

			MapTile tile = getTileForCharacter(tiles, mapping.asText().charAt(0));

			List<JsonNode> nodes = new ArrayList<>();

			if (mapping.isArray()) {
				mapping.forEach(nodes::add);
			} else {
				nodes.add(mapping);
			}

			nodes.forEach(node -> tile.add(mapper.parse(node)));

		});

	}

	//TODO Test each one of these by passing in map tile map and node with only entries in it

	private TileMapping parseTerrain(final JsonNode node) {
		if (node.isObject()) {
			return new TerrainMapping(node.get("ter").asText());
		} else {
			return new TerrainMapping(node.asText());
		}
	}

	private TileMapping parseFurniture(final JsonNode node) {
		if (node.isObject()) {
			return new FurnitureMapping(node.get("furn").asText());
		} else {
			return new FurnitureMapping(node.asText());
		}
	}

	private TileMapping parseGasPumps(final JsonNode node) {
		JsonNode amount = node.get("amount");
		return new GasPumpMapping(amount.get(0).asInt(), amount.get(1).asInt());
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
		JsonNode amount = node.get("amount");
		return new ToiletMapping(amount.get(0).asInt(), amount.get(1).asInt());
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

	private MapTile getTileForCharacter(final Map<Character, MapTile> tiles, final Character character) {

		if (tiles.containsKey(character)) {
			return tiles.get(character);
		} else {
			MapTile tile = new MapTile();
			tiles.put(character, tile);
			return tile;
		}

	}

	/*private void loadMapgenSection(final JsonNode root, final String omTerrain) {

		log.info("Loading mapgen section.");
		long startTime = System.nanoTime();

		JsonNode object = root.get("object");

		MapgenEntry map = new MapgenEntry();

		map.settings.overmapTerrain = omTerrain;

		Map<Character, TerrainMapping> terrainMap = parseTerrainMappings(object.get("terrain").fields());
		Map<Character, FurnitureMapping> furnitureMap = parseMappings(TileMapping.Type.FURNITURE, object.get("furniture").fields());
		Map<Character, GasPumpMapping> gasPumpMap = parseMappings(TileMapping.Type.GAS_PUMP, object.get("gaspumps").fields());
		Map<Character, VendingMachineMapping> vendingMachineMap = parseMappings(TileMapping.Type.VENDING_MACHINE, object.get("vendingmachines").fields());
		Map<Character, FieldMapping> fieldsMap = parseMappings(TileMapping.Type.FIELD, object.get("fields").fields());
		Map<Character, SignMapping> signsMap = parseMappings(TileMapping.Type.SIGN, object.get("signs").fields());
		Map<Character, MonstersMapping> monstersMap = parseMappings(TileMapping.Type.MONSTERS, object.get("monsters").fields());
		Map<Character, ToiletMapping> toiletMap = parseMappings(TileMapping.Type.TOILET, object.get("toilets").fields());
		Map<Character, NPCMapping> npcMap = parseMappings(TileMapping.Type.NPC, object.get("npcs").fields());
		Map<Character, ItemsMapping> itemsMap = parseMappings(TileMapping.Type.ITEMS, object.get("items").fields());
		Map<Character, VehicleMapping> vehicleMap = parseMappings(TileMapping.Type.VEHICLE, object.get("vehicles").fields());
		Map<Character, ItemMapping> itemMap = parseMappings(TileMapping.Type.ITEM, object.get("item").fields());
		Map<Character, TrapMapping> trapMap = parseMappings(TileMapping.Type.TRAP, object.get("traps").fields());
		Map<Character, MonsterMapping> monsterMap = parseMappings(TileMapping.Type.MONSTER, object.get("monster").fields());

		//Map<Character, String> furnitureMap = parseMappings(TileMapping.Type, object.get("mapping").fields());

		String fillTer = object.has("fill_ter") ? object.get("fill_ter").asText() : "t_grass";

		Value<Integer> y = new Value<>();
		y.value = 0;
		object.get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				map.tiles[i][y.value] = terrainMap.get(rowString.charAt(i));
				map.furniture[i][y.value] = furnitureMap.get(rowString.charAt(i));
				if (map.tiles[i][y.value] == null) {
					map.tiles[i][y.value] = fillTer;
				}
				if (map.furniture[i][y.value] == null) {
					map.furniture[i][y.value] = "f_null";
				}
			}
			y.value++;
		});

		if (object.has("place_groups")) {
			map.placeGroupZones.addAll(loadPlaceGroupZones(object.get("place_groups")));
		}

		map.markSaved();
		maps.add(map);

		log.info("Loaded mapgen section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}*/

	private void loadItemGroupSection(final JsonNode root) {

		log.info("Loading Item Group section.");
		long startTime = System.nanoTime();

		ItemGroupEntry entry = new ItemGroupEntry();

		entry.id = root.get("id").asText();

		root.get("items").forEach(item -> entry.items.put(item.get(0).asText(), item.get(1).asInt()));

		entry.markSaved();

		itemGroupEntries.add(entry);

		log.info("Loaded Item Group section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}

	private void loadMonsterGroupSection(final JsonNode root) {

		log.info("Loading Monster Group section.");
		long startTime = System.nanoTime();

		MonsterGroupEntry entry = new MonsterGroupEntry();

		entry.name = root.get("name").asText();
		entry.defaultGroup = root.get("default").asText();

		root.get("monsters").forEach(monsterGroupMonster -> {
			MonsterGroupMonster monster = new MonsterGroupMonster();
			monster.monster = monsterGroupMonster.get("monster").asText();
			monster.frequency = monsterGroupMonster.get("freq").asInt();
			monster.multiplier = monsterGroupMonster.get("cost_multiplier").asInt();
			entry.monsters.add(monster);
		});

		entry.markSaved();

		monsterGroupEntries.add(entry);

		log.info("Loaded Monster Group section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}

	private void loadOvermapSection(final JsonNode root) {

		log.info("Loading Overmap section.");
		long startTime = System.nanoTime();

		OvermapEntry entry = new OvermapEntry();

		entry.id = root.get("id").asText();
		entry.name = root.get("name").asText();
		entry.rotate = root.get("rotate").asBoolean();
		entry.lineDrawing = root.get("lineDrawing").asBoolean();
		entry.symbol = root.get("sym").asInt();
		entry.symbolColor = root.get("color").asText();
		entry.seeCost = root.get("see_cost").asInt();
		entry.extras = root.get("extras").asText();
		entry.knownDown = root.get("known_down").asBoolean();
		entry.knownUp = root.get("known_up").asBoolean();
		entry.monsterDensity = root.get("mondensity").asInt();
		entry.sidewalk = root.get("sidewalk").asBoolean();
		entry.allowRoad = root.get("allow_road").asBoolean();
		entry.markSaved();

		overmapEntries.add(entry);

		if (root.has("mapgen")) {
			root.get("mapgen").forEach(mapgenSection -> loadMapgenSection(mapgenSection, entry.id));
		}

		log.info("Loaded Overmap section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}

	private void load() throws IOException {

		log.info("Loading '" + path.toAbsolutePath() + "'.");
		long startTime = System.nanoTime();

		OBJECT_MAPPER.readTree(path.toFile()).forEach(root -> {

			try {

				switch (root.get("type").asText()) {

					case "mapgen":
						loadMapgenSection(root);
						break;

					case "item_group":
						loadItemGroupSection(root);
						break;

					case "monstergroup":
						loadMonsterGroupSection(root);
						break;

					case "overmap_terrain":
						loadOvermapSection(root);
						break;

					default:
						log.info("Unsupported file section encountered (" + root.get("type").asText() + "). Bugging out!");
						break;

				}

			} catch (Exception e) {
				log.error("Error while reading map file '" + path.toAbsolutePath() + "':", e);
			}

		});

		log.info("Loaded '" + path.toAbsolutePath() + "' in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}

}
