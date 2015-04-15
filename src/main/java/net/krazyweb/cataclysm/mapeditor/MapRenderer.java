package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.data.PlaceGroupZone;
import net.krazyweb.cataclysm.mapeditor.tools.PencilTool;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
import net.krazyweb.cataclysm.mapeditor.tools.Tool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

public class MapRenderer {

	private static final Logger log = LogManager.getLogger(MapRenderer.class);

	@FXML
	private StackPane root;

	@FXML
	private Canvas terrain, overlays, grid, groups;

	private Rectangle2D bounds;
	private boolean dragging = false;
	private MapEditor map;
	private EventBus eventBus;
	private Tool tool = new PencilTool(); //TODO Set to last tool used on startup
	private MapTile currentTile; //TODO Set to last tile used on startup, create default MapTile in init()
	private TileSet tileSet;

	//TODO Condense these handlers?
	private final EventHandler<MouseEvent> clickEvent = event -> {
		tool.click(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize, currentTile, map));
		updateStatus((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize);
	};

	private final EventHandler<MouseEvent> releaseEvent = event -> {
		tool.release(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize, currentTile, map));
		updateStatus((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize);
	};

	private final EventHandler<MouseEvent> dragEvent = event -> {
		tool.drag(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize, currentTile, map));
		updateStatus((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize);
	};

	private final EventHandler<MouseEvent> dragStartEvent = event -> {
		tool.dragStart(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize, currentTile, map));
		updateStatus((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize);
	};

