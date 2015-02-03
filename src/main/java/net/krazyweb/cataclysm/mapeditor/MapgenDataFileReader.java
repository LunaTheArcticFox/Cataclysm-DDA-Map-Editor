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

		System.out.println("terrain =");
		object.get("terrain").fields().forEachRemaining(tile -> {
			terrainMap.put(tile.getKey().charAt(0), tile.getValue().asText());
			if (!Tile.terrain.containsKey(tile.getValue().asText())) {
				Tile.addNewTile(tile.getValue().asText(), false);
			}
			System.out.println(tile.getKey() + "  " + tile.getValue().asText());
		});

		System.out.println("furniture =");
		object.get("furniture").fields().forEachRemaining(tile -> {

			furnitureMap.put(tile.getKey().charAt(0), tile.getValue().asText());

			if (!Tile.furniture.containsKey(tile.getValue().asText())) {
				Tile.addNewTile(tile.getValue().asText(), true);
			}

			System.out.println(tile.getKey() + "  " + tile.getValue().asText());

		});

		System.out.println("\r\nbackground =");

		Value<Integer> y = new Value<>();
		y.value = 0;
		object.get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				map.terrain[i][y.value] = Tile.terrain.get(terrainMap.get(rowString.charAt(i)));
				System.out.print(Tile.terrain.get(terrainMap.get(rowString.charAt(i))));
			}
			System.out.println();
			y.value++;
		});

		System.out.println("\r\nforeground =");
		object.get("rows").forEach(row -> {
			String rowString = row.asText();
			for (int i = 0; i < rowString.length(); i++) {
				if (furnitureMap.containsKey(rowString.charAt(i))) {
					System.out.print(Tile.furniture.get(furnitureMap.get(rowString.charAt(i))));
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		});

	}

}
