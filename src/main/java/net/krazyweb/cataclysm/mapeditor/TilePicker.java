package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilePickedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TilePicker {

	@FXML
	private TilePane tileContainer;

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilesetLoadedListener(final TilesetLoadedEvent event) {

		tileContainer.getChildren().clear();

		try {
			loadTiles(Paths.get("Sample Data").resolve("terrain.json"));
			loadTiles(Paths.get("Sample Data").resolve("furniture.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void loadTiles(final Path path) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = mapper.readTree(path.toFile());

		root.forEach(node -> {
			try {

				Tile tile = Tile.tiles.get(node.get("id").asText());

				if (tile == null) {
					return;
				}

				ImageView view = new ImageView(TileSet.textures.get(tile.getTile().getID()));
				view.setPickOnBounds(true);
				view.setOnMousePressed(mouseEvent -> {
					eventBus.post(new TilePickedEvent(tile));
				});
				view.setOnMouseMoved(mouseEvent -> {
					eventBus.post(new TileHoverEvent(node.get("name").asText(), 0, 0));
				});
				tileContainer.getChildren().add(view);

			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

}