	private final EventHandler<MouseEvent> dragFinishEvent = event -> {
		tool.dragEnd(event, currentTile, groups, map);
		updateOverlays(tool.getHighlight((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize, currentTile, map));
		updateStatus((int) event.getX() / tileSet.tileSize, (int) event.getY() / tileSet.tileSize);
	};

	@FXML
	private void initialize() {
		tileSet = ApplicationSettings.currentTileset;
		updateCanvasSize();
		root.setEffect(new DropShadow(5, 0, 3, new Color(0, 0, 0, 0.2)));
		grid.setManaged(false);
		grid.setVisible(false);
		drawGrid();
		grid.setManaged(ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GRID));
		grid.setVisible(ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GRID));
	}

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilePickedEventListener(final TilePickedEvent event) {
		currentTile = event.getTile();
	}

	@Subscribe
	public void tileSetLoadedEventListener(final TilesetLoadedEvent event) {
		tileSet = event.getTileSet();
		updateCanvasSize();
	}

	private void updateCanvasSize() {

		int size = tileSet.tileSize * 24;

		terrain.setWidth(size);
		overlays.setWidth(size);
		grid.setWidth(size);
		groups.setWidth(size);

		terrain.setHeight(size);
		overlays.setHeight(size);
		grid.setHeight(size);
		groups.setHeight(size);

	}

	public void redraw() {
		drawMap();
		drawPlaceGroups();
	}

	public void redraw(final int x, final int y) {
		drawTile(x,     y);
		drawTile(x + 1, y);
		drawTile(x - 1, y);
		drawTile(x,     y + 1);
		drawTile(x, y - 1);
	}

	public void redrawPlaceGroups() {
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
		if (x >= 0 && y >= 0 && x < MapEditor.SIZE && y < MapEditor.SIZE) {
			eventBus.post(new TileHoverEvent(map.getTileAt(x, y), x, y));
		}
	}

	private void updateOverlays(final Set<Point> highlight) {

		clearOverlay();

		BufferedImage bufferedImage = new BufferedImage(MapEditor.SIZE * tileSet.tileSize, MapEditor.SIZE * tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = bufferedImage.createGraphics();

		GraphicsContext context = overlays.getGraphicsContext2D();

		Path2D area = new Path2D.Float();

		for (Point point : highlight) {

			boolean adjBelow = false;
			boolean adjRight = false;
			boolean adjDiag = false;

			for (Point point1 : highlight) {
				if (point1 != point) {
					if (point.x == point1.x - 1 && point.y == point1.y - 1) {
						adjDiag = true;
					} else if (point.x == point1.x - 1 && point.y == point1.y) {
						area.append(new java.awt.Rectangle(point.x * tileSet.tileSize + tileSet.tileSize, point.y * tileSet.tileSize, 2, tileSet.tileSize - 1), false);
						adjRight = true;
					} else if (point.x == point1.x && point.y == point1.y - 1) {
						area.append(new java.awt.Rectangle(point.x * tileSet.tileSize, point.y * tileSet.tileSize + tileSet.tileSize, tileSet.tileSize - 1, 2), false);
						adjBelow = true;
					}
				}
			}

			if (!adjDiag && adjBelow && adjRight) {
				area.append(new java.awt.Rectangle(point.x * tileSet.tileSize + tileSet.tileSize - 1, point.y * tileSet.tileSize, 2, tileSet.tileSize - 1), false);
				area.append(new java.awt.Rectangle(point.x * tileSet.tileSize, point.y * tileSet.tileSize + tileSet.tileSize - 1, tileSet.tileSize - 1, 2), false);
			}

			java.awt.Rectangle r = new java.awt.Rectangle(
					point.x * tileSet.tileSize,
					point.y * tileSet.tileSize,
					tileSet.tileSize + (adjRight ? 0 : -1) + (!adjDiag && adjBelow && adjRight ? -1 : 0),
					tileSet.tileSize + (adjBelow ? 0 : -1) + (!adjDiag && adjBelow && adjRight ? -1 : 0));

			area.append(r, false);

			//TODO Bitwise mapping of texture
			graphics.drawImage(SwingFXUtils.fromFXImage(tool.getHighlightTile(currentTile), null), point.x * tileSet.tileSize, point.y * tileSet.tileSize, null);

		}

		Area finalArea = new Area(area);

		graphics.setPaint(new java.awt.Color(1.0f, 1.0f, 1.0f, 0.7f));
		graphics.draw(finalArea);
		graphics.setPaint(new java.awt.Color(1.0f, 1.0f, 1.0f, 0.2f));
		graphics.fill(finalArea);

		context.clearRect(0, 0, MapEditor.SIZE * tileSet.tileSize, MapEditor.SIZE * tileSet.tileSize);
		context.drawImage(SwingFXUtils.toFXImage(bufferedImage, null), 0, 0);

		bounds = area.getBounds2D();

	}

	private void drawGrid() {

		GraphicsContext context = grid.getGraphicsContext2D();
		grid.setOpacity(0.25);

		context.setStroke(Color.WHITE);
		context.setLineWidth(1);

		for (int i = 0; i < MapEditor.SIZE; i++) {
			double loc = i * tileSet.tileSize + 0.5;
			context.moveTo(loc, 0);
			context.lineTo(loc, tileSet.tileSize * MapEditor.SIZE);
			context.moveTo(0, loc);
			context.lineTo(tileSet.tileSize * MapEditor.SIZE, loc);
			context.stroke();
		}

		double loc = MapEditor.SIZE * tileSet.tileSize - 0.5;

		context.moveTo(loc, 0);
		context.lineTo(loc, tileSet.tileSize * MapEditor.SIZE);
		context.moveTo(0, loc);
		context.lineTo(tileSet.tileSize * MapEditor.SIZE, loc);
		context.stroke();

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

	public void setMapEditor(final MapEditor map) {

		this.map = map;

		overlays.setOnMouseMoved(mouseEvent -> {
			updateOverlays(tool.getHighlight((int) mouseEvent.getX() / tileSet.tileSize, (int) mouseEvent.getY() / tileSet.tileSize, currentTile, map));
			updateStatus((int) mouseEvent.getX() / tileSet.tileSize, (int) mouseEvent.getY() / tileSet.tileSize);
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
		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				drawTile(x, y);
			}
		}
		drawPlaceGroups();
	}

	private void drawPlaceGroups() {
		GraphicsContext graphicsContext = groups.getGraphicsContext2D();
		graphicsContext.clearRect(0, 0, tileSet.tileSize * MapEditor.SIZE, tileSet.tileSize * MapEditor.SIZE);
		List<PlaceGroupZone> placeGroupZones = map.getPlaceGroupZones();
		for (int i = placeGroupZones.size() - 1; i >= 0; i--) {
			PlaceGroupZone placeGroupZone = placeGroupZones.get(i);
			graphicsContext.setFill(placeGroupZone.fillColor);
			graphicsContext.setStroke(placeGroupZone.strokeColor);
			graphicsContext.fillRect(placeGroupZone.bounds.x1 * tileSet.tileSize + 0.5, placeGroupZone.bounds.y1 * tileSet.tileSize + 0.5, placeGroupZone.bounds.getWidth() * tileSet.tileSize - 1, placeGroupZone.bounds.getHeight() * tileSet.tileSize - 1); //TODO Use tileset size
			graphicsContext.strokeRect(placeGroupZone.bounds.x1 * tileSet.tileSize + 0.5, placeGroupZone.bounds.y1 * tileSet.tileSize + 0.5, placeGroupZone.bounds.getWidth() * tileSet.tileSize - 1, placeGroupZone.bounds.getHeight() * tileSet.tileSize - 1); //TODO Use tileset size
		}
	}

	private void drawTile(final int x, final int y) {

		terrain.getGraphicsContext2D().setFill(Color.BLACK);
		terrain.getGraphicsContext2D().fillRect(x * tileSet.tileSize, y * tileSet.tileSize, tileSet.tileSize, tileSet.tileSize);

		drawTile(x, y, terrain.getGraphicsContext2D());

	}

	private void drawTile(final int x, final int y, final GraphicsContext graphicsContext) {

		if (x < 0 || y < 0 || x >= MapEditor.SIZE || y >= MapEditor.SIZE) {
			return;
		}

		if (map.getTileAt(x, y) == null) {
			int bitwiseMapping = map.getTerrainBitwiseMapping(x, y);
			Image texture;
			if (map.getFillTerrain() == null) { //TODO Properly handle missing fill terrain
				texture = SwingFXUtils.toFXImage(new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR), null);
			} else {
				texture = SwingFXUtils.toFXImage(tileSet.textures.get(TileConfiguration.tiles.get(map.getFillTerrain()).getTile(TileConfiguration.BITWISE_TYPES[bitwiseMapping]).getID()), null);
			}
			graphicsContext.drawImage(texture, x * tileSet.tileSize, y * tileSet.tileSize);
			return;
		}

		//TODO Use tileset fallback if configured
		//Fallback for tiles not supported by tileset
		if (TileConfiguration.tiles.get(map.getTileAt(x, y).getTileID()) == null) {
			graphicsContext.setFill(Color.FUCHSIA);
			graphicsContext.fillRect(x * tileSet.tileSize, y * tileSet.tileSize, tileSet.tileSize, tileSet.tileSize);
			return;
		}

		if (map.getTileAt(x, y).displayTerrain == null && map.getTileAt(x, y).displayFurniture != null && map.getFillTerrain() != null) { //TODO Properly handle missing fill terrain?
			BufferedImage terrainImage = tileSet.textures.get(TileConfiguration.get(map.getFillTerrain()).getTile(TileConfiguration.BITWISE_TYPES[0]).getID()); //TODO Rotate and bitwise map fillTerrain
			graphicsContext.drawImage(SwingFXUtils.toFXImage(terrainImage, null), x * tileSet.tileSize, y * tileSet.tileSize);
		}

		int terrainBitwiseMapping = map.getTerrainBitwiseMapping(x, y);
		int furnitureBitwiseMapping = map.getFurnitureBitwiseMapping(x, y);
		Image texture = map.getTileAt(x, y).getTexture(terrainBitwiseMapping, furnitureBitwiseMapping);
		graphicsContext.drawImage(texture, x * tileSet.tileSize, y * tileSet.tileSize);

	}

}
