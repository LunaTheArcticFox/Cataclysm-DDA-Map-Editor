package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import jfxtras.labs.util.ShapeConverter;
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;
import net.krazyweb.cataclysm.mapeditor.tools.PencilTool;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
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

	private Bounds bounds;
	private boolean dragging = false;
	private CataclysmMap map;
	private EventBus eventBus;
	private Tool tool = new PencilTool(); //TODO Set to last tool used on startup
	private Tile currentTile = Tile.tiles.get("t_grass"); //TODO Set to last tile used on startup

	//TODO Condense these handlers?
	private final EventHandler<MouseEvent> clickEvent = event -> {
		tool.click(event, currentTile, groups, map);
		updateInfo(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> releaseEvent = event -> {
		tool.release(event, currentTile, groups, map);
		updateInfo(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> dragEvent = event -> {
		tool.drag(event, currentTile, groups, map);
		updateInfo(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> dragStartEvent = event -> {
		tool.dragStart(event, currentTile, groups, map);
		updateInfo(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> dragFinishEvent = event -> {
		tool.dragEnd(event, currentTile, groups, map);
		updateInfo(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
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

	@Subscribe
	public void zoomChangeEventListener(final ZoomChangeEvent event) {

		KeyValue scaleTerrainX = new KeyValue(terrain.scaleXProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleTerrainXFrame = new KeyFrame(Duration.millis(150), scaleTerrainX);

		KeyValue scaleTerrainY = new KeyValue(terrain.scaleYProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleTerrainYFrame = new KeyFrame(Duration.millis(150), scaleTerrainY);

		KeyValue scaleOverlaysX = new KeyValue(overlays.scaleXProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleOverlaysXFrame = new KeyFrame(Duration.millis(150), scaleOverlaysX);

		KeyValue scaleOverlaysY = new KeyValue(overlays.scaleYProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleOverlaysYFrame = new KeyFrame(Duration.millis(150), scaleOverlaysY);

		KeyValue scaleGroupsX = new KeyValue(groups.scaleXProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleGroupsXFrame = new KeyFrame(Duration.millis(150), scaleGroupsX);

		KeyValue scaleGroupsY = new KeyValue(groups.scaleYProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleGroupsYFrame = new KeyFrame(Duration.millis(150), scaleGroupsY);

		Timeline scaleAnimation = new Timeline();
		scaleAnimation.getKeyFrames().add(scaleTerrainXFrame);
		scaleAnimation.getKeyFrames().add(scaleTerrainYFrame);
		scaleAnimation.getKeyFrames().add(scaleOverlaysXFrame);
		scaleAnimation.getKeyFrames().add(scaleOverlaysYFrame);
		scaleAnimation.getKeyFrames().add(scaleGroupsXFrame);
		scaleAnimation.getKeyFrames().add(scaleGroupsYFrame);

		scaleAnimation.play();

	}

	private void clearOverlay() {
		if (bounds != null) {
			overlays.getGraphicsContext2D().clearRect(bounds.getMinX() - 1, bounds.getMinY() - 1, bounds.getWidth() + 2, bounds.getHeight() + 2);
		}
	}

	private void updateStatus(final int x, final int y) {
		//TODO Delegate String responsibility to StatusBarController
		if (x >= 0 && y >= 0 && x < CataclysmMap.SIZE && y < CataclysmMap.SIZE) {
			eventBus.post(new TileHoverEvent(map.getTerrainAt(x, y) + " | " + map.getFurnitureAt(x, y), x, y));
		}
	}

	//TODO Attempt optimization
	private void updateInfo(final List<Point> highlight) {

		clearOverlay();

		Shape path = new Path();

		for (Point point : highlight) {
			Rectangle r = new Rectangle(point.x * 32, point.y * 32, 32, 32); //TODO Use tileset size
			r.setFill(Color.WHITE);
			path = Shape.union(path, r);
		}

		bounds = path.getBoundsInLocal();

		GraphicsContext context = overlays.getGraphicsContext2D();

		context.setFill(new Color(1, 1, 1, 0.25));
		context.setStroke(Color.WHITE);

		context.beginPath();
		context.appendSVGPath(ShapeConverter.shapeToSvgString(path));
		context.closePath();
		context.stroke();
		context.fill();

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

		overlays.setOnMouseMoved(mouseEvent -> {
			updateInfo(tool.getHighlight((int) mouseEvent.getX() / 32, (int) mouseEvent.getY() / 32, currentTile, map));
			updateStatus((int) mouseEvent.getX() / 32, (int) mouseEvent.getY() / 32);
		});

		overlays.setOnMouseExited(mouseEvent -> clearOverlay());

		overlays.setOnMouseReleased(mouseEvent -> {
			if (dragging) {
				dragging = false;
				dragFinishEvent.handle(mouseEvent);
			} else {
				releaseEvent.handle(mouseEvent);
			}
		});

		overlays.setOnMousePressed(clickEvent);
		overlays.setOnMouseDragged(mouseEvent -> {
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
