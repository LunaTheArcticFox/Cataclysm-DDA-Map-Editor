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
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;
import net.krazyweb.cataclysm.mapeditor.tools.PencilTool;
import net.krazyweb.cataclysm.mapeditor.tools.Tool;

import java.util.List;

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
	private Canvas terrain, overlays, groups;

	private int lastHoverX, lastHoverY;
	private boolean dragging = false;
	private CataclysmMap map;
	private EventBus eventBus;
	private Tool tool = new PencilTool(); //TODO Set to last tool used on startup
	private Tile currentTile = Tile.tiles.get("t_grass");

	//TODO Condense these handlers
	private final EventHandler<MouseEvent> clickEvent = event -> {
		updateInfo(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays
		tool.click(event, currentTile, groups, map);
	};

	private final EventHandler<MouseEvent> releaseEvent = event -> {
		updateInfo(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays
		tool.release(event, currentTile, groups, map);
	};

	private final EventHandler<MouseEvent> dragEvent = event -> {
		updateInfo(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays
		tool.drag(event, currentTile, groups, map);
	};

	private final EventHandler<MouseEvent> dragStartEvent = event -> {
		updateInfo(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays
		tool.dragStart(event, currentTile, groups, map);
	};

	private final EventHandler<MouseEvent> dragFinishEvent = event -> {
		updateInfo(event.getX(), event.getY()); //TODO Let the tool define where to draw the overlays
		tool.dragEnd(event, currentTile, groups, map);
	};

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilePickedEventListener(final TilePickedEvent event) {
		currentTile = event.getTile();
	}

	@Subscribe
	public void mapRedrawRequestEventListener(final MapRedrawRequestEvent event) {
		drawMap();
	}

	@Subscribe
	public void tileRedrawRequestEventListener(final TileRedrawRequestEvent event) {
		drawTile(event.getX(),     event.getY());
		drawTile(event.getX() + 1, event.getY());
		drawTile(event.getX() - 1, event.getY());
		drawTile(event.getX(),     event.getY() + 1);
		drawTile(event.getX(),     event.getY() - 1);
	}

	@Subscribe
	public void placeGroupRedrawRequestEventListener(final PlaceGroupRedrawRequestEvent event) {
		drawPlaceGroups();
	}

	@Subscribe
	public void toolSelectedEventListener(final ToolSelectedEvent event) {
		tool = event.getTool();
	}

	private void clearOverlay() {
		overlays.getGraphicsContext2D().clearRect(lastHoverX  * 32 - 5, lastHoverY * 32 - 5, 42, 42); //TODO Use tileset size
	}

	private void updateInfo(final int mouseX, final int mouseY) {

		int eventX = (mouseX / 32); //TODO Use tileset size
		int eventY = (mouseY / 32); //TODO Use tileset size

		if (eventX < 0 || eventY < 0 || eventX >= CataclysmMap.SIZE || eventY >= CataclysmMap.SIZE) {
			clearOverlay();
			lastHoverX = eventX;
			lastHoverY = eventY;
			return;
		}

		if (eventX != lastHoverX || eventY != lastHoverY) {

			clearOverlay();

			StringBuilder info = new StringBuilder()
					.append(map.getTerrainAt(eventX, eventY)).append(" | ")
					.append(map.getFurnitureAt(eventX, eventY)).append(" | ");

			List<PlaceGroupZone> zones = map.getPlaceGroupZonesAt(eventX, eventY);
			zones.forEach(zone -> info.append(" (").append(zone.group.type).append(" ").append(zone.group.group).append(")"));

			eventBus.post(new TileHoverEvent(info.toString(), eventX, eventY)); //TODO Pass tiles to event-not formatting; have the consumers format the text instead
			lastHoverX = eventX;
			lastHoverY = eventY;

			overlays.getGraphicsContext2D().setStroke(Color.WHITE);
			overlays.getGraphicsContext2D().strokeRect(lastHoverX * 32, lastHoverY * 32, 32, 32); //TODO Use tileset size

		}

	}

	private void updateInfo(final double mouseX, final double mouseY) {
		updateInfo((int) mouseX, (int) mouseY);
	}

	@Subscribe
	public void mapLoadedEventListener(final MapLoadedEvent event) {

		//TODO Move this?
		if (map != null) {
			eventBus.unregister(map);
		}

		try {
			map = event.getMap();
			drawMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		root.setOnMouseMoved(mouseEvent -> updateInfo(mouseEvent.getX(), mouseEvent.getY()));

		root.setOnMouseExited(mouseEvent -> clearOverlay());

		root.setOnMouseReleased(mouseEvent -> {
			if (dragging) {
				dragging = false;
				dragFinishEvent.handle(mouseEvent);
			} else {
				releaseEvent.handle(mouseEvent);
			}
		});

		root.setOnMousePressed(clickEvent);
		root.setOnMouseDragged(mouseEvent -> {
			if (!dragging) {
				dragging = true;
				dragStartEvent.handle(mouseEvent);
			}
			dragEvent.handle(mouseEvent);
		});

	}

	private void drawMap() {
		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				drawTile(x, y);
			}
		}
		drawPlaceGroups();
	}

	private void drawPlaceGroups() {
		GraphicsContext graphicsContext = groups.getGraphicsContext2D();
		graphicsContext.clearRect(0, 0, 768, 768); //TODO Use calculated size
		List<PlaceGroupZone> placeGroupZones = map.getPlaceGroupZones();
		for (int i = placeGroupZones.size() - 1; i >= 0; i--) {
			PlaceGroupZone placeGroupZone = placeGroupZones.get(i);
			graphicsContext.setFill(placeGroupZone.fillColor);
			graphicsContext.setStroke(placeGroupZone.strokeColor);
			graphicsContext.fillRect(placeGroupZone.x * 32, placeGroupZone.y * 32, placeGroupZone.w * 32, placeGroupZone.h * 32); //TODO Use tileset size
			graphicsContext.strokeRect(placeGroupZone.x * 32, placeGroupZone.y * 32, placeGroupZone.w * 32, placeGroupZone.h * 32); //TODO Use tileset size
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
