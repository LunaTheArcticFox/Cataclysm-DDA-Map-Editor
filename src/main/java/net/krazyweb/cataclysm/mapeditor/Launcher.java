package net.krazyweb.cataclysm.mapeditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher extends Application {

	private static Logger log = LogManager.getLogger(Launcher.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		log.info("Starting Application");
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editorMain.fxml"));
			Parent root = loader.load();
			primaryStage.setTitle("Cataclysm Map Editor - Untitled*");
			primaryStage.setScene(new Scene(root, 1100, 900)); //TODO Fit to screen if need be and remember last size/position
			primaryStage.setResizable(true);
			//primaryStage.getIcons().add(new Image("/package/forge.png")); //TODO Icon
			primaryStage.setOnCloseRequest(event -> loader.<EditorMain>getController().requestClose()); //TODO Save on exit prompts
			loader.<EditorMain>getController().onInitialized(primaryStage);
			primaryStage.show();
		} catch (Exception e) {
			log.error("Error while starting or running application:", e);
		}
	}

}
