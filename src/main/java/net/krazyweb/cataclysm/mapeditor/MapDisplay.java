package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
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

		try {

			MapgenMap map = event.getMap();

			GraphicsContext terrainGraphicsContext = terrain.getGraphicsContext2D();

			for (int x = 0; x < 24; x++) {
				for (int y = 0; y < 24; y++) {
					if (Tile.tiles.get(map.terrain[x][y]).isMultiTile()) {
						drawRotatedImage(terrainGraphicsContext, TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getBackground()), 90, x * 32, y * 32);
						drawRotatedImage(terrainGraphicsContext, TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getForeground()), 90, x * 32, y * 32);
					} else {
						terrainGraphicsContext.drawImage(TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getBackground()), x * 32, y * 32);
						terrainGraphicsContext.drawImage(TileSet.textures.get(Tile.tiles.get(map.terrain[x][y]).getForeground()), x * 32, y * 32);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void rotate(final GraphicsContext gc, final double angle, final double x, final double y) {
		Rotate r = new Rotate(angle, x, y);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	private void drawRotatedImage(final GraphicsContext graphicsContext, final Image image, final double angle, final int x, final int y) {
		if (image == null) {
			return;
		}
		graphicsContext.save(); // saves the current state on stack, including the current transform
		rotate(graphicsContext, angle, x + image.getWidth() / 2, y + image.getHeight() / 2);
		graphicsContext.drawImage(image, x, y);
		graphicsContext.restore(); // back to original state (before rotation)
	}

}
