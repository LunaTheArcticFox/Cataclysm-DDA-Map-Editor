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
	public static Map<String, Image> textures = new TreeMap<>();

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

		JsonNode root = OBJECT_MAPPER.readTree(path.toFile()); //TODO Allow for multiple maps per file

		root.get("tiles-new").get(0).get("tiles").forEach(tileDef -> {
			if (tileDef.get("fg") == null) {
				return;
			}
			int tileNumber = tileDef.get("fg").asInt();
			int x = tileNumber % 16;
			int y = tileNumber / 16;
			System.out.println("Adding " + tileDef.get("id") + " " + x * 32 + ", " + y * 32);
			Image image = SwingFXUtils.toFXImage(texture.getSubimage(x * 32, y * 32, 32, 32), null);
			textures.put(tileDef.get("id").asText(), image);
		});

	}

}
