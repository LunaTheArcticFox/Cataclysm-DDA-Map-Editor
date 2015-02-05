package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.events.LoadMapEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class EditorMain extends Application {

	@FXML
	private BorderPane root;

	@FXML
	private ScrollPane mapPanel;

	@FXML
	private VBox tilePickerPanel, toolbarPanel;

	private EventBus eventBus = new EventBus();

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/editorMain.fxml"));
			primaryStage.setTitle("Cataclysm Map Editor");
			primaryStage.setScene(new Scene(root, 1600, 900));
			primaryStage.setResizable(true);
			//primaryStage.getIcons().add(new Image("/package/forge.png")); //TODO Icon
			primaryStage.setOnCloseRequest(event -> Platform.exit()); //TODO Save on exit prompts
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void initialize() {

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
		statusBarLoader.<StatusBar>getController().setEventBus(eventBus);
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
		mapPanel.setContent(loader.<ScrollPane>getRoot());

		newFile();

		//Bind listeners for things such as hotkeys

	}

	@FXML
	private void newFile() {
		eventBus.post(new LoadMapEvent(Paths.get("templates").resolve("default.json")));
	}

	@FXML
	private void openFile() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Pick Json File");
		fileChooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());

		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			eventBus.post(new LoadMapEvent(selectedFile.toPath()));
		}

	}

}
