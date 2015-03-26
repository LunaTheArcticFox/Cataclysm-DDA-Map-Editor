package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.TilePane;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilePickedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TilePicker {

	private static Logger log = LogManager.getLogger(TilePicker.class);

	@FXML
	private TilePane tileContainer;

	@FXML
	private ScrollPane tilePaneContainer;

	private EventBus eventBus;

	@FXML
	public void initialize() {
		tilePaneContainer.addEventFilter(ScrollEvent.SCROLL, event -> {
			double deltaY = event.getDeltaY() * 1.75;
			double height = tilePaneContainer.getContent().getBoundsInLocal().getWidth();
			double vValue = tilePaneContainer.getVvalue();
			tilePaneContainer.setVvalue(vValue + -deltaY / height);
		});
	}

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilesetLoadedListener(final TilesetLoadedEvent event) {

		tileContainer.getChildren().clear();

		Path gameFolderPath = ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.GAME_FOLDER);

		try {
			loadTiles(gameFolderPath.resolve(Paths.get("data", "json", "terrain.json")));
			loadTiles(gameFolderPath.resolve(Paths.get("data", "json", "terrain", "ags_terrain.json")));
			loadTiles(gameFolderPath.resolve(Paths.get("data", "json", "furniture.json")));
		} catch (IOException e) {
			log.error("Error while loading terrain and furniture definitions:", e);
		}

	}

	private void loadTiles(final Path path) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = mapper.readTree(path.toFile());

		root.forEach(node -> {

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
				eventBus.post(new TileHoverEvent(node.get("id").asText() + " (" + node.get("name").asText() + ")", 0, 0));
			});
			tileContainer.getChildren().add(view);

		});

	}

}
