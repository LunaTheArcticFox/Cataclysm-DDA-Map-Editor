package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.TilePane;
import net.krazyweb.cataclysm.mapeditor.events.FileLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilePickedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.FurnitureMapping;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TerrainMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class TilePicker {

	@SuppressWarnings("unused")
	private static Logger log = LogManager.getLogger(TilePicker.class);

	@FXML
	private TilePane userTileContainer, mapTileContainer, defaultTileContainer;

	@FXML
	private TitledPane userTilePanel, mapTilePanel, defaultTilePanel;

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
		loadTiles();
	}

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilesetLoadedEventListener(final TilesetLoadedEvent event) {
		defaultTileContainer.getChildren().clear();
		loadTiles();
	}

	@Subscribe
	public void fileLoadedEventListener(final FileLoadedEvent event) {

		//TODO Sort tiles by... something
		mapTileContainer.getChildren().clear();
		Set<MapTile> addedTiles = new HashSet<>();
		event.getMaps().forEach(map -> {
			for (int x = 0; x < MapEditor.SIZE; x++) {
				for (int y = 0; y < MapEditor.SIZE; y++) {
					if (map.tiles[x][y] != null && !addedTiles.contains(map.tiles[x][y])) {
						load(map.tiles[x][y], mapTileContainer);
						addedTiles.add(map.tiles[x][y]);
					}
				}
			}
		});

		if (mapTileContainer.getChildren().isEmpty()) {
			mapTilePanel.setExpanded(false);
		} else {
			mapTilePanel.setExpanded(true);
		}

	}

	private void load(final MapTile mapTile, final TilePane tilePane) {

		ImageView view = new ImageView(mapTile.getTexture(0, 0));
		view.setPickOnBounds(true);
		view.setOnMousePressed(mouseEvent -> eventBus.post(new TilePickedEvent(mapTile)));
		view.setOnMouseMoved(mouseEvent -> eventBus.post(new TileHoverEvent(mapTile, 0, 0)));

		//TODO Create own Tooltip class with finer control over positioning and timing
		Tooltip tooltip = new Tooltip(mapTile.tileMappings.toString());
		Tooltip.install(view, tooltip);

		//TODO Click handlers for map/user tiles

		tilePane.getChildren().add(view);

	}

	private void loadTiles() {

		//TODO Sort tiles by... something
		//TODO Add special tiles such as toilets and vending machines
		Tile.getAll().forEach(tile -> {

			MapTile mapTile = new MapTile();

			if (tile.id.startsWith("t_")) {
				mapTile.add(new TerrainMapping(tile.id));
			} else if (tile.id.startsWith("f_")){
				mapTile.add(new FurnitureMapping(tile.id));
			}

			load(mapTile, defaultTileContainer);

		});

	}

}
