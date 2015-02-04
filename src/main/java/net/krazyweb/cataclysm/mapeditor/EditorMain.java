package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.events.LoadMapEvent;

import java.io.IOException;
import java.nio.file.Paths;

public class EditorMain extends Application {

	@FXML
	private ScrollPane mapPanel;

	private EventBus eventBus = new EventBus();

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/newEditorMain.fxml"));
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

		new TileSet(Paths.get("Sample Data").resolve("tileset").resolve("tile_config.json"));

		eventBus.register(new MapLoader(eventBus));

		//Load the model

		//Load each component in the main view and pass the model to them
		//-> Status bar
		//-> Canvasses
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mapCanvasses.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		eventBus.register(loader.<MapDisplay>getController());
		mapPanel.setContent(loader.<ScrollPane>getRoot());
		//-> Toolbars
		//-> Tile picker

		eventBus.post(new LoadMapEvent(Paths.get("Sample Data").resolve("house05.json")));

		//Bind listeners for things such as hotkeys

	}

}
