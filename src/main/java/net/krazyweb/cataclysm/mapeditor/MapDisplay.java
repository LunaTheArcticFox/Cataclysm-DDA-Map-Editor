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
import net.krazyweb.cataclysm.mapeditor.events.RedrawRequestEvent;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilePickedEvent;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

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

	@FXML
	private StackPane root;

	@FXML
	private Canvas terrain, overlays;

	private int lastX, lastY, lastHoverX, lastHoverY;

	private CataclysmMap map;

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
			if (event.isSecondaryButtonDown()) {
				//rotateMapClockwise();
				return;
			}
			drawBox(event.getX(), event.getY());
			int eventX = ((int) (event.getX() - 1) / 32);
			int eventY = ((int) (event.getY() - 1) / 32);
			//Current tool draw
			//TODO Do this much better, including updating orientation for surrounding tiles when re-drawn
			map.drawTile(eventX, eventY, currentTile);
			/*if (currentTile.getID().startsWith("t")) {
				map.terrain[eventX][eventY] = currentTile.getID();
				if (currentTile.getID().endsWith("_v") || currentTile.getID().endsWith("_h")) {
					System.out.println(currentTile.getID());
					int bitwiseMapping = getBitwiseMapping(eventX, eventY, map.terrain);
					map.terrain[eventX][eventY] = currentTile.getID().substring(0, currentTile.getID().lastIndexOf("_"));
					map.terrain[eventX][eventY] += BITWISE_FORCE_ORIENTATION[bitwiseMapping] == Orientation.HORIZONTAL ? "_h" : "_v";
				}
			} else {
				map.furniture[eventX][eventY] = currentTile.getID();
			}*/
			/*drawTile(eventX, eventY);
			drawTile(eventX + 1, eventY);
			drawTile(eventX - 1, eventY);
			drawTile(eventX, eventY + 1);
			drawTile(eventX, eventY - 1);*/
		};
		root.setOnMousePressed(mouseListener);
		root.setOnMouseDragged(mouseListener);
	}

	@Subscribe
	public void redrawRequestEventListener(final RedrawRequestEvent event) {
		drawTile(event.getX(),     event.getY());
		drawTile(event.getX() + 1, event.getY());
		drawTile(event.getX() - 1, event.getY());
		drawTile(event.getX(),     event.getY() + 1);
		drawTile(event.getX(),     event.getY() - 1);
	}

	/*private void rotateMapClockwise() {
		transposeArray(map.terrain);
		reverseColumns(map.terrain);
		transposeArray(map.furniture);
		reverseColumns(map.furniture);
		drawMap();
	}*/

	private void transposeArray(final String[][] array) {
		for(int i = 0; i < 24; i++) {
			for(int j = i + 1; j < 24; j++) {
				String temp = array[i][j];
				array[i][j] = array[j][i];
				array[j][i] = temp;
			}
		}
	}

	private void reverseColumns(final String[][] array) {
		for(int j = 0; j < array.length; j++){
			for(int i = 0; i < array[j].length / 2; i++) {
				String temp = array[i][j];
				array[i][j] = array[array.length - i - 1][j];
				array[array.length - i - 1][j] = temp;
			}
		}
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
			eventBus.post(new TileHoverEvent(map.getTerrainAt(eventX, eventY) + " | " + map.getFurnitureAt(eventX, eventY), eventX, eventY));
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
			drawMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void drawMap() {
		for (int x = 0; x < 24; x++) {
			for (int y = 0; y < 24; y++) {
				drawTile(x, y);
			}
		}
	}

	private void drawTile(final int x, final int y) {

		terrain.getGraphicsContext2D().setFill(Color.BLACK);
		terrain.getGraphicsContext2D().fillRect(x * 32, y * 32, 32, 32);

		drawTile(x, y, terrain.getGraphicsContext2D());

	}

	private void drawTile(final int x, final int y, final GraphicsContext graphicsContext) {

		if (x < 0 || y < 0 || x >= 24 || y >= 24) {
			return;
		}

		//Fallback for tiles not supported by tileset
		if (Tile.tiles.get(map.getTerrainAt(x, y)) == null) {
			graphicsContext.setFill(Color.FUCHSIA);
			graphicsContext.fillRect(x * 32, y * 32, 32, 32);
			return;
		}

		//TODO Don't duplicate these sections
		if (Tile.tiles.get(map.getTerrainAt(x, y)).isMultiTile()) {
			int bitwiseMapping = map.getBitwiseMapping(x, y, CataclysmMap.Layer.TERRAIN);
			Image texture = TileSet.textures.get(Tile.tiles.get(map.getTerrainAt(x, y)).getTile(BITWISE_TYPES[bitwiseMapping]).getID());
			int rotation = BITWISE_ROTATIONS[bitwiseMapping];
			drawRotatedImage(graphicsContext, texture, rotation, x * 32, y * 32);
		} else {
			Image texture = TileSet.textures.get(Tile.tiles.get(map.getTerrainAt(x, y)).getTile().getID());
			graphicsContext.drawImage(texture, x * 32, y * 32);
		}

		//TODO Don't duplicate these sections
		if (map.getFurnitureAt(x, y) != null) {
			if (Tile.tiles.get(map.getFurnitureAt(x, y)).isMultiTile()) {
				int bitwiseMapping = map.getBitwiseMapping(x, y, CataclysmMap.Layer.FURNITURE);
				Image texture = TileSet.textures.get(Tile.tiles.get(map.getFurnitureAt(x, y)).getTile(BITWISE_TYPES[bitwiseMapping]).getID());
				int rotation = BITWISE_ROTATIONS[bitwiseMapping];
				drawRotatedImage(graphicsContext, texture, rotation, x * 32, y * 32);
			} else {
				Image texture = TileSet.textures.get(Tile.tiles.get(map.getFurnitureAt(x, y)).getTile().getID());
				graphicsContext.drawImage(texture, x * 32, y * 32);
			}
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

}
