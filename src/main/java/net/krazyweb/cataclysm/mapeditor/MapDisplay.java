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
import net.krazyweb.cataclysm.mapeditor.events.TileRedrawRequestEvent;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.tools.PencilTool;
import net.krazyweb.cataclysm.mapeditor.tools.Tool;

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

	private int lastHoverX, lastHoverY, lastDrawX = -1, lastDrawY = -1;
	private boolean dragging = false;
	private CataclysmMap map;
	private EventBus eventBus;
	private Tool tool = new PencilTool(); //TODO Set to last tool used on startup
	private Tile currentTile;

	//TODO Condense these handlers
	private final EventHandler<MouseEvent> clickEvent = event -> {

		int eventX = ((int) (event.getX() - 1) / 32); //TODO Use tileset size
		int eventY = ((int) (event.getY() - 1) / 32); //TODO Use tileset size
		drawBox(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays

		if (lastDrawX != eventX || lastDrawY != eventY) {
			tool.click(eventX, eventY, currentTile, map);
			lastDrawX = eventX;
			lastDrawY = eventY;
		}

	};

	private final EventHandler<MouseEvent> dragEvent = event -> {

		int eventX = ((int) (event.getX() - 1) / 32); //TODO Use tileset size
		int eventY = ((int) (event.getY() - 1) / 32); //TODO Use tileset size
		drawBox(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays

		if (lastDrawX != eventX || lastDrawY != eventY) {
			tool.drag(eventX, eventY, currentTile, map);
			lastDrawX = eventX;
			lastDrawY = eventY;
		}

	};

	private final EventHandler<MouseEvent> dragStartEvent = event -> {

		int eventX = ((int) (event.getX() - 1) / 32); //TODO Use tileset size
		int eventY = ((int) (event.getY() - 1) / 32); //TODO Use tileset size
		drawBox(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays

		tool.dragStart(eventX, eventY, currentTile, map);
		lastDrawX = eventX;
		lastDrawY = eventY;

	};

	private final EventHandler<MouseEvent> dragFinishEvent = event -> {

		int eventX = ((int) (event.getX() - 1) / 32); //TODO Use tileset size
		int eventY = ((int) (event.getY() - 1) / 32); //TODO Use tileset size
		drawBox(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays

		tool.dragEnd(eventX, eventY, currentTile, map);
		lastDrawX = eventX;
		lastDrawY = eventY;

	};


	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@FXML
	private void initialize() {

		root.setOnMouseMoved(event -> drawBox(event.getX(), event.getY()));

		root.setOnMouseReleased(event -> {
			if (dragging) {
				dragging = false;
				dragFinishEvent.handle(event);
			}
		});

		root.setOnMousePressed(clickEvent);
		root.setOnMouseDragged(event -> {
			if (!dragging) {
				dragging = true;
				dragStartEvent.handle(event);
			}
			dragEvent.handle(event);
		});

	}

	@Subscribe
	public void tilePickedEventListener(final TilePickedEvent event) {
		currentTile = event.getTile();
	}

	@Subscribe
	public void redrawRequestEventListener(final TileRedrawRequestEvent event) {
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
	}

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
	}*/

	private void clearOverlay() {
		overlays.getGraphicsContext2D().clearRect(lastHoverX  * 32 - 5, lastHoverY * 32 - 5, 42, 42); //TODO Use tileset size
	}

	private void drawBox(final int mouseX, final int mouseY) {

		int eventX = (mouseX / 32); //TODO Use tileset size
		int eventY = (mouseY / 32); //TODO Use tileset size

		if (eventX != lastHoverX || eventY != lastHoverY) {

			clearOverlay();

			eventBus.post(new TileHoverEvent(map.getTerrainAt(eventX, eventY) + " | " + map.getFurnitureAt(eventX, eventY), eventX, eventY));
			lastHoverX = eventX;
			lastHoverY = eventY;

			overlays.getGraphicsContext2D().setStroke(Color.WHITE);
			overlays.getGraphicsContext2D().strokeRect(lastHoverX * 32, lastHoverY * 32, 32, 32); //TODO Use tileset size

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
		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				drawTile(x, y);
			}
		}
	}

	private void drawTile(final int x, final int y) {

		terrain.getGraphicsContext2D().setFill(Color.BLACK);
		terrain.getGraphicsContext2D().fillRect(x * 32, y * 32, 32, 32); //TODO Use tileset size

		drawTile(x, y, terrain.getGraphicsContext2D());

	}

	private void drawTile(final int x, final int y, final GraphicsContext graphicsContext) {

		if (x < 0 || y < 0 || x >= CataclysmMap.SIZE || y >= CataclysmMap.SIZE) {
			return;
		}

		//Fallback for tiles not supported by tileset
		if (Tile.tiles.get(map.getTerrainAt(x, y)) == null) {
			graphicsContext.setFill(Color.FUCHSIA);
			graphicsContext.fillRect(x * 32, y * 32, 32, 32); //TODO Use tileset size
			return;
		}

		//TODO Don't duplicate these sections
		if (Tile.tiles.get(map.getTerrainAt(x, y)).isMultiTile()) {
			int bitwiseMapping = map.getBitwiseMapping(x, y, CataclysmMap.Layer.TERRAIN);
			Image texture = TileSet.textures.get(Tile.tiles.get(map.getTerrainAt(x, y)).getTile(BITWISE_TYPES[bitwiseMapping]).getID());
			int rotation = BITWISE_ROTATIONS[bitwiseMapping];
			drawRotatedImage(graphicsContext, texture, rotation, x * 32, y * 32); //TODO Use tileset size
		} else {
			Image texture = TileSet.textures.get(Tile.tiles.get(map.getTerrainAt(x, y)).getTile().getID());
			graphicsContext.drawImage(texture, x * 32, y * 32); //TODO Use tileset size
		}

		//TODO Don't duplicate these sections
		if (map.getFurnitureAt(x, y) != null) {
			if (Tile.tiles.get(map.getFurnitureAt(x, y)).isMultiTile()) {
				int bitwiseMapping = map.getBitwiseMapping(x, y, CataclysmMap.Layer.FURNITURE);
				Image texture = TileSet.textures.get(Tile.tiles.get(map.getFurnitureAt(x, y)).getTile(BITWISE_TYPES[bitwiseMapping]).getID());
				int rotation = BITWISE_ROTATIONS[bitwiseMapping];
				drawRotatedImage(graphicsContext, texture, rotation, x * 32, y * 32); //TODO Use tileset size
			} else {
				Image texture = TileSet.textures.get(Tile.tiles.get(map.getFurnitureAt(x, y)).getTile().getID());
				graphicsContext.drawImage(texture, x * 32, y * 32); //TODO Use tileset size
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
