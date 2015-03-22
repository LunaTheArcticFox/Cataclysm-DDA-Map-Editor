package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.MapManager;
import net.krazyweb.cataclysm.mapeditor.tools.Tool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class EditorMain {

	private static Logger log = LogManager.getLogger(EditorMain.class);

	@FXML
	private BorderPane root;

	@FXML
	private VBox tilePickerPanel, mapContainer;

	@FXML
	private MenuItem undoButton, redoButton;

	@FXML
	private CheckMenuItem showGridButton, showGroupsButton;

	private EventBus eventBus = new EventBus();
	private MapManager mapManager;
	private Stage primaryStage;

	@FXML
	private void initialize() {

		eventBus.register(ApplicationSettings.getInstance());

		showGridButton.setSelected(ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GRID));
		showGroupsButton.setSelected(ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GROUPS));

		Tool.setEventBus(eventBus);

		//-> Tile picker
		FXMLLoader tilePickerLoader = new FXMLLoader(getClass().getResource("/fxml/tilePicker.fxml"));
		try {
			tilePickerLoader.load();
		} catch (IOException e) {
			log.error("Error while attempting to load '/fxml/tilePicker.fxml':", e);
		}
		eventBus.register(tilePickerLoader.getController());
		tilePickerLoader.<TilePicker>getController().setEventBus(eventBus);
		tilePickerPanel.getChildren().add(tilePickerLoader.<VBox>getRoot());

		new TileSet(Paths.get("Sample Data").resolve("tileset").resolve("tile_config.json"), eventBus);

		eventBus.register(this);

		FXMLLoader mapManagerLoader = new FXMLLoader(getClass().getResource("/fxml/mapManager.fxml"));
		try {
			mapManagerLoader.load();
		} catch (IOException e) {
			log.error("Error while attempting to load '/fxml/mapManager.fxml':", e);
		}
		eventBus.register(mapManagerLoader.<MapManager>getController());
		mapManagerLoader.<MapManager>getController().setEventBus(eventBus);
		mapManager = mapManagerLoader.<MapManager>getController();
		mapContainer.getChildren().add(mapManagerLoader.<ScrollPane>getRoot());

		//-> Toolbars
		FXMLLoader mapToolbarLoader = new FXMLLoader(getClass().getResource("/fxml/mapToolbar.fxml"));
		try {
			mapToolbarLoader.load();
		} catch (IOException e) {
			log.error("Error while attempting to load '/fxml/mapToolbar.fxml':", e);
		}
		mapToolbarLoader.<MapToolbar>getController().setEventBus(eventBus);
		mapToolbarLoader.<MapToolbar>getController().setMapManager(mapManager);
		mapContainer.getChildren().add(0, mapToolbarLoader.<ScrollPane>getRoot());

		//Load each component in the main view and pass the model to them
		//-> Status bar
		FXMLLoader statusBarLoader = new FXMLLoader(getClass().getResource("/fxml/statusBar.fxml"));
		try {
			statusBarLoader.load();
		} catch (IOException e) {
			log.error("Error while attempting to load '/fxml/statusBar.fxml':", e);
		}
		eventBus.register(statusBarLoader.<MapRenderer>getController());
		statusBarLoader.<StatusBarController>getController().setEventBus(eventBus);
		root.setBottom(statusBarLoader.<AnchorPane>getRoot());

		newFile();

	}

	@Subscribe
	public void mapSavedEventListener(final FileSavedEvent event) {
		refreshTitle();
	}

	@Subscribe
	public void mapLoadedEventListener(final FileLoadedEvent event) {
		refreshTitle();
		undoButton.setText("_Undo");
		redoButton.setText("_Redo");
	}

	@Subscribe
	public void mapChangedEventListener(final MapChangedEvent event) {
		refreshTitle();
	}

	@Subscribe
	public void undoRedoPerformedEventListener(final UndoBufferChangedEvent event) {
		undoButton.setText("_Undo " + event.getUndoText());
		undoButton.setDisable(event.getUndoText().isEmpty());
		redoButton.setText("_Redo " + event.getRedoText());
		redoButton.setDisable(event.getRedoText().isEmpty());
	}

	private void refreshTitle() {

		String title = "Cataclysm Map Editor - ";

		if (mapManager.getPath() != null) {
			title += mapManager.getPath().getFileName();
		} else {
			title += "Untitled";
		}

		title += mapManager.isSaved() ? "" : "*";

		primaryStage.setTitle(title);

	}

	@FXML
	private void newFile() {
		mapManager.load(ApplicationSettings.DEFAULT_NEW_FILE);
	}

	@FXML
	private void newMap() {
		mapManager.addMap(ApplicationSettings.DEFAULT_NEW_FILE);
	}

	@FXML
	private void openFile() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Pick Json File");
		fileChooser.setInitialDirectory(ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.LAST_FOLDER).toAbsolutePath().toFile());

		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Cataclysm JSON File", "*.json");
		fileChooser.getExtensionFilters().add(filter);
		fileChooser.setSelectedExtensionFilter(filter);

		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			mapManager.load(selectedFile.toPath());
		}

	}

	@FXML
	private void saveFile() {

		if (mapManager.getPath() == null) {
			saveFileAs();
		} else {
			mapManager.save();
		}

	}

	@FXML
	private void saveFileAs() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		fileChooser.setInitialDirectory(ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.LAST_FOLDER).toAbsolutePath().toFile());

		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Cataclysm JSON File", "*.json");
		fileChooser.getExtensionFilters().add(filter);
		fileChooser.setSelectedExtensionFilter(filter);

		File selectedFile = fileChooser.showSaveDialog(null);
		if (selectedFile != null) {
			mapManager.save(selectedFile.toPath());
		}

	}

	@FXML
	public void options() {
		log.info("Options pressed!");
	}

	@FXML
	private void revertFile() {
		mapManager.revert();
	}

	@FXML
	private void exit() {
		requestClose();
	}

	@FXML
	private void undo() {
		mapManager.undo();
	}

	@FXML
	private void redo() {
		mapManager.redo();
	}

	@FXML
	private void editMapProperties() {
		mapManager.editMapProperties();
	}

	@FXML
	private void editDefinitions() {
		mapManager.editDefinitions();
	}

	@FXML
	private void toggleGrid() {
		eventBus.post(new ShowGridEvent(showGridButton.isSelected()));
	}

	@FXML
	private void toggleGroups() {
		eventBus.post(new ShowGroupsEvent(showGroupsButton.isSelected()));
	}

	@FXML
	private void showPlaceGroupHelp() {
		//TODO
		/*
		* Place items from item group in the rectangle (x1,y1) - (x2,y2). Several items may be spawned
		* on different places. Several items may spawn at once (at one place) when the item group says
		* so (uses @ref item_group::items_from which may return several items at once).
		* @param chance Chance for more items. A chance of 100 creates 1 item all the time, otherwise
		* it's the chance that more items will be created (place items until the random roll with that
		* chance fails). The chance is used for the first item as well, so it may not spawn an item at
		* all. Values <= 0 or > 100 are invalid.
		 */
	}

	public void setPrimaryStage(final Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void requestClose() {
		ApplicationSettings.getInstance().save();
		//TODO Create modal dialogue class to ask for save on exit
		Platform.exit();
	}

}
