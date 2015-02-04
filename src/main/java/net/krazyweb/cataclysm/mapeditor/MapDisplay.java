package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import net.krazyweb.cataclysm.mapeditor.events.MapLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;

import java.util.HashMap;
import java.util.Map;

public class MapDisplay {

	private static final Tile.AdditionalTileType[] BITWISE_TYPES = {
			Tile.AdditionalTileType.UNCONNECTED,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.EDGE,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.EDGE,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.CENTER
	};

	private static final int[] BITWISE_ROTATIONS = {
			0, 0, 270, 0, 180, 0, 270, 270, 90, 90, 90, 0, 180, 90, 180, 0
	};

	private static final Map<String, String> tileGroups = new HashMap<>();

	@FXML
	private StackPane root;

	@FXML
	private Canvas terrain, overlays;

	private int lastX, lastY;

	private MapgenMap map;

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@FXML
	private void initialize() {
		root.setOnMouseMoved(event -> {
			drawBox(event.getX(), event.getY());
		});
		tileGroups.put("t_wall_h", "wallGroup");
		tileGroups.put("t_wall_v", "wallGroup");
	}

	private void clearOverlay() {
		overlays.getGraphicsContext2D().clearRect((lastX / 32) * 32 - 5, (lastY / 32) * 32 - 5, 42, 42);
	}

	private void drawBox(final int mouseX, final int mouseY) {

		clearOverlay();

		lastX = (mouseX / 32) * 32;
		lastY = (mouseY / 32) * 32;

		overlays.getGraphicsContext2D().setStroke(Color.WHITE);
		overlays.getGraphicsContext2D().strokeRect(lastX, lastY, 32, 32);

		int eventX = (mouseX / 32);
		int eventY = (mouseY / 32);

		eventBus.post(new TileHoverEvent(map.terrain[eventX][eventY], eventX, eventY));

	}

	private void drawBox(final double mouseX, final double mouseY) {
		drawBox((int) mouseX, (int) mouseY);
	}

	@Subscribe
	public void mapLoadedEventListener(final MapLoadedEvent event) {

		try {

			map = event.getMap();

			GraphicsContext terrainGraphicsContext = terrain.getGraphicsContext2D();

			for (int x = 0; x < 24; x++) {
				for (int y = 0; y < 24; y++) {
					if (Tile.tiles.get(map.terrain[x][y]).isMultiTile()) {
						System.out.println("Multitile");
						int bitwiseMapping = getBitwiseMapping(x, y);
						Image background = TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getBackground(BITWISE_TYPES[bitwiseMapping]));
						Image foreground = TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getForeground(BITWISE_TYPES[bitwiseMapping]));
						int rotation = BITWISE_ROTATIONS[bitwiseMapping];
						drawRotatedImage(terrainGraphicsContext, background, rotation, x * 32, y * 32);
						drawRotatedImage(terrainGraphicsContext, foreground, rotation, x * 32, y * 32);
					} else {
						terrainGraphicsContext.drawImage(TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getBackground()), x * 32, y * 32);
						terrainGraphicsContext.drawImage(TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getForeground()), x * 32, y * 32);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void rotate(final GraphicsContext gc, final double angle, final double x, final double y) {
		Rotate r = new Rotate(angle, x, y);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	private void drawRotatedImage(final GraphicsContext graphicsContext, final Image image, final double angle, final int x, final int y) {
		if (image == null) {
			return;
		}
		graphicsContext.save(); // saves the current state on stack, including the current transform
		rotate(graphicsContext, angle, x + image.getWidth() / 2, y + image.getHeight() / 2);
		graphicsContext.drawImage(image, x, y);
		graphicsContext.restore(); // back to original state (before rotation)
	}

	private int getBitwiseMapping(final int x, final int y) {

		String current = tileAt(x, y);

		byte tilemap = 0;

		if (current.isEmpty()) {
			return 0;
		}

		if (tileAt(x, y + 1).equals(current)) {
			tilemap += 1;
		}

		if (tileAt(x + 1, y).equals(current)) {
			tilemap += 2;
		}

		if (tileAt(x, y - 1).equals(current)) {
			tilemap += 4;
		}

		if (tileAt(x - 1, y).equals(current)) {
			tilemap += 8;
		}

		return tilemap;

	}

	private String tileAt(final int x, final int y) {
		if (x < 0 || y < 0 || x > 24 - 1 || y > 24 - 1) {
			return "";
		}
		String tile = map.terrain[x][y];
		if (tileGroups.containsKey(tile)) {
			return tileGroups.get(tile);
		}
		return map.terrain[x][y];
	}

}
