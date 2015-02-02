package net.krazyweb.cataclysm.mapeditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EditorMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/editorMain.fxml"));
        primaryStage.setTitle("Cataclysm Map Editor");
        primaryStage.setScene(new Scene(root, 750, 750));
        primaryStage.setResizable(true);
        //primaryStage.getIcons().add(new Image("/package/forge.png")); //TODO Icon
        primaryStage.setOnCloseRequest(event -> Platform.exit()); //TODO Save on exit prompts
        primaryStage.show();
    }

}
