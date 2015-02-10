package net.krazyweb.cataclysm.mapeditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

	@Override
	public void start(final Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editorMain.fxml"));
			loader.load();
			loader.<EditorMain>getController().setPrimaryStage(primaryStage);
			Parent root = loader.getRoot();
			primaryStage.setTitle("Cataclysm Map Editor - Untitled*");
			primaryStage.setScene(new Scene(root, 1100, 900)); //TODO Fit to screen if need be and remember last size/position
			primaryStage.setResizable(true);
			//primaryStage.getIcons().add(new Image("/package/forge.png")); //TODO Icon
			primaryStage.setOnCloseRequest(event -> Platform.exit()); //TODO Save on exit prompts
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
