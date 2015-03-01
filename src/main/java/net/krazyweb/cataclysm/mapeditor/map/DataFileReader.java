package net.krazyweb.cataclysm.mapeditor.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.krazyweb.cataclysm.mapeditor.map.data.*;
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
	private List<OverMapEntry> overMapEntries = new ArrayList<>();
	private EventBus eventBus;

	public DataFileReader(final Path path, final EventBus eventBus) {
		this.path = path;
		this.eventBus = eventBus;
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

	public List<OverMapEntry> getOverMapEntries() {
		return overMapEntries;
	}

	private class Value<T> {
		T value;
	}

	private Map<Character, String> mapToSymbols(final Iterator<Map.Entry<String, JsonNode>> tiles) {
		Map<Character, String> symbolMap = new HashMap<>();
		tiles.forEachRemaining(tile -> symbolMap.put(tile.getKey().charAt(0), tile.getValue().asText()));
		return symbolMap;
	}

	private List<PlaceGroupZone> loadPlaceGroupZones(final JsonNode placeGroupZones) {

		List<PlaceGroupZone> zones = new ArrayList<>();

		placeGroupZones.forEach(placeGroupDef -> {
			Map.Entry<String, JsonNode> id = placeGroupDef.fields().next();
			String type = id.getKey();
			String group = id.getValue().asText();
			PlaceGroup placeGroup = new PlaceGroup();
			placeGroup.type = type;
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

				}
			});
			zones.add(zone);
		});

		return zones;

	}

	private void loadMapgenSection(final JsonNode root) {
		loadMapgenSection(root, root.has("om_terrain") ? root.get("om_terrain").get(0).asText() : "No OM Terrain");
	}

	private void loadMapgenSection(final JsonNode root, final String omTerrain) {

		log.info("Loading mapgen section.");
		long startTime = System.nanoTime();

		JsonNode object = root.get("object");

		MapgenEntry map = new MapgenEntry();

		map.settings.overMapTerrain = omTerrain;

		Map<Character, String> terrainMap = mapToSymbols(object.get("terrain").fields());
		Map<Character, String> furnitureMap = mapToSymbols(object.get("furniture").fields());

		String fillTer = object.has("fill_ter") ? object.get("fill_ter").asText() : "t_grass";

		Value<Integer> y = new Value<>();
		y.value = 0;
		object.get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				map.terrain[i][y.value] = terrainMap.get(rowString.charAt(i));
				map.furniture[i][y.value] = furnitureMap.get(rowString.charAt(i));
				if (map.terrain[i][y.value] == null) {
					map.terrain[i][y.value] = fillTer;
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

	private void loadOverMapSection(final JsonNode root) {

		log.info("Loading OverMap section.");
		long startTime = System.nanoTime();

		OverMapEntry entry = new OverMapEntry();

		entry.id = root.get("id").asText();
		entry.name = root.get("name").asText();
		entry.rotate = root.get("rotate").asBoolean();
		entry.symbol = root.get("sym").asInt();
		entry.symbolColor = root.get("color").asText();
		entry.seeCost = root.get("see_cost").asInt();
		entry.extras = root.get("extras").asText();
		entry.monsterDensity = root.get("mondensity").asInt();
		entry.sidewalk = root.get("sidewalk").asBoolean();
		entry.markSaved();

		overMapEntries.add(entry);

		if (root.has("mapgen")) {
			root.get("mapgen").forEach(mapgenSection -> loadMapgenSection(mapgenSection, entry.id));
		}

		log.info("Loaded OverMap section in " + FORMATTER.format((System.nanoTime() - startTime) / 1000000.0) + " milliseconds.");

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

					case "overmap_terrain":
						loadOverMapSection(root);
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
