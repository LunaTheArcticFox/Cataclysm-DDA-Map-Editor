package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import net.krazyweb.cataclysm.mapeditor.MapRenderer;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.undo.TileChangeAction;
import net.krazyweb.cataclysm.mapeditor.map.undo.UndoEvent;
import net.krazyweb.cataclysm.mapeditor.tools.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CataclysmMap {

	public static final int SIZE = 24;

	private static enum Orientation {
		EITHER, VERTICAL, HORIZONTAL
	}

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

	public static enum Layer {
		TERRAIN, FURNITURE
	}

	protected MapState lastSavedState = null;
	protected MapState currentState = new MapState();

	private Set<Point> changedTiles = new HashSet<>();

	private EventBus eventBus;
	private MapManager manager;
	private MapRenderer renderer;

	protected CataclysmMap(final EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	protected void setManager(final MapManager manager) {
		this.manager = manager;
	}
	protected void setRenderer(final MapRenderer renderer) {
		this.renderer = renderer;
	}

	private void rotateMapClockwise() {
		transposeArray(currentState.terrain);
		reverseColumns(currentState.terrain);
		transposeArray(currentState.furniture);
		reverseColumns(currentState.furniture);
		currentState.placeGroupZones.forEach(PlaceGroupZone::rotate);
		renderer.redraw();
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
	}

	private UndoEvent undoEvent = new UndoEvent();

	public void finishEdit(final String operationName) {
		undoEvent.setName(operationName);
		manager.addUndoEvent(undoEvent);
		undoEvent = new UndoEvent();
		changedTiles.clear();
	}

	public void setTile(final int x, final int y, final Tile tile) {

		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return;
		}

		String terrainBefore = getTerrainAt(x, y);
		String furnitureBefore = getFurnitureAt(x, y);

		//TODO get terrain type instead of just checking if furniture for things like items (?)
		if (tile.isFurniture()) {
			if (!changedTiles.contains(new Point(x, y))) {
				undoEvent.addAction(new TileChangeAction(this, Layer.FURNITURE, new Point(x, y), furnitureBefore, tile.getID()));
				changedTiles.add(new Point(x, y));
			}
			currentState.furniture[x][y] = tile.getID();
		} else {
			if (!changedTiles.contains(new Point(x, y))) {
				undoEvent.addAction(new TileChangeAction(this, Layer.TERRAIN, new Point(x, y), terrainBefore, tile.getID()));
				changedTiles.add(new Point(x, y));
			}
			currentState.terrain[x][y] = tile.getID();
		}

		updateTile(x, y);
		updateTilesSurrounding(x, y);

		if (!getTerrainAt(x, y).equals(terrainBefore) || !getFurnitureAt(x, y).equals(furnitureBefore)) {
			renderer.redraw(x, y);
		}

	}

	public void setTile(final Point location, final Layer layer, final String tile) {

		String terrainBefore = getTerrainAt(location.x, location.y);
		String furnitureBefore = getFurnitureAt(location.x, location.y);

		if (layer == Layer.TERRAIN) {
			currentState.terrain[location.x][location.y] = tile;
		} else {
			currentState.furniture[location.x][location.y] = tile;
		}

		if (!getTerrainAt(location.x, location.y).equals(terrainBefore) || !getFurnitureAt(location.x, location.y).equals(furnitureBefore)) {
			renderer.redraw(location.x, location.y);
		}

	}

	public void addPlaceGroupZone(final int index, final PlaceGroupZone zone) {
		currentState.placeGroupZones.add(index, zone);
		renderer.redrawPlaceGroups();
	}

	public void addPlaceGroupZone(final PlaceGroupZone zone) {
		currentState.placeGroupZones.add(zone);
		renderer.redrawPlaceGroups();
	}

	public void removePlaceGroupZone(final PlaceGroupZone zone) {
		currentState.placeGroupZones.remove(zone);
		renderer.redrawPlaceGroups();
	}

	public PlaceGroupZone getPlaceGroupZoneAt(final int x, final int y) {
		for (PlaceGroupZone zone : currentState.placeGroupZones) {
			if (zone.contains(x, y)) {
				return zone;
			}
		}
		return null;
	}

	public List<PlaceGroupZone> getPlaceGroupZonesAt(final int x, final int y) {
		return currentState.placeGroupZones.stream().filter(zone -> zone.contains(x, y)).collect(Collectors.toList());
	}

	public List<PlaceGroupZone> getPlaceGroupZones() {
		return new ArrayList<>(currentState.placeGroupZones);
	}

	private void updateTilesSurrounding(final int x, final int y) {
		updateTile(x - 1, y);
		updateTile(x + 1, y);
		updateTile(x, y - 1);
		updateTile(x, y + 1);
	}

	private void updateTile(final int x, final int y) {

		String tile = getTerrainAt(x, y);

		if (tile.endsWith("_v") || tile.endsWith("_h")) {
			int bitwiseMapping = getBitwiseMapping(x, y, Layer.TERRAIN);
			currentState.terrain[x][y] = tile.substring(0, tile.lastIndexOf("_"));
			currentState.terrain[x][y] += BITWISE_FORCE_ORIENTATION[bitwiseMapping] == Orientation.HORIZONTAL ? "_h" : "_v";
		}

	}

	public int getBitwiseMapping(final int x, final int y, final Layer layer) {

		String current = getTileAt(x, y, layer);

		byte tilemap = 0;

		if (current.isEmpty()) {
			return 0;
		}

		if (getTileAt(x, y + 1, layer) != null && Tile.tilesConnect(getTileAt(x, y + 1, layer), current)) {
			tilemap += 1;
		}

		if (getTileAt(x + 1, y, layer) != null && Tile.tilesConnect(getTileAt(x + 1, y, layer), current)) {
			tilemap += 2;
		}

		if (getTileAt(x, y - 1, layer) != null && Tile.tilesConnect(getTileAt(x, y - 1, layer), current)) {
			tilemap += 4;
		}

		if (getTileAt(x - 1, y, layer) != null && Tile.tilesConnect(getTileAt(x - 1, y, layer), current)) {
			tilemap += 8;
		}

		return tilemap;

	}

	public String getTileAt(final int x, final int y, final Layer layer) {
		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return "";
		}
		if (layer == Layer.TERRAIN) {
			if (currentState.terrain[x][y] == null) {
				return "";
			}
			return currentState.terrain[x][y];
		} else {
			if (currentState.furniture[x][y] == null) {
				return "";
			}
			return currentState.furniture[x][y];
		}
	}

	public String getTerrainAt(final int x, final int y) {
		return getTileAt(x, y, Layer.TERRAIN);
	}

	public String getFurnitureAt(final int x, final int y) {
		return getTileAt(x, y, Layer.FURNITURE);
	}

	protected boolean isSaved() {
		return lastSavedState == null || currentState.equals(lastSavedState);
	}

	@Override
	public String toString() {
		return "Map"; //TODO Use map name
	}

}
