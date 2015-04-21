package net.krazyweb.cataclysm.mapeditor.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.krazyweb.cataclysm.mapeditor.map.data.*;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.*;
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
		Iterator<Map.Entry<String, JsonNode>> nodeIterator = root.get("object").fields();

		while (nodeIterator.hasNext()) {

			Map.Entry<String, JsonNode> node = nodeIterator.next();

			log.trace("Parsing node: " + node.getKey());

			switch (node.getKey()) {

				case "fill_ter":
					map.fillTerrain = node.getValue().asText();
					break;

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
					mapTiles(tiles, node.getValue(), this::parseItemGroups);
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

				//case "mapping":
				//	mapAllPurposeMapping();
				//	break;

				default:
					log.info("Unrecognized mapgen field: '" + node.getKey() + "'");
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

		maps.add(map);

		log.info("Loaded mapgen section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

	}

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
				TileMapping t = mapper.parse(node);
				if (t != null) {
					tile.add(t);
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
		return new MonsterGroupMapping(node.get("monster").asText(), node.get("density").asDouble(), node.get("chance").asInt());
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

	private TileMapping parseItemGroups(final JsonNode node) {
		return new ItemGroupMapping(node.get("item").asText(), node.get("chance").asInt());
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

		//TODO Determine which of these are actually optional
		entry.id = root.get("id").asText();
		entry.name = root.get("name").asText();
		entry.rotate = root.get("rotate").asBoolean();
		if (root.has("lineDrawing")) { entry.lineDrawing = root.get("lineDrawing").asBoolean(); }
		entry.symbol = root.get("sym").asInt();
		entry.symbolColor = root.get("color").asText();
		entry.seeCost = root.get("see_cost").asInt();
		if (root.has("extras")) { entry.extras = root.get("extras").asText(); }
		if (root.has("known_down")) { entry.knownDown = root.get("known_down").asBoolean(); }
		if (root.has("known_up")) { entry.knownUp = root.get("known_up").asBoolean(); }
		entry.monsterDensity = root.get("mondensity").asInt();
		entry.sidewalk = root.get("sidewalk").asBoolean();
		if (root.has("allow_road")) { entry.allowRoad = root.get("allow_road").asBoolean(); }
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
