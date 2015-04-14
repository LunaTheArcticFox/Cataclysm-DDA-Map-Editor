package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

//TODO Make this a service
public class TileSet {

	private static Logger log = LogManager.getLogger(TileSet.class);

	public Map<String, BufferedImage> textures = new TreeMap<>();
	public int tileSize = 24;

	private BufferedImage texture = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_4BYTE_ABGR);

	private EventBus eventBus;

	public TileSet(final Path path, final EventBus eventBus) {
		this.eventBus = eventBus;
		try {
			load(path);
		} catch (IOException e) {
			log.error("Error while attempting to read tileset definitions:", e);
		}
	}

	private void load(final Path path) throws IOException {

		//TODO Load fallback textures if present
		JsonNode root = new ObjectMapper().readTree(path.resolve("tile_config.json").toFile());

		log.debug("TileSet Size: " + root.get("tile_info").get(0));
		tileSize = root.get("tile_info").get(0).get("width").asInt();

		Path tileImagePath = null;
		JsonNode tiles;
		if (root.has("tiles-new")) {
			tiles = root.get("tiles-new").get(0).get("tiles");
			if (root.get("tiles-new").get(0).has("file")) {
				tileImagePath = ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.GAME_FOLDER).resolve(Paths.get(root.get("tiles-new").get(0).get("file").asText()));
			}
		} else {
			tiles = root.get("tiles");
		}

		if (tileImagePath == null) {
			Optional<Path> potentialPath = Files.find(path, 1, (path1, basicFileAttributes) -> path1.getFileName().toString().endsWith(".png")).findFirst();
			if (potentialPath.isPresent()) {
				tileImagePath = potentialPath.get();
			} else {
				throw new IOException("Could not find tileset image for tileset '" + path + "'.");
			}
		}

		try {
			texture = ImageIO.read(tileImagePath.toFile());
		} catch (IOException e) {
			log.error("Error while attempting to read tileset image '" + tileImagePath.toString() + "':", e);
		}

		tiles.forEach(tileDef -> {

			TileConfiguration tileConfiguration = new TileConfiguration(tileDef.get("id").asText());

			int foreground = -1;
			int background = -1;

			if (tileDef.has("fg")) {
				foreground = tileDef.get("fg").asInt();
			}

			if (tileDef.has("bg")) {
				background = tileDef.get("bg").asInt();
			}

			if (tileDef.has("rotates")) {
				tileConfiguration.rotates = tileDef.get("rotates").asBoolean();
			}

			createTileImage(tileConfiguration.getID(), foreground, background);

			if (tileDef.has("multitile") && tileDef.get("multitile").asBoolean()) {
				tileDef.get("additional_tiles").forEach(additionalTileDef -> {

					TileConfiguration additionalTileConfiguration = new TileConfiguration(tileConfiguration.getID() + ">>" + additionalTileDef.get("id").asText());

					int foregroundID = -1;
					int backgroundID = -1;

					if (additionalTileDef.has("fg")) {
						foregroundID = additionalTileDef.get("fg").asInt();
					}

					if (additionalTileDef.has("bg")) {
						backgroundID = additionalTileDef.get("bg").asInt();
					}

					if (additionalTileDef.has("rotates")) {
						additionalTileConfiguration.rotates = additionalTileDef.get("rotates").asBoolean();
					}

					createTileImage(additionalTileConfiguration.getID(), foregroundID, backgroundID);

					tileConfiguration.addMultiTile(additionalTileConfiguration, TileConfiguration.AdditionalTileType.valueOf(additionalTileDef.get("id").asText().toUpperCase()));

				});
			}
			TileConfiguration.tiles.put(tileDef.get("id").asText(), tileConfiguration);
		});

		eventBus.post(new TilesetLoadedEvent(this));

	}

	private void createTileImage(String id, final int foreground, final int background) {

		int x = foreground % 16;
		int y = foreground / 16;

		BufferedImage foregroundImage = null;
		BufferedImage backgroundImage = null;

		if (foreground >= 0) {
			foregroundImage = texture.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);
		}

		if (background >= 0) {
			x = background % 16;
			y = background / 16;
			backgroundImage = texture.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);
			if (foregroundImage != null) {
				BufferedImage tempImage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.getGraphics().drawImage(backgroundImage, 0, 0, null);
				tempImage.getGraphics().drawImage(foregroundImage, 0, 0, null);
				backgroundImage = tempImage;
			}
		}

		if (backgroundImage != null) {
			textures.put(id, backgroundImage);
		} else if (foregroundImage != null) {
			textures.put(id, foregroundImage);
		}

	}

}
