package net.krazyweb.cataclysm.mapeditor.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MapDataFileReader extends Service<Boolean> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private Path path;

	private CataclysmMap map;
	private EventBus eventBus;

	public MapDataFileReader(final Path path, final EventBus eventBus) {
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

	public CataclysmMap getMap() {
		return map;
	}

	private class Value<T> {
		T value;
	}

	private void load() throws IOException {

		map = new CataclysmMap(eventBus);

		OBJECT_MAPPER.readTree(path.toFile()).forEach(root -> {

			if (!(root.get("type").asText().equals("mapgen") || root.get("type").asText().equals("overmap_terrain"))) {
				return;
			}

			System.out.println("type = " + root.get("type").asText());

			JsonNode object = root.get("type").asText().equals("overmap_terrain") ? root.get("mapgen").get(0).get("object") : root.get("object");

			Map<Character, String> terrainMap = new HashMap<>();
			Map<Character, String> furnitureMap = new HashMap<>();

			object.get("terrain").fields().forEachRemaining(tile -> {
				terrainMap.put(tile.getKey().charAt(0), tile.getValue().asText());
			});

			object.get("furniture").fields().forEachRemaining(tile -> {
				furnitureMap.put(tile.getKey().charAt(0), tile.getValue().asText());
			});

			Value<String> fillTer = new Value<>();

			if (object.has("fill_ter")) {
				fillTer.value = object.get("fill_ter").asText();
			} else {
				fillTer.value = "";
			}

			Value<Integer> y = new Value<>();
			y.value = 0;
			object.get("rows").forEach(row -> {
				String rowString = row.asText();
				for (int i = 0; i < rowString.length(); i++) {
					map.currentState.terrain[i][y.value] = terrainMap.get(rowString.charAt(i));
					if (map.currentState.terrain[i][y.value] == null) {
						System.out.println("EMPTY");
						map.currentState.terrain[i][y.value] = fillTer.value;
					}
				}
				y.value++;
			});

			y.value = 0;

			object.get("rows").forEach(row -> {
				String rowString = row.asText();
				for (int i = 0; i < rowString.length(); i++) {
					map.currentState.furniture[i][y.value] = furnitureMap.get(rowString.charAt(i));
				}
				y.value++;
			});

		});

	}

}
