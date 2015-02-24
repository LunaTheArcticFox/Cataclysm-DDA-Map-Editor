package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import net.krazyweb.cataclysm.mapeditor.MapRenderer;
import net.krazyweb.cataclysm.mapeditor.events.UndoBufferChangedEvent;
import net.krazyweb.cataclysm.mapeditor.map.undo.UndoBufferListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.Map;

public class MapManager implements UndoBufferListener {

	private static Logger log = LogManager.getLogger(MapManager.class);

	@FXML
	private TabPane root;

	private Path path;
	private EventBus eventBus;

	private MapEditor mapEditor;
	private ScrollPane mapContainer;

	private boolean listenerRegistered = false;

	private Map<Tab, MapgenEntry> maps = new IdentityHashMap<>();

	@FXML
	public void initialize() {
		VBox.setVgrow(root, Priority.ALWAYS);
	}

	public void setEventBus(final EventBus eventBus) {

		this.eventBus = eventBus;

		mapEditor = new MapEditor(eventBus);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mapCanvas.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			log.error("Error while loading '/fxml/mapCanvas.fxml':", e);
		}
		eventBus.register(loader.<MapRenderer>getController());
		loader.<MapRenderer>getController().setEventBus(eventBus);
		loader.<MapRenderer>getController().setMapEditor(mapEditor);
		mapEditor.setRenderer(loader.<MapRenderer>getController());


		GridPane centeredContentPane = new GridPane();
		centeredContentPane.add(loader.<ScrollPane>getRoot(), 0, 0);

		RowConstraints row = new RowConstraints();
		row.setPercentHeight(100);
		row.setFillHeight(false);
		row.setValignment(VPos.CENTER);
		centeredContentPane.getRowConstraints().add(row);

		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(100);
		column.setFillWidth(false);
		column.setHalignment(HPos.CENTER);
		centeredContentPane.getColumnConstraints().add(column);

		mapContainer = new ScrollPane(centeredContentPane);
		mapContainer.setPrefWidth(100);
		mapContainer.setPrefHeight(100);
		mapContainer.setFocusTraversable(false);
		mapContainer.setFitToWidth(true);
		mapContainer.setFitToHeight(true);

	}

	public void load(final Path path) {
		//Load: Spawn file read service to get map sections and load each one

		try {
			if (!Files.isSameFile(path, Paths.get("templates").resolve("default.json"))) {
				this.path = path;
			}
		} catch (IOException e) {
			log.error("Error while determining if '" + path.toAbsolutePath() + "' is the same file as the default template:", e);
		}

		//TODO Unregister old maps
		root.getTabs().clear();
		if (mapEditor.getUndoBuffer() != null) {
			updateUndoRedoText();
		}

		DataFileReader dataFileReader = new DataFileReader(path, eventBus);
		dataFileReader.setOnSucceeded(event -> {

			dataFileReader.getMaps().forEach(this::loadMap);

			if (!listenerRegistered) {
				root.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
					if (oldTab != null) {
						oldTab.setContent(null);
						mapEditor.getUndoBuffer().unregister(this);
					}
					if (newTab != null) {
						newTab.setContent(mapContainer);
						mapEditor.setMapgenEntry(maps.get(newTab));
						mapEditor.getUndoBuffer().register(this);
						updateUndoRedoText();
					}
				});
				listenerRegistered = true;
			}

			mapEditor.setMapgenEntry(maps.get(root.getTabs().get(0)));
			mapEditor.getUndoBuffer().register(this);
			root.getTabs().get(0).setContent(mapContainer);

		});

		dataFileReader.start();

	}

	public void addMap(final Path path) {

		DataFileReader dataFileReader = new DataFileReader(path, eventBus);
		dataFileReader.setOnSucceeded(event -> {
			dataFileReader.getMaps().forEach(this::loadMap);
			mapEditor.setMapgenEntry(maps.get(root.getTabs().get(root.getTabs().size() - 1)));
			root.getSelectionModel().select(root.getTabs().get(root.getTabs().size() - 1));
			root.getTabs().get(root.getTabs().size() - 1).setContent(mapContainer);
		});

		dataFileReader.start();

	}

	private void loadMap(final MapgenEntry map) {

		log.info("Adding map '" + map + "' to MapManager.");

		Tab tab = new Tab(map.settings.overMapTerrain); //TODO Rename tab when changed

		maps.put(tab, map);
		root.getTabs().add(tab);

	}

	public void save() {
		save(path);
	}

	public void save(final Path path) {
		//Save: For each map, get mapgen section, write extra sections
		this.path = path;
	}

	public void revert() {
		if (path == null) {
			load(Paths.get("templates").resolve("default.json"));
		} else {
			load(path);
		}
	}

	public void undo() {

		if (!mapEditor.getUndoBuffer().hasPreviousEvent()) {
			return;
		}

		mapEditor.getUndoBuffer().undoLastEvent();

	}

	public void redo() {

		if (!mapEditor.getUndoBuffer().hasNextEvent()) {
			return;
		}

		mapEditor.getUndoBuffer().redoNextEvent();

	}

	public void rotateMap() {
		mapEditor.startEdit();
		mapEditor.rotateMapClockwise();
		mapEditor.finishEdit("Rotate Map");
	}

	private void updateUndoRedoText() {

		String undoText = "";
		String redoText = "";

		if (mapEditor.getUndoBuffer().hasPreviousEvent()) {
			undoText = mapEditor.getUndoBuffer().getCurrentEvent().getName();
		}

		if (mapEditor.getUndoBuffer().hasNextEvent()) {
			redoText = mapEditor.getUndoBuffer().peekAtNextEvent().getName();
		}

		eventBus.post(new UndoBufferChangedEvent(undoText, redoText));

	}

	public boolean isSaved() {
		return false; //TODO
	}

	public Path getPath() {
		return path;
	}

	@Override
	public void undoBufferChanged() {
		updateUndoRedoText();
	}

}
