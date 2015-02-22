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
import net.krazyweb.cataclysm.mapeditor.map.undo.UndoBuffer;
import net.krazyweb.cataclysm.mapeditor.map.undo.UndoEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MapManager {

	private static Logger log = LogManager.getLogger(MapManager.class);

	@FXML
	private TabPane root;

	private Path path;
	private EventBus eventBus;
	private UndoBuffer undoBuffer;

	@FXML
	public void initialize() {
		VBox.setVgrow(root, Priority.ALWAYS);
	}

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void load(final Path path) {
		//Load: Spawn file read service to get map sections and load each one

		try {
			if (!Files.isSameFile(path, Paths.get("templates").resolve("default.json"))) {
				this.path = path;
			}
		} catch (IOException e) {
			log.error("An error occurred while attempting to determine if '" + path.toAbsolutePath() + "' is the same file as the default template.", e);
		}

		//TODO Unregister old maps
		root.getTabs().clear();
		undoBuffer = new UndoBuffer();
		updateUndoRedoText();

		DataFileReader dataFileReader = new DataFileReader(path, eventBus);
		dataFileReader.setOnSucceeded(event -> dataFileReader.getMaps().forEach(this::loadMap));

		dataFileReader.start();

	}

	private void loadMap(final CataclysmMap map) {

		map.setManager(this);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mapCanvas.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			log.error("Error while attempting to load '/fxml/mapCanvas.fxml':", e);
		}
		eventBus.register(loader.<MapRenderer>getController());
		loader.<MapRenderer>getController().setEventBus(eventBus);
		loader.<MapRenderer>getController().setMap(map);
		map.setRenderer(loader.<MapRenderer>getController());

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

		ScrollPane mapContainer = new ScrollPane(centeredContentPane);
		mapContainer.setPrefWidth(100);
		mapContainer.setPrefHeight(100);
		mapContainer.setFocusTraversable(false);
		mapContainer.setFitToWidth(true);
		mapContainer.setFitToHeight(true);

		Tab tab = new Tab("map");
		tab.setContent(mapContainer);

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
		load(path);
	}

	public void undo() {

		if (!undoBuffer.hasPreviousEvent()) {
			return;
		}

		undoBuffer.undoLastEvent();
		updateUndoRedoText();

	}

	public void redo() {

		if (!undoBuffer.hasNextEvent()) {
			return;
		}

		undoBuffer.redoNextEvent();
		updateUndoRedoText();

	}

	public void rotateMap() {

	}

	protected void addUndoEvent(final UndoEvent event) {
		undoBuffer.addEvent(event);
		updateUndoRedoText();
	}

	private void updateUndoRedoText() {

		String undoText = "";
		String redoText = "";

		if (undoBuffer.hasPreviousEvent()) {
			undoText = undoBuffer.getCurrentEvent().getName();
		}

		if (undoBuffer.hasNextEvent()) {
			redoText = undoBuffer.peekAtNextEvent().getName();
		}

		eventBus.post(new UndoBufferChangedEvent(undoText, redoText));

	}

	public boolean isSaved() {
		return false; //TODO
	}

	public Path getPath() {
		return path;
	}

}
