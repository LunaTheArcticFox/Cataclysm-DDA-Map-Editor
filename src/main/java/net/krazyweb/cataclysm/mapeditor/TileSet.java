package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

//TODO Make this a service
public class TileSet {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	//TODO Un-static this
	public static Map<String, Image> textures = new TreeMap<>();

	private BufferedImage texture = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);

	private EventBus eventBus;

	public TileSet(final Path path, final EventBus eventBus) {
		this.eventBus = eventBus;
		try {
			texture = ImageIO.read(new File("Sample Data/tileset/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			load(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void load(final Path path) throws IOException {

		JsonNode root = OBJECT_MAPPER.readTree(path.toFile());

		root.get("tiles-new").get(0).get("tiles").forEach(tileDef -> {

			Tile tile = new Tile(tileDef.get("id").asText());

			int foreground = -1;
			int background = -1;

			if (tileDef.has("fg")) {
				foreground = tileDef.get("fg").asInt();
			}

			if (tileDef.has("bg")) {
				background = tileDef.get("bg").asInt();
			}

			createTileImage(tile.getID(), foreground, background);

			/*if (tileDef.has("fg")) {
				int tileNumber = tileDef.get("fg").asInt();
				loadImageFromNumber(tileNumber);
				tile.setForeground(tileNumber);
			}
			if (tileDef.has("bg")) {
				int tileNumber = tileDef.get("bg").asInt();
				loadImageFromNumber(tileNumber);
				tile.setBackground(tileNumber);
			}*/
			if (tileDef.has("multitile") && tileDef.get("multitile").asBoolean()) {
				tileDef.get("additional_tiles").forEach(additionalTileDef -> {

					Tile additionalTile = new Tile(tile.getID() + ">>" + additionalTileDef.get("id").asText());

					int foregroundID = -1;
					int backgroundID = -1;

					if (additionalTileDef.has("fg")) {
						foregroundID = additionalTileDef.get("fg").asInt();
					}

					if (additionalTileDef.has("bg")) {
						backgroundID = additionalTileDef.get("bg").asInt();
					}

					createTileImage(additionalTile.getID(), foregroundID, backgroundID);

					tile.addMultiTile(additionalTile, Tile.AdditionalTileType.valueOf(additionalTileDef.get("id").asText().toUpperCase()));

				});
			}
			Tile.tiles.put(tileDef.get("id").asText(), tile);
		});

		eventBus.post(new TilesetLoadedEvent(Paths.get("")));

	}

	private void createTileImage(String id, final int foreground, final int background) {

		int x = foreground % 16;
		int y = foreground / 16;

		BufferedImage foregroundImage = null;
		BufferedImage backgroundImage = null;

		if (foreground >= 0) {
			foregroundImage = texture.getSubimage(x * 32, y * 32, 32, 32);
		}

		if (background >= 0) {
			x = background % 16;
			y = background / 16;
			backgroundImage = texture.getSubimage(x * 32, y * 32, 32, 32);
			if (foregroundImage != null) {
				BufferedImage tempImage = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.getGraphics().drawImage(backgroundImage, 0, 0, null);
				tempImage.getGraphics().drawImage(foregroundImage, 0, 0, null);
				backgroundImage = tempImage;
			}
		}

		if (backgroundImage != null) {
			textures.put(id, SwingFXUtils.toFXImage(backgroundImage, null));
		} else if (foregroundImage != null) {
			textures.put(id, SwingFXUtils.toFXImage(foregroundImage, null));
		}

	}

	/*private void loadImageFromNumber(final int number) {
		int x = number % 16;
		int y = number / 16;
		Image image = SwingFXUtils.toFXImage(texture.getSubimage(x * 32, y * 32, 32, 32), null);
		textures.put(number, image);
	}*/

}
