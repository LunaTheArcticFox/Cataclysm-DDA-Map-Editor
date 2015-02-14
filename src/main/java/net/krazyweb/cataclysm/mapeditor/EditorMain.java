package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.tools.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class EditorMain {

	@FXML
	private BorderPane root;

	@FXML
	private ScrollPane mapPanel;

	@FXML
	private VBox tilePickerPanel, toolbarPanel;

	private EventBus eventBus = new EventBus();
	private Stage primaryStage;
	private CataclysmMap map;

	@FXML
	private void initialize() {

		Tool.setEventBus(eventBus);

		//-> Tile picker
		FXMLLoader tilePickerLoader = new FXMLLoader(getClass().getResource("/fxml/tilePicker.fxml"));
		try {
			tilePickerLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		eventBus.register(tilePickerLoader.getController());
		tilePickerLoader.<TilePicker>getController().setEventBus(eventBus);
		tilePickerPanel.getChildren().add(tilePickerLoader.<VBox>getRoot());

		new TileSet(Paths.get("Sample Data").resolve("tileset").resolve("tile_config.json"), eventBus);

		eventBus.register(this);
		eventBus.register(new MapLoader(eventBus));

		//-> Toolbars
		FXMLLoader mapToolbarLoader = new FXMLLoader(getClass().getResource("/fxml/mapToolbar.fxml"));
		try {
			mapToolbarLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapToolbarLoader.<MapToolbar>getController().setEventBus(eventBus);
		toolbarPanel.getChildren().add(0, mapToolbarLoader.<ScrollPane>getRoot());

		//Load each component in the main view and pass the model to them
		//-> Status bar
		FXMLLoader statusBarLoader = new FXMLLoader(getClass().getResource("/fxml/statusBar.fxml"));
		try {
			statusBarLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		eventBus.register(statusBarLoader.<MapDisplay>getController());
		statusBarLoader.<StatusBarController>getController().setEventBus(eventBus);
		root.setBottom(statusBarLoader.<AnchorPane>getRoot());

		//-> Canvasses
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mapCanvasses.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		eventBus.register(loader.<MapDisplay>getController());
		loader.<MapDisplay>getController().setEventBus(eventBus);
		loader.<MapDisplay>getController().setParent(mapPanel);

		Group group = new Group();
		group.getChildren().add(loader.<ScrollPane>getRoot());

		mapPanel.setContent(group);

		newFile();

		//Bind listeners for things such as hotkeys

	}

	@Subscribe
	public void mapSavedEventListener(final MapSavedEvent event) {
		refreshTitle();
	}

	@Subscribe
	public void mapLoadedEventListener(final MapLoadedEvent event) {
		map = event.getMap();
		refreshTitle();
	}

	@Subscribe
	public void mapChangedEventListener(final MapChangedEvent event) {
		refreshTitle();
	}

	private void refreshTitle() {

		String title = "Cataclysm Map Editor - ";

		if (map.getPath() != null) {
			title += map.getPath().getFileName();
		} else {
			title += "Untitled";
		}

		title += map.isSaved() ? "" : "*";

		primaryStage.setTitle(title);

	}

	@FXML
	private void newFile() {
		eventBus.post(new RequestLoadMapEvent(Paths.get("templates").resolve("default.json")));
	}

	@FXML
	private void openFile() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Pick Json File");
		fileChooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());

		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			eventBus.post(new RequestLoadMapEvent(selectedFile.toPath()));
		}

	}

	@FXML
	private void saveFile() {

		if (map.getPath() == null) {
			saveFileAs();
		} else {
			eventBus.post(new RequestSaveMapEvent(map.getPath()));
		}

	}

	@FXML
	private void saveFileAs() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		fileChooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());

		File selectedFile = fileChooser.showSaveDialog(null);
		if (selectedFile != null) {
			eventBus.post(new RequestSaveMapEvent(selectedFile.toPath()));
		}

	}

	@FXML
	private void revertFile() {
		eventBus.post(new RequestRevertMapEvent());
	}

	@FXML
	private void exit() {
		requestClose();
	}

	public void setPrimaryStage(final Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void requestClose() {
		//TODO Create modal dialogue class to ask for save on exit
		Platform.exit();
	}

}
