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
import javafx.scene.effect.DropShadow;
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
import java.util.Set;

public class MapRenderer {

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
	private Canvas terrain, overlays, grid, groups;

	private Bounds bounds;
	private boolean dragging = false;
	private CataclysmMap map;
	private EventBus eventBus;
	private Tool tool = new PencilTool(); //TODO Set to last tool used on startup
	private Tile currentTile = Tile.tiles.get("t_grass"); //TODO Set to last tile used on startup

	//TODO Condense these handlers?
	private final EventHandler<MouseEvent> clickEvent = event -> {
		tool.click(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> releaseEvent = event -> {
		tool.release(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> dragEvent = event -> {
		tool.drag(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> dragStartEvent = event -> {
		tool.dragStart(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	private final EventHandler<MouseEvent> dragFinishEvent = event -> {
		tool.dragEnd(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / 32, (int) event.getY() / 32, currentTile, map)); //TODO Tile Size
		updateStatus((int) event.getX() / 32, (int) event.getY() / 32);
	};

	@FXML
	private void initialize() {
		root.setEffect(new DropShadow(5, 0, 3, new Color(0, 0, 0, 0.2)));
		grid.setManaged(false); //TODO Use setting as of last run, default to hidden
		grid.setVisible(false); //TODO Use setting as of last run, default to hidden
		drawGrid();
	}

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilePickedEventListener(final TilePickedEvent event) {
		currentTile = event.getTile();
	}

	public void redraw() {
		drawMap();
	}

	public void redraw(final int x, final int y) {
		drawTile(x,     y);
		drawTile(x + 1, y);
		drawTile(x - 1, y);
		drawTile(x,     y + 1);
		drawTile(x,     y - 1);
	}

	/*@Subscribe
	public void placeGroupRedrawRequestEventListener(final PlaceGroupRedrawRequestEvent event) {
		drawPlaceGroups();
	}*/

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

		KeyValue scaleGridX = new KeyValue(grid.scaleXProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleGridXFrame = new KeyFrame(Duration.millis(150), scaleGridX);

		KeyValue scaleGridY = new KeyValue(grid.scaleYProperty(), event.getZoomLevel(), Interpolator.EASE_BOTH);
		KeyFrame scaleGridYFrame = new KeyFrame(Duration.millis(150), scaleGridY);

		Timeline scaleAnimation = new Timeline();
		scaleAnimation.getKeyFrames().add(scaleTerrainXFrame);
		scaleAnimation.getKeyFrames().add(scaleTerrainYFrame);
		scaleAnimation.getKeyFrames().add(scaleOverlaysXFrame);
		scaleAnimation.getKeyFrames().add(scaleOverlaysYFrame);
		scaleAnimation.getKeyFrames().add(scaleGroupsXFrame);
		scaleAnimation.getKeyFrames().add(scaleGroupsYFrame);
		scaleAnimation.getKeyFrames().add(scaleGridXFrame);
		scaleAnimation.getKeyFrames().add(scaleGridYFrame);

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
	private void updateOverlays(final Set<Point> highlight) {

		clearOverlay();

		Shape path = new Path();

		GraphicsContext context = overlays.getGraphicsContext2D();

		context.setGlobalAlpha(0.75);
		for (Point point : highlight) {
			Rectangle r = new Rectangle(point.x * 32, point.y * 32, 32, 32); //TODO Use tileset size
			r.setFill(Color.WHITE);
			path = Shape.union(path, r);
			context.drawImage(tool.getHighlightTile(currentTile), point.x * 32, point.y * 32); //TODO Bitwise map tile previews
		}
		context.setGlobalAlpha(1.0);

		bounds = path.getBoundsInLocal();

		context.setFill(new Color(1, 1, 1, 0.2));
		context.setStroke(new Color(1, 1, 1, 0.75));
		context.setLineWidth(2);

		context.beginPath();
		context.appendSVGPath(ShapeConverter.shapeToSvgString(path));
		context.closePath();
		context.stroke();
		context.fill();

	}

	private void drawGrid() {

		GraphicsContext context = grid.getGraphicsContext2D();

		context.setStroke(new Color(1, 1, 1, 0.8));
		context.setLineWidth(0.5);

		for (int i = 0; i < CataclysmMap.SIZE; i++) {
			context.strokeLine(i * 32, 0, i * 32, 768);
			context.strokeLine(0, i * 32, 768, i * 32);
		}

	}

	@Subscribe
	public void showGridEventListener(final ShowGridEvent event) {
		grid.setManaged(event.showGrid());
		grid.setVisible(event.showGrid());
	}

	@Subscribe
	public void showGroupsEventListener(final ShowGroupsEvent event) {
		groups.setManaged(event.showGroups());
		groups.setVisible(event.showGroups());
	}

	public void setMap(final CataclysmMap map) {

		this.map = map;

		drawMap();

		overlays.setOnMouseMoved(mouseEvent -> {
			updateOverlays(tool.getHighlight((int) mouseEvent.getX() / 32, (int) mouseEvent.getY() / 32, currentTile, map));
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

	/*@Subscribe
	public void mapLoadedEventListener(final MapLoadedEvent event) {

		//TODO Move this?

	}*/

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

		//TODO Use tileset fallback if configured
		//Fallback for tiles not supported by tileset
		if (Tile.tiles.get(map.getTerrainAt(x, y)) == null) {
			graphicsContext.setFill(Color.FUCHSIA);
			graphicsContext.fillRect(x * 32, y * 32, 32, 32); //TODO Use tileset size
			return;
		}

		//TODO Use tileset fallback if configured
		if (Tile.tiles.get(map.getFurnitureAt(x, y)) == null) {
			graphicsContext.setFill(new Color(0.8, 0.2, 0.6, 0.5));
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
