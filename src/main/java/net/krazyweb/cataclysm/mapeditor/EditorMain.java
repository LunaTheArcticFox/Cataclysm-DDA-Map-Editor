package net.krazyweb.cataclysm.mapeditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.tools.PencilTool;
import net.krazyweb.cataclysm.mapeditor.tools.Tool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class EditorMain extends Application {

	@FXML
	private Canvas canvas, canvasOverlay;

	@FXML
	private Button pencilButton, lineButton;

	private Tool tool = new PencilTool();

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/editorMain.fxml"));
		primaryStage.setTitle("Cataclysm Map Editor");
		primaryStage.setScene(new Scene(root, 1600, 900));
		primaryStage.setResizable(true);
		//primaryStage.getIcons().add(new Image("/package/forge.png")); //TODO Icon
		primaryStage.setOnCloseRequest(event -> Platform.exit()); //TODO Save on exit prompts
		primaryStage.show();
	}

	private int lastX = 0;
	private int lastY = 0;

	private void clearOverlay() {
		canvasOverlay.getGraphicsContext2D().clearRect((lastX / 32) * 32 - 5, (lastY / 32) * 32 - 5, 42, 42);
	}

	private void drawBox(final int mouseX, final int mouseY) {

		clearOverlay();

		lastX = (mouseX / 32) * 32;
		lastY = (mouseY / 32) * 32;

		canvasOverlay.getGraphicsContext2D().setStroke(Color.WHITE);
		canvasOverlay.getGraphicsContext2D().strokeRect(lastX, lastY, 32, 32);

	}

	private void drawBox(final double mouseX, final double mouseY) {
		drawBox((int) mouseX, (int) mouseY);
	}

	private static class Value<T> {
		T value;
	}

	private int convertMouseCoord(final int point) {
		return (point / 32) * 32;
	}

	private int convertMouseCoord(final double point) {
		return convertMouseCoord((int) point);
	}

	@FXML
	private void testMapgenDataFileReader() {

		new TileSet(Paths.get("Sample Data").resolve("tileset").resolve("tile_config.json"));

		Value<BufferedImage> texture = new Value<>();
		texture.value = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);

		try {
			texture.value = ImageIO.read(new File("Sample Data/tileset/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		canvasOverlay.setOnMouseMoved(event -> drawBox(event.getX(), event.getY()));
		canvasOverlay.setOnMouseExited(event -> clearOverlay());

		canvasOverlay.setOnMousePressed(event -> {
			tool.click(convertMouseCoord(event.getX()), convertMouseCoord(event.getY()), canvas);
		});

		canvasOverlay.setOnMouseDragged(event -> {
			drawBox(event.getX(), event.getY());
			//TODO currentTool.drag(x, y);
			tool.drag(convertMouseCoord(event.getX()), convertMouseCoord(event.getY()), canvas);
			/*GraphicsContext graphics2D = canvas.getGraphicsContext2D();
			graphics2D.setFill(Color.CHOCOLATE);
			graphics2D.fillRect(((int) event.getX() / 32) * 32, ((int) event.getY() / 32) * 32, 32, 32);
			System.out.println("Paint " + ((int) event.getX() / 32) * 32 + "," + ((int) event.getY() / 32) * 32);*/
		});

		MapgenDataFileReader reader = new MapgenDataFileReader(Paths.get("Sample Data").resolve("house05.json"));
		reader.start();

		reader.setOnSucceeded(event -> {

			MapgenMap map = reader.getMap();

			GraphicsContext graphics2D = canvas.getGraphicsContext2D();

			for (int x = 0; x < 24; x++) {
				for (int y = 0; y < 24; y++) {
					if (map.terrain[x][y] == 0) {
						graphics2D.setFill(Color.CHOCOLATE);
					} else {
						graphics2D.setFill(Color.CADETBLUE);
					}
					graphics2D.fillRect(x * 32, y * 32, 32, 32);
				}
			}

		});

	}

}
