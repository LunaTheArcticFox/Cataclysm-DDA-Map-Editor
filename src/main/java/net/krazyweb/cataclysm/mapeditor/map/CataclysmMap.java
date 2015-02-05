package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.events.TileRedrawRequestEvent;

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

	protected static class State {
		protected String[][] terrain = new String[SIZE][SIZE];
		protected String[][] furniture = new String[SIZE][SIZE];
	}

	public static enum Layer {
		TERRAIN, FURNITURE
	}

	protected State currentState = new State();

	private boolean locked = false;
	private boolean editing = false;

	private EventBus eventBus;

	public CataclysmMap(final EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	public void startEdit() {
		editing = true;
		saveUndoState();
	}

	public void finishEdit() {
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
			currentState.furniture[x][y] = tile.getID();
		} else {
			currentState.terrain[x][y] = tile.getID();
		}

		updateTile(x, y);
		updateTilesSurrounding(x, y);

		if (!getTerrainAt(x, y).equals(terrainBefore) || !getFurnitureAt(x, y).equals(furnitureBefore)) {
			eventBus.post(new TileRedrawRequestEvent(x, y));
		}

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

	/**
	 * Saves the current state in the undo buffer
	 */
	private void saveUndoState() {
		//TODO Clone and add state to undo buffer
	}

	private void cloneArray(final String[][] source, final String[][] destination) {

	}

}
