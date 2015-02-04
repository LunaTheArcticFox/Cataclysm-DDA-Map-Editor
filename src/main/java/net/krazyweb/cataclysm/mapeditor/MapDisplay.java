package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.krazyweb.cataclysm.mapeditor.events.MapLoadedEvent;

public class MapDisplay {

	@FXML
	private StackPane root;

	@FXML
	private Canvas terrain, overlays;

	private int lastX, lastY;

	@FXML
	private void initialize() {
		root.setOnMouseMoved(event -> {
			drawBox(event.getX(), event.getY());
		});
	}

	private void clearOverlay() {
		overlays.getGraphicsContext2D().clearRect((lastX / 32) * 32 - 5, (lastY / 32) * 32 - 5, 42, 42);
	}

	private void drawBox(final int mouseX, final int mouseY) {

		clearOverlay();

		lastX = (mouseX / 32) * 32;
		lastY = (mouseY / 32) * 32;

		overlays.getGraphicsContext2D().setStroke(Color.WHITE);
		overlays.getGraphicsContext2D().strokeRect(lastX, lastY, 32, 32);

	}

	private void drawBox(final double mouseX, final double mouseY) {
		drawBox((int) mouseX, (int) mouseY);
	}

	@Subscribe
	public void mapLoadedEventListener(final MapLoadedEvent event) {

		MapgenMap map = event.getMap();

		GraphicsContext terrainGraphicsContext = terrain.getGraphicsContext2D();

		for (int x = 0; x < 24; x++) {
			for (int y = 0; y < 24; y++) {
				System.out.println(map.terrain[x][y]);
				terrainGraphicsContext.drawImage(TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).background), x * 32, y * 32);
				terrainGraphicsContext.drawImage(TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).foreground), x * 32, y * 32);
			}
		}

	}

}
