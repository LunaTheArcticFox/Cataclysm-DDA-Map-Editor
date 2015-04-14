package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tile {

	private static final Logger log = LogManager.getLogger(Tile.class);

	private static List<Tile> tiles = new ArrayList<>();

	public String id;
	public boolean connectsToWalls = false;

	private Tile(final String id, final boolean connectsToWalls) {
		this.id = id;
		this.connectsToWalls = connectsToWalls;
	}

	public static Tile get(final String tileId) {
		for (Tile tile : tiles) {
			if (tile.id.equals(tileId)) {
				return tile;
			}
		}
		return null;
	}

	public static List<Tile> getAll() {
		return tiles;
	}

	public static void loadTiles() {

		Path gameFolder = ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.GAME_FOLDER);

		try {
			load(gameFolder.resolve(Paths.get("data", "json", "terrain.json")));
			load(gameFolder.resolve(Paths.get("data", "json", "furniture.json")));
			load(gameFolder.resolve(Paths.get("data", "json", "terrain", "ags_terrain.json")));
		} catch (IOException e) {
			log.error("Error while loading terrain and furniture definitions:", e);
		}

	}

	private static void load(final Path path) throws IOException {

		log.info("Loading tiles from: '" + path + "'");

		JsonNode root = new ObjectMapper().readTree(path.toFile());

		root.forEach(node -> {

			boolean connectsToWalls = false;

			if (node.has("flags")) {
				for (JsonNode flag : node.get("flags")) {

					String parsedFlag = flag.asText().replaceAll("\"", "");

					if (parsedFlag.equals("CONNECT_TO_WALL") || parsedFlag.equals("WALL")) {
						log.trace("Connects to Walls: " + node.get("id").asText());
						connectsToWalls = true;
						break;
					}

				}
			}

			tiles.add(new Tile(node.get("id").asText(), connectsToWalls));
			log.trace("Loaded tile: '" + node.get("id").asText() + "'");

		});

	}

}
