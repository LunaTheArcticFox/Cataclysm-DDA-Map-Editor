package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.krazyweb.cataclysm.mapeditor.MapRenderer;
import net.krazyweb.cataclysm.mapeditor.map.data.MapSettings;
import net.krazyweb.cataclysm.mapeditor.map.data.MapgenEntry;
import net.krazyweb.cataclysm.mapeditor.map.data.PlaceGroup;
import net.krazyweb.cataclysm.mapeditor.map.data.PlaceGroupZone;
import net.krazyweb.cataclysm.mapeditor.map.undo.*;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class MapEditor {

	private static final Logger log = LogManager.getLogger(MapEditor.class);

	public static final int SIZE = 24;

	private enum Orientation {
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

	public enum Layer {
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
		transposeArray(currentMap.tiles);
		reverseColumns(currentMap.tiles);
		currentMap.placeGroupZones.forEach(PlaceGroupZone::rotate);
		renderer.redraw();
	}

	private void transposeArray(final MapTile[][] array) {
		for(int i = 0; i < SIZE; i++) {
			for(int j = i + 1; j < SIZE; j++) {
				MapTile temp = array[i][j];
				array[i][j] = array[j][i];
				array[j][i] = temp;
			}
		}
	}

	private void reverseColumns(final MapTile[][] array) {
		for(int j = 0; j < array.length; j++){
			for(int i = 0; i < array[j].length / 2; i++) {
				MapTile temp = array[i][j];
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

	public void setTile(final int x, final int y, final MapTile tile) {

		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return;
		}

		MapTile before = currentMap.tiles[x][y];

		if (editing && !changedTiles.contains(new Point(x, y))) {
			undoEvent.addAction(new TileChangeAction(this, new Point(x, y), before, tile));
			changedTiles.add(new Point(x, y));
		}

		currentMap.tiles[x][y] = tile;

		if (!currentMap.tiles[x][y].equals(before)) {
			renderer.redraw(x, y);
		}

	}

	public void setTile(final Point location, final MapTile tile) {
		setTile(location.x, location.y, tile);
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
		zone.bounds.shift(deltaX, deltaY);
		renderer.redrawPlaceGroups();
	}

	public void modifyPlaceGroup(final PlaceGroup placeGroup, final PlaceGroup.Type type, final String group, final int chance) {
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

	/*private void updateTilesSurrounding(final int x, final int y) {
		updateTile(x - 1, y);
		updateTile(x + 1, y);
		updateTile(x, y - 1);
		updateTile(x, y + 1);
	}*/

	/*private void updateTile(final int x, final int y) {

		String tile = getTerrainAt(x, y);

		if (tile.endsWith("_v") || tile.endsWith("_h")) {
			int bitwiseMapping = getBitwiseMapping(x, y, Layer.TERRAIN);
			currentMap.tiles[x][y] = tile.substring(0, tile.lastIndexOf("_"));
			currentMap.tiles[x][y] += BITWISE_FORCE_ORIENTATION[bitwiseMapping] == Orientation.HORIZONTAL ? "_h" : "_v";
		}

	}*/

	public int getBitwiseMapping(final int x, final int y) {

		//String current = getTileAt(x, y);

		byte tilemap = 0;

		/*if (current.isEmpty()) {
			return 0;
		}

		if (getTileAt(x, y + 1) != null && Tile.tilesConnect(getTileAt(x, y + 1), current)) {
			tilemap += 1;
		}

		if (getTileAt(x + 1, y) != null && Tile.tilesConnect(getTileAt(x + 1, y), current)) {
			tilemap += 2;
		}

		if (getTileAt(x, y - 1) != null && Tile.tilesConnect(getTileAt(x, y - 1), current)) {
			tilemap += 4;
		}

		if (getTileAt(x - 1, y) != null && Tile.tilesConnect(getTileAt(x - 1, y), current)) {
			tilemap += 8;
		}*/

		return tilemap;

	}

	public MapTile getTileAt(final int x, final int y) {
		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return null;
		}
		return currentMap.tiles[x][y];
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

	public void editMapProperties(){

		Dialog<MapSettings> settingsDialog = new Dialog<>();
		settingsDialog.setTitle("Edit Map Properties");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);
		grid.setPadding(new Insets(0, 10, 10, 10));

		TextField overmapTerrain = new TextField(currentMap.settings.overmapTerrain);

		VBox terrainBox = new VBox();
		terrainBox.setSpacing(5);
		terrainBox.getChildren().add(new Label("Terrain:"));
		terrainBox.getChildren().add(overmapTerrain);

		grid.add(terrainBox, 1, 1);

		TextField weight = new TextField(currentMap.settings.weight + "");

		VBox weightBox = new VBox();
		weightBox.setSpacing(5);
		weightBox.getChildren().add(new Label("Weight:"));
		weightBox.getChildren().add(weight);
		grid.add(weightBox, 1, 2);

		settingsDialog.getDialogPane().setContent(grid);

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		settingsDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		settingsDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				return new MapSettings(overmapTerrain.getText(), Integer.parseInt(weight.getText()));
			}
			return null;
		});

		Platform.runLater(overmapTerrain::requestFocus);

		Optional<MapSettings> result = settingsDialog.showAndWait();

		result.ifPresent(mapSettings -> {
			if (!mapSettings.equals(currentMap.settings)) {
				startEdit();
				setMapSettings(mapSettings);
				finishEdit("Edit Map Settings");
			}
		});

	}

	public void setMapSettings(final MapSettings mapSettings) {

		MapSettings old = currentMap.settings;

		currentMap.settings = mapSettings;

		if (editing) {
			undoEvent.addAction(new MapSettingsChangeAction(this, old, mapSettings));
		}

	}

	@Override
	public String toString() {
		return currentMap.settings.overmapTerrain;
	}

}
