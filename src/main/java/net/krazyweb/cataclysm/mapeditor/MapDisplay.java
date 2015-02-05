package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import net.krazyweb.cataclysm.mapeditor.events.MapLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilePickedEvent;

import java.util.HashMap;
import java.util.Map;

public class MapDisplay {

	private static enum Orientation {
		EITHER, VERTICAL, HORIZONTAL
	}

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

	private static final Orientation[] BITWISE_FORCE_ORIENTATION = {
			Orientation.EITHER,
			Orientation.VERTICAL,
			Orientation.HORIZONTAL,
			Orientation.EITHER,
			Orientation.VERTICAL,
			Orientation.VERTICAL,
			Orientation.EITHER,
			Orientation.VERTICAL,
			Orientation.HORIZONTAL,
			Orientation.EITHER,
			Orientation.HORIZONTAL,
			Orientation.HORIZONTAL,
			Orientation.EITHER,
			Orientation.VERTICAL,
			Orientation.HORIZONTAL,
			Orientation.EITHER
	};

	private static final Map<String, String> tileGroups = new HashMap<>();

	@FXML
	private StackPane root;

	@FXML
	private Canvas terrain, overlays;

	private int lastX, lastY, lastHoverX, lastHoverY;

	private MapgenMap map;

	private EventBus eventBus;

	private Tile currentTile;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilePickedEventListener(final TilePickedEvent event) {
		currentTile = event.getTile();
	}

	@FXML
	private void initialize() {
		root.setOnMouseMoved(event -> {
			drawBox(event.getX(), event.getY());
		});
		EventHandler<MouseEvent> mouseListener = event -> {
			drawBox(event.getX(), event.getY());
			int eventX = ((int) (event.getX() - 1) / 32);
			int eventY = ((int) (event.getY() - 1) / 32);
			//Current tool draw
			//TODO Do this much better, including updating orientation for surrounding tiles when re-drawn
			if (currentTile.getID().startsWith("t")) {
				map.terrain[eventX][eventY] = currentTile.getID();
				if (currentTile.getID().endsWith("_v") || currentTile.getID().endsWith("_h")) {
					System.out.println(currentTile.getID());
					int bitwiseMapping = getBitwiseMapping(eventX, eventY, map.terrain);
					map.terrain[eventX][eventY] = currentTile.getID().substring(0, currentTile.getID().lastIndexOf("_"));
					map.terrain[eventX][eventY] += BITWISE_FORCE_ORIENTATION[bitwiseMapping] == Orientation.HORIZONTAL ? "_h" : "_v";
				}
			} else {
				map.furniture[eventX][eventY] = currentTile.getID();
			}
			drawTile(eventX, eventY);
			drawTile(eventX + 1, eventY);
			drawTile(eventX - 1, eventY);
			drawTile(eventX, eventY + 1);
			drawTile(eventX, eventY - 1);
		};
		root.setOnMousePressed(mouseListener);
		root.setOnMouseDragged(mouseListener);
		tileGroups.put("t_wall_h", "wallGroup");
		tileGroups.put("t_wall_v", "wallGroup");
		tileGroups.put("t_window_frame", "wallGroup");
		tileGroups.put("t_window_boarded", "wallGroup");
		tileGroups.put("t_window_empty", "wallGroup");
		tileGroups.put("t_window_domestic", "wallGroup");
		tileGroups.put("t_door_c", "wallGroup");
		tileGroups.put("t_door_locked", "wallGroup");
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

		int eventX = ((mouseX - 1) / 32);
		int eventY = ((mouseY - 1) / 32);

		if (eventX != lastHoverX || eventY != lastHoverY) {
			eventBus.post(new TileHoverEvent(map.terrain[eventX][eventY] + " | " + map.furniture[eventX][eventY], eventX, eventY));
			lastHoverX = eventX;
			lastHoverY = eventY;
		}

	}

	private void drawBox(final double mouseX, final double mouseY) {
		drawBox((int) mouseX, (int) mouseY);
	}

	@Subscribe
	public void mapLoadedEventListener(final MapLoadedEvent event) {

		try {

			map = event.getMap();

			for (int x = 0; x < 24; x++) {
				for (int y = 0; y < 24; y++) {
					drawTile(x, y);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void drawTile(final int x, final int y) {

		terrain.getGraphicsContext2D().setFill(Color.BLACK);
		terrain.getGraphicsContext2D().fillRect(x * 32, y * 32, 32, 32);

		drawTile(x, y, terrain.getGraphicsContext2D(), map.terrain);
		drawTile(x, y, terrain.getGraphicsContext2D(), map.furniture);

	}

	private void drawTile(final int x, final int y, final GraphicsContext graphicsContext, final String[][] data) {

		if (x < 0 || y < 0 || x >= 24 || y >= 24 || data[x][y] == null || data[x][y].isEmpty()) {
			return;
		}

		//Fallback for tiles not supported by tileset
		if (Tile.tiles.get(data[x][y]) == null) {
			graphicsContext.setFill(Color.FUCHSIA);
			graphicsContext.fillRect(x * 32, y * 32, 32, 32);
			return;
		}

		if (Tile.tiles.get(data[x][y]).isMultiTile()) {
			int bitwiseMapping = getBitwiseMapping(x, y, data);
			Image texture = TileSet.textures.get(Tile.tiles.get(data[x][y]).getTile(BITWISE_TYPES[bitwiseMapping]).getID());
			int rotation = BITWISE_ROTATIONS[bitwiseMapping];
			drawRotatedImage(graphicsContext, texture, rotation, x * 32, y * 32);
		} else {
			Image texture = TileSet.textures.get(Tile.tiles.get(data[x][y]).getTile().getID());
			graphicsContext.drawImage(texture, x * 32, y * 32);
		}

	}

	private void rotate(final GraphicsContext graphicsContext, final double angle, final double x, final double y) {
		Rotate rotation = new Rotate(angle, x, y);
		graphicsContext.setTransform(rotation.getMxx(), rotation.getMyx(), rotation.getMxy(), rotation.getMyy(), rotation.getTx(), rotation.getTy());
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

	private int getBitwiseMapping(final int x, final int y, final String[][] data) {

		String current = tileAt(x, y, data);

		byte tilemap = 0;

		if (current.isEmpty()) {
			return 0;
		}

		if (tileAt(x, y + 1, data) != null && tilesConnect(tileAt(x, y + 1, data), current)) {
			tilemap += 1;
		}

		if (tileAt(x + 1, y, data) != null && tilesConnect(tileAt(x + 1, y, data), current)) {
			tilemap += 2;
		}

		if (tileAt(x, y - 1, data) != null && tilesConnect(tileAt(x, y - 1, data), current)) {
			tilemap += 4;
		}

		if (tileAt(x - 1, y, data) != null && tilesConnect(tileAt(x - 1, y, data), current)) {
			tilemap += 8;
		}

		return tilemap;

	}

	private boolean tilesConnect(final String tile1, final String tile2) {

		if (tile1.equals(tile2)) {
			return true;
		}

		if (tileGroups.containsKey(tile1) && tileGroups.containsKey(tile2)) {
			return tileGroups.get(tile1).equals(tileGroups.get(tile2));
		}

		return (tile1.endsWith("_v") || tile1.endsWith("_h")) && tile1.startsWith(tile2.substring(0, tile2.lastIndexOf("_")));

	}

	private String tileAt(final int x, final int y, final String[][] data) {
		if (x < 0 || y < 0 || x > 24 - 1 || y > 24 - 1) {
			return "";
		}
		return data[x][y];
	}

}
