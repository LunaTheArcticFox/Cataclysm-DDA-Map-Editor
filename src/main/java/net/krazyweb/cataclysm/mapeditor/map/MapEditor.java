package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import net.krazyweb.cataclysm.mapeditor.MapRenderer;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.undo.*;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class MapEditor {

	public static final int SIZE = 24;

	private static Logger log = LogManager.getLogger(MapEditor.class);

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

	protected MapgenEntry currentMap;

	private MapRenderer renderer;

	private UndoEvent undoEvent = new UndoEvent();
	private Map<MapgenEntry, UndoBuffer> undoBuffers = new IdentityHashMap<>();
	private Set<Point> changedTiles = new HashSet<>();
	private boolean editing = false;

	protected MapEditor(final EventBus eventBus) {
		eventBus.register(this);
	}

	protected void setRenderer(final MapRenderer renderer) {
		this.renderer = renderer;
	}

	public void rotateMapClockwise() {
		if (editing) {
			undoEvent.addAction(new RotateMapAction(this));
		}
		transposeArray(currentMap.terrain);
		reverseColumns(currentMap.terrain);
		transposeArray(currentMap.furniture);
		reverseColumns(currentMap.furniture);
		currentMap.placeGroupZones.forEach(PlaceGroupZone::rotate);
		renderer.redraw();
	}

	private void transposeArray(final String[][] array) {
		for(int i = 0; i < SIZE; i++) {
			for(int j = i + 1; j < SIZE; j++) {
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

	public void startEdit() {
		if (!editing) {
			undoEvent = new UndoEvent();
			editing = true;
		}
	}

	public void finishEdit(final String operationName) {
		undoEvent.setName(operationName);
		undoBuffers.get(currentMap).addEvent(undoEvent);
		changedTiles.clear();
		editing = false;
	}

	public void setTile(final int x, final int y, final Tile tile) {

		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return;
		}



		String terrainBefore = getTerrainAt(x, y);
		String furnitureBefore = getFurnitureAt(x, y);

		//TODO get terrain type instead of just checking if furniture for things like items (?)
		if (tile.isFurniture()) {
			if (editing && !changedTiles.contains(new Point(x, y))) {
				undoEvent.addAction(new TileChangeAction(this, Layer.FURNITURE, new Point(x, y), furnitureBefore, tile.getID()));
				changedTiles.add(new Point(x, y));
			}
			currentMap.furniture[x][y] = tile.getID();
		} else {
			if (editing && !changedTiles.contains(new Point(x, y))) {
				undoEvent.addAction(new TileChangeAction(this, Layer.TERRAIN, new Point(x, y), terrainBefore, tile.getID()));
				changedTiles.add(new Point(x, y));
			}
			currentMap.terrain[x][y] = tile.getID();
		}

		updateTile(x, y);
		updateTilesSurrounding(x, y);

		if (!getTerrainAt(x, y).equals(terrainBefore) || !getFurnitureAt(x, y).equals(furnitureBefore)) {
			renderer.redraw(x, y);
		}

	}

	public void setTile(final Point location, final Layer layer, final String tile) {

		if (location.x < 0 || location.y < 0 || location.x >= SIZE || location.y >= SIZE) {
			return;
		}

		String terrainBefore = getTerrainAt(location.x, location.y);
		String furnitureBefore = getFurnitureAt(location.x, location.y);

		if (layer == Layer.TERRAIN) {
			currentMap.terrain[location.x][location.y] = tile;
		} else {
			currentMap.furniture[location.x][location.y] = tile;
		}

		if (!getTerrainAt(location.x, location.y).equals(terrainBefore) || !getFurnitureAt(location.x, location.y).equals(furnitureBefore)) {
			renderer.redraw(location.x, location.y);
		}

	}

	public void addPlaceGroupZone(final int index, final PlaceGroupZone zone) {
		if (editing) {
			undoEvent.addAction(new PlaceGroupZoneAddedAction(this, index, zone));
		}
		currentMap.placeGroupZones.add(index, zone);
		renderer.redrawPlaceGroups();
	}

	public void addPlaceGroupZone(final PlaceGroupZone zone) {
		if (editing) {
			undoEvent.addAction(new PlaceGroupZoneAddedAction(this, zone));
		}
		currentMap.placeGroupZones.add(zone);
		renderer.redrawPlaceGroups();
	}

	public void removePlaceGroupZone(final PlaceGroupZone zone) {
		if (editing) {
			undoEvent.addAction(new PlaceGroupZoneRemovedAction(this, currentMap.placeGroupZones.indexOf(zone), zone));
		}
		currentMap.placeGroupZones.remove(zone);
		renderer.redrawPlaceGroups();
	}

	public void movePlaceGroupZone(final PlaceGroupZone zone, final int deltaX, final int deltaY) {
		if (editing) {
			undoEvent.addAction(new PlaceGroupZoneMovedAction(this, zone, deltaX, deltaY));
		}
		zone.x += deltaX;
		zone.y += deltaY;
	}

	public void modifyPlaceGroup(final PlaceGroup placeGroup, final String type, final String group, final int chance) {
		if (editing) {
			undoEvent.addAction(new PlaceGroupModifiedAction(this, placeGroup, type, group, chance));
		}
		placeGroup.type = type;
		placeGroup.group = group;
		placeGroup.chance = chance;
	}

	public PlaceGroupZone getPlaceGroupZoneAt(final int x, final int y) {
		for (PlaceGroupZone zone : currentMap.placeGroupZones) {
			if (zone.contains(x, y)) {
				return zone;
			}
		}
		return null;
	}

	public List<PlaceGroupZone> getPlaceGroupZonesAt(final int x, final int y) {
		return currentMap.placeGroupZones.stream().filter(zone -> zone.contains(x, y)).collect(Collectors.toList());
	}

	public List<PlaceGroupZone> getPlaceGroupZones() {
		return new ArrayList<>(currentMap.placeGroupZones);
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
			currentMap.terrain[x][y] = tile.substring(0, tile.lastIndexOf("_"));
			currentMap.terrain[x][y] += BITWISE_FORCE_ORIENTATION[bitwiseMapping] == Orientation.HORIZONTAL ? "_h" : "_v";
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
			if (currentMap.terrain[x][y] == null) {
				return "";
			}
			return currentMap.terrain[x][y];
		} else {
			if (currentMap.furniture[x][y] == null) {
				return "";
			}
			return currentMap.furniture[x][y];
		}
	}

	public String getTerrainAt(final int x, final int y) {
		return getTileAt(x, y, Layer.TERRAIN);
	}

	public String getFurnitureAt(final int x, final int y) {
		return getTileAt(x, y, Layer.FURNITURE);
	}

	protected void setMapgenEntry(final MapgenEntry mapgenEntry) {
		if (!undoBuffers.containsKey(mapgenEntry)) {
			undoBuffers.put(mapgenEntry, new UndoBuffer());
		}
		currentMap = mapgenEntry;
		renderer.redraw();
	}

	public UndoBuffer getUndoBuffer() {
		return undoBuffers.get(currentMap);
	}

	@Override
	public String toString() {
		return currentMap.settings.overMapTerrain;
	}

}
