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
	public static Map<Integer, Image> textures = new TreeMap<>();

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

			if (tileDef.has("fg")) {
				int tileNumber = tileDef.get("fg").asInt();
				loadImageFromNumber(tileNumber);
				tile.setForeground(tileNumber);
			}
			if (tileDef.has("bg")) {
				int tileNumber = tileDef.get("bg").asInt();
				loadImageFromNumber(tileNumber);
				tile.setBackground(tileNumber);
			}
			if (tileDef.has("multitile") && tileDef.get("multitile").asBoolean()) {
				tileDef.get("additional_tiles").forEach(additionalTileDef -> {
					Tile additionalTile = new Tile(additionalTileDef.get("id").asText());
					if (additionalTileDef.has("fg")) {
						int tileNumber = additionalTileDef.get("fg").asInt();
						loadImageFromNumber(tileNumber);
						additionalTile.setForeground(tileNumber);
					}
					if (additionalTileDef.has("bg")) {
						int tileNumber = additionalTileDef.get("bg").asInt();
						loadImageFromNumber(tileNumber);
						additionalTile.setBackground(tileNumber);
					}
					tile.addMultiTile(additionalTile, Tile.AdditionalTileType.valueOf(additionalTileDef.get("id").asText().toUpperCase()));
				});
			}
			Tile.tiles.put(tileDef.get("id").asText(), tile);
		});

		eventBus.post(new TilesetLoadedEvent(Paths.get("")));

	}

	private void loadImageFromNumber(final int number) {
		int x = number % 16;
		int y = number / 16;
		Image image = SwingFXUtils.toFXImage(texture.getSubimage(x * 32, y * 32, 32, 32), null);
		textures.put(number, image);
	}

}
