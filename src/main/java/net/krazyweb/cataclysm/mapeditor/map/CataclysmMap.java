package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.events.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	protected static class State {

		protected String lastOperation = "";
		protected String[][] terrain = new String[SIZE][SIZE];
		protected String[][] furniture = new String[SIZE][SIZE];
		protected List<PlaceGroupZone> placeGroupZones = new ArrayList<>();

		@SuppressWarnings("CloneDoesntCallSuperClone")
		@Override
		protected State clone() throws CloneNotSupportedException {
			State state = new State();
			state.lastOperation = lastOperation;
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					state.terrain[x][y] = terrain[x][y];
					state.furniture[x][y] = furniture[x][y];
				}
			}
			placeGroupZones.forEach(zone -> state.placeGroupZones.add(new PlaceGroupZone(zone)));
			return state;
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			State state = (State) o;

			if (lastOperation != null ? !lastOperation.equals(state.lastOperation) : state.lastOperation != null) {
				return false;
			}
			if (placeGroupZones != null ? !placeGroupZones.equals(state.placeGroupZones) : state.placeGroupZones != null) {
				return false;
			}

			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					if (!state.terrain[x][y].equals(terrain[x][y]) || !state.furniture[x][y].equals(furniture[x][y])) {
						return false;
					}
				}
			}

			return true;

		}

		@Override
		public int hashCode() {
			int result = lastOperation != null ? lastOperation.hashCode() : 0;
			result = 31 * result + (placeGroupZones != null ? placeGroupZones.hashCode() : 0);
			result = 31 * result + Arrays.deepHashCode(terrain);
			result = 31 * result + Arrays.deepHashCode(furniture);
			return result;
		}

	}

	public static enum Layer {
		TERRAIN, FURNITURE
	}

	protected UndoBuffer undoBuffer = new UndoBuffer();

	protected State lastSavedState = null;
	protected State currentState = new State();
	protected Path path = null;

	private EventBus eventBus;

	public CataclysmMap(final EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void requestSaveMapEventListener(final RequestSaveMapEvent event) {

		//TODO Thread this; copy state so editing may continue on long saves
		MapDataFileWriter writer = new MapDataFileWriter(event.getPath(), this, eventBus);
		writer.setOnSucceeded(e -> eventBus.post(new MapSavedEvent(this)));

		//TODO Lock editing while saving (or copy state to save so editing can continue)
		writer.start();

	}

	@Subscribe
	public void revertMapEventListener(final RequestRevertMapEvent event) {
		if (path == null) {
			eventBus.post(new RequestLoadMapEvent(Paths.get("templates").resolve("default.json")));
		} else {
			eventBus.post(new RequestLoadMapEvent(path));
		}
	}

	@Subscribe
	public void rotateMapEventListener(final RotateMapEvent event) {
		rotateMapClockwise();
		finishEdit("Rotate");
	}

	@Subscribe
	public void requestUndoEventListener(final RequestUndoEvent event) {
		undo();
	}

	@Subscribe
	public void requestRedoEventListener(final RequestRedoEvent event) {
		redo();
	}

	private void undo() {
		if (!undoBuffer.hasPreviousEvent()) {
			return;
		}
		eventBus.post(new UpdateRedoTextEvent(currentState.lastOperation));
		try {
			currentState = undoBuffer.undoLastEvent().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		eventBus.post(new UpdateUndoTextEvent(currentState.lastOperation));
		eventBus.post(new MapChangedEvent());
		eventBus.post(new MapRedrawRequestEvent());
	}

	private void redo() {
		if (!undoBuffer.hasNextEvent()) {
			return;
		}
		try {
			currentState = undoBuffer.getNextEvent().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		if (undoBuffer.hasNextEvent()) {
			eventBus.post(new UpdateRedoTextEvent(undoBuffer.peekAtNextEvent().lastOperation));
		} else {
			eventBus.post(new UpdateRedoTextEvent(""));
		}
		eventBus.post(new UpdateUndoTextEvent(currentState.lastOperation));
		eventBus.post(new MapChangedEvent());
		eventBus.post(new MapRedrawRequestEvent());
	}

	private void rotateMapClockwise() {
		transposeArray(currentState.terrain);
		reverseColumns(currentState.terrain);
		transposeArray(currentState.furniture);
		reverseColumns(currentState.furniture);
		currentState.placeGroupZones.forEach(net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone::rotate);
		eventBus.post(new MapRedrawRequestEvent());
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

	public void finishEdit(final String operationName) {
		currentState.lastOperation = operationName;
		saveUndoState();
		eventBus.post(new MapChangedEvent());
		eventBus.post(new UpdateUndoTextEvent(operationName));
		eventBus.post(new UpdateRedoTextEvent(""));
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

	public void addPlaceGroupZone(final int index, final PlaceGroupZone zone) {
		currentState.placeGroupZones.add(index, zone);
		eventBus.post(new PlaceGroupRedrawRequestEvent());
	}

	public void addPlaceGroupZone(final PlaceGroupZone zone) {
		currentState.placeGroupZones.add(zone);
		eventBus.post(new PlaceGroupRedrawRequestEvent());
	}

	public void removePlaceGroupZone(final PlaceGroupZone zone) {
		currentState.placeGroupZones.remove(zone);
		eventBus.post(new PlaceGroupRedrawRequestEvent());
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

	public Path getPath() {
		return path;
	}

	public boolean isSaved() {
		return lastSavedState == null || currentState.equals(lastSavedState);
	}

	protected void saveUndoState() {
		try {
			undoBuffer.addState(currentState);
			currentState = currentState.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

}
