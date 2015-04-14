package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.TilePane;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TilePicker {

	private static Logger log = LogManager.getLogger(TilePicker.class);

	@FXML
	private TilePane userTileContainer, mapTileContainer, defaultTileContainer;

	@FXML
	private ScrollPane tilePaneContainer;

	private EventBus eventBus;
	private TileSet tileSet;

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
		tileSet = event.getTileSet();
		defaultTileContainer.getChildren().clear();
		loadTiles();
	}

	private void loadTiles() {

		//TODO Properly handle missing tile textures (Load tiles and tileset differently)
		/*if (TileConfiguration.get(node.get("id").asText()) == null) {
			TileConfiguration.tiles.put(node.get("id").asText(), new TileConfiguration(node.get("id").asText()));
			TileSet.textures.put(node.get("id").asText(), new BufferedImage(TileSet.tileSize, TileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR));
		}

		ImageView view = new ImageView(mapTile.getTexture(0, 0));
		view.setPickOnBounds(true);
		view.setOnMousePressed(mouseEvent -> eventBus.post(new TilePickedEvent(mapTile)));
		view.setOnMouseMoved(mouseEvent -> eventBus.post(new TileHoverEvent(mapTile, 0, 0)));

		//TODO Create own Tooltip class with finer control over positioning and timing
		Tooltip tooltip = new Tooltip(mapTile.tileMappings.toString());
		Tooltip.install(view, tooltip);

		defaultTileContainer.getChildren().add(view);*/

	}

}
