package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.ApplicationSettings;
import net.krazyweb.cataclysm.mapeditor.MapRenderer;
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.data.*;
import net.krazyweb.cataclysm.mapeditor.map.undo.UndoBufferListener;
import net.krazyweb.util.CloseAction;
import net.krazyweb.util.FXMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

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
	private List<MapgenEntry> mapInsertionOrder = new ArrayList<>();
	private List<ItemGroupEntry> itemGroupEntries = new ArrayList<>();
	private List<MonsterGroupEntry> monsterGroupEntries = new ArrayList<>();
	private List<OvermapEntry> overmapEntries = new ArrayList<>();

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
			if (!Files.isSameFile(path, ApplicationSettings.DEFAULT_NEW_FILE)) {
				this.path = path;
			} else {
				this.path = null;
			}
		} catch (IOException e) {
			log.error("Error while determining if '" + path.toAbsolutePath() + "' is the same file as the default template:", e);
		}

		itemGroupEntries.clear();
		monsterGroupEntries.clear();
		overmapEntries.clear();
		maps.values().forEach(eventBus::unregister);
		maps.clear();
		root.getTabs().clear();
		if (mapEditor.getUndoBuffer() != null) {
			updateUndoRedoText();
		}

		DataFileReader dataFileReader = new DataFileReader(path);
		dataFileReader.setOnSucceeded(event -> {

			itemGroupEntries.addAll(dataFileReader.getItemGroupEntries());
			monsterGroupEntries.addAll(dataFileReader.getMonsterGroupEntries());
			overmapEntries.addAll(dataFileReader.getOvermapEntries());

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

			eventBus.post(new FileLoadedEvent(path, dataFileReader.getMaps()));

		});

		dataFileReader.start();

	}

	public void addMap(final Path path) {

		DataFileReader dataFileReader = new DataFileReader(path);
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

		mapInsertionOrder.add(map);

		Tab tab = new Tab(map.settings.overmapTerrain);

		maps.put(tab, map);
		root.getTabs().add(tab);

	}

	public void save() {
		save(path);
	}

	public void save(final Path path) {

		//TODO Service-ify and run on separate thread

		List<MapgenEntry> mapgenEntries = new ArrayList<>(maps.values());
		Collections.sort(mapgenEntries, (map1, map2) -> {

			if (mapInsertionOrder.indexOf(map1) > mapInsertionOrder.indexOf(map2)) {
				return 1;
			} else if (mapInsertionOrder.indexOf(map2) >  mapInsertionOrder.indexOf(map1)) {
				return -1;
			}

			return 0;

		});

		List<String> lines = new ArrayList<>();

		lines.add("[");

		itemGroupEntries.forEach(itemGroupEntry -> {

			itemGroupEntry.getJsonLines().forEach(line -> lines.add(Jsonable.INDENT + line));

			if (itemGroupEntries.indexOf(itemGroupEntry) != itemGroupEntries.size() - 1) {
				String line = lines.remove(lines.size() - 1);
				lines.add(line + ",");
			} else if (itemGroupEntries.indexOf(itemGroupEntry) == itemGroupEntries.size() - 1) {
				if (mapgenEntries.size() > 0 || overmapEntries.size() > 0 || monsterGroupEntries.size() > 0) {
					String line = lines.remove(lines.size() - 1);
					lines.add(line + ",");
				}
			}

			itemGroupEntry.markSaved();

		});

		monsterGroupEntries.forEach(monsterGroupEntry -> {

			monsterGroupEntry.getJsonLines().forEach(line -> lines.add(Jsonable.INDENT + line));

			if (monsterGroupEntries.indexOf(monsterGroupEntry) != monsterGroupEntries.size() - 1) {
				String line = lines.remove(lines.size() - 1);
				lines.add(line + ",");
			} else if (monsterGroupEntries.indexOf(monsterGroupEntry) == monsterGroupEntries.size() - 1) {
				if (mapgenEntries.size() > 0 || overmapEntries.size() > 0) {
					String line = lines.remove(lines.size() - 1);
					lines.add(line + ",");
				}
			}

			monsterGroupEntry.markSaved();

		});

		overmapEntries.forEach(overmapEntry -> {

			overmapEntry.getJsonLines().forEach(line -> lines.add(Jsonable.INDENT + line));

			if (overmapEntries.indexOf(overmapEntry) != overmapEntries.size() - 1) {
				String line = lines.remove(lines.size() - 1);
				lines.add(line + ",");
			} else if (overmapEntries.indexOf(overmapEntry) == overmapEntries.size() - 1) {
				if (mapgenEntries.size() > 0) {
					String line = lines.remove(lines.size() - 1);
					lines.add(line + ",");
				}
			}

			overmapEntry.markSaved();

		});

		Collections.reverse(mapgenEntries);

		mapgenEntries.forEach(map -> {

			map.getJsonLines().forEach(line -> lines.add(Jsonable.INDENT + line));

			if (mapgenEntries.indexOf(map) != mapgenEntries.size() - 1) {
				String line = lines.remove(lines.size() - 1);
				lines.add(line + ",");
			}

			map.markSaved();

		});

		Collections.reverse(mapgenEntries);

		lines.add("]");

		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)){
			lines.forEach(line -> {
				try {
					writer.append(line).append(System.lineSeparator());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.path = path;

		eventBus.post(new FileSavedEvent(path));

	}

	public void revert() {
		if (path == null) {
			load(ApplicationSettings.DEFAULT_NEW_FILE);
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

	private void refreshTabName() {
		root.getSelectionModel().getSelectedItem().setText(mapEditor.currentMap.settings.overmapTerrain);
	}

	public void editMapProperties() {
		mapEditor.editMapProperties();
	}

	public void editDefinitions() {

		FXMLLoader loader = FXMLHelper.loadFXML("/fxml/definitionsEditor/editorDialog.fxml").orElseThrow(IllegalStateException::new);
		DefinitionsEditor editor = loader.<DefinitionsEditor>getController();

		editor.setItemGroupEntries(itemGroupEntries);
		editor.setMonsterGroupEntries(monsterGroupEntries);
		editor.setOvermapEntries(overmapEntries);

		Stage stage = new Stage();
		stage.setScene(new Scene(loader.getRoot()));
		stage.setTitle("Definitions Editor");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();

		if (editor.getCloseAction() == CloseAction.SAVE) {
			itemGroupEntries = new ArrayList<>(editor.getItemGroupEntries());
			monsterGroupEntries = new ArrayList<>(editor.getMonsterGroupEntries());
			overmapEntries = new ArrayList<>(editor.getOvermapEntries());
			eventBus.post(new DefinitionsChangedEvent());
		}

	}

	public boolean isSaved() {
		for (MapgenEntry map : maps.values()) {
			if (!map.isSaved()) {
				return false;
			}
		}
		for (ItemGroupEntry itemGroupEntry : itemGroupEntries) {
			if (!itemGroupEntry.isSaved()) {
				return false;
			}
		}
		for (MonsterGroupEntry monsterGroupEntry : monsterGroupEntries) {
			if (!monsterGroupEntry.isSaved()) {
				return false;
			}
		}
		for (OvermapEntry overmapEntry : overmapEntries) {
			if (!overmapEntry.isSaved()) {
				return false;
			}
		}
		return true;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public void undoBufferChanged() {
		updateUndoRedoText();
		refreshTabName();
		eventBus.post(new MapChangedEvent());
	}

}
