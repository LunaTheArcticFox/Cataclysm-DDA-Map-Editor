package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MapgenDataFileReader extends Service<Boolean> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private Path path;

	private MapgenMap map = new MapgenMap();

	public MapgenDataFileReader(final Path path) {
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

	public MapgenMap getMap() {
		return map;
	}

	private class Value<T> {
		T value;
	}

	private void load() throws IOException {

		JsonNode root = OBJECT_MAPPER.readTree(path.toFile()).get(0); //TODO Allow for multiple maps per file

		System.out.println("type = " + root.get("type").asText());
		System.out.println("om_terrain = " + root.get("om_terrain").get(0).asText());
		System.out.println("method = " + root.get("method").asText());
		System.out.println("weight = " + root.get("weight").asInt());

		JsonNode object = root.get("object");

		Map<Character, String> terrainMap = new HashMap<>();
		Map<Character, String> furnitureMap = new HashMap<>();

		object.get("terrain").fields().forEachRemaining(tile -> {
			terrainMap.put(tile.getKey().charAt(0), tile.getValue().asText());
		});

		object.get("furniture").fields().forEachRemaining(tile -> {
			furnitureMap.put(tile.getKey().charAt(0), tile.getValue().asText());
		});


		Value<Integer> y = new Value<>();
		y.value = 0;
		object.get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				map.terrain[i][y.value] = terrainMap.get(rowString.charAt(i));
			}
			y.value++;
		});

		y.value = 0;

		object.get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				map.furniture[i][y.value] = furnitureMap.get(rowString.charAt(i));
			}
			y.value++;
/*			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				if (furnitureMap.containsKey(rowString.charAt(i))) {
					System.out.print(furnitureMap.get(rowString.charAt(i)));
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();*/
		});

	}

}
