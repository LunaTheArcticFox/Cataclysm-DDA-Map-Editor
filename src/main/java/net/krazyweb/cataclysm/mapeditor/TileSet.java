package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

//TODO Make this a service
public class TileSet {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	//TODO Un-static this
	public static Map<Integer, Image> textures = new TreeMap<>();

	private BufferedImage texture = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);

	public TileSet(final Path path) {
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

			Tile tile = new Tile();

			if (tileDef.has("fg")) {
				int tileNumber = tileDef.get("fg").asInt();
				loadImageFromID(tileNumber);
				tile.setForeground(tileNumber);
			}
			if (tileDef.has("bg")) {
				int tileNumber = tileDef.get("bg").asInt();
				loadImageFromID(tileNumber);
				tile.setBackground(tileNumber);
			}
			if (tileDef.has("multitile") && tileDef.get("multitile").asBoolean()) {
				tileDef.get("additional_tiles").forEach(additionalTileDef -> {
					System.out.println("Tile....");
					Tile additionalTile = new Tile();
					if (additionalTileDef.has("fg")) {
						int tileNumber = additionalTileDef.get("fg").asInt();
						loadImageFromID(tileNumber);
						additionalTile.setForeground(tileNumber);
					}
					if (additionalTileDef.has("bg")) {
						int tileNumber = additionalTileDef.get("bg").asInt();
						loadImageFromID(tileNumber);
						additionalTile.setBackground(tileNumber);
					}
					Tile.tiles.put(additionalTileDef.get("id").asText(), additionalTile);
				});
			}
			Tile.tiles.put(tileDef.get("id").asText(), tile);
		});

	}

	private void loadImageFromID(final int id) {
		int x = id % 16;
		int y = id / 16;
		Image image = SwingFXUtils.toFXImage(texture.getSubimage(x * 32, y * 32, 32, 32), null);
		textures.put(id, image);
	}

}
