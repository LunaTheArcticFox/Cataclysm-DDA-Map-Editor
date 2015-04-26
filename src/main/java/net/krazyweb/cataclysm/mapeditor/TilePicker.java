package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.events.*;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTileEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.FurnitureMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TerrainMapping;
import net.krazyweb.util.CloseAction;
import net.krazyweb.util.FXMLHelper;
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

		view.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {

				if (tilePane == defaultTileContainer) {

					MenuItem cloneMenuItem = new MenuItem("Clone to Map Tile");
					cloneMenuItem.setOnAction(event1 -> load(mapTile.copy(), mapTileContainer));

					MenuItem cancelMenuItem = new MenuItem("Cancel");

					ContextMenu menu = new ContextMenu(cloneMenuItem, new SeparatorMenuItem(), cancelMenuItem);
					menu.setAutoHide(true);
					menu.setHideOnEscape(true);
					menu.show(view, event.getScreenX(), event.getScreenY());

					cancelMenuItem.setOnAction(event1 -> menu.hide());

				} else {

					//TODO Delete option, removes all tiles on map as well

					MenuItem editMenuItem = new MenuItem("Edit");
					editMenuItem.setOnAction(event1 -> load(mapTile.copy(), mapTileContainer));

					MenuItem cloneMenuItem = new MenuItem("Clone");
					cloneMenuItem.setOnAction(event1 -> load(mapTile.copy(), mapTileContainer));

					MenuItem cancelMenuItem = new MenuItem("Cancel");

					ContextMenu menu = new ContextMenu(editMenuItem, cloneMenuItem, new SeparatorMenuItem(), cancelMenuItem);
					menu.setAutoHide(true);
					menu.setHideOnEscape(true);
					menu.show(view, event.getScreenX(), event.getScreenY());

					cancelMenuItem.setOnAction(event1 -> menu.hide());

					editMenuItem.setOnAction(event1 -> {
						//TODO straight to this for middle click
						FXMLLoader loader = FXMLHelper.loadFXML("/fxml/mapTileEditor/editorDialog.fxml").orElseThrow(RuntimeException::new);

						loader.<MapTileEditor>getController().setMapTile(mapTile);

						Stage stage = new Stage();
						stage.setScene(new Scene(loader.getRoot()));
						stage.setTitle("Tile Editor");
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.showAndWait();

						if (loader.<MapTileEditor>getController().getCloseAction() == CloseAction.SAVE) {
							tooltip.setText(mapTile.tileMappings.toString());
							view.setImage(mapTile.getTexture(0, 0));
							eventBus.post(new TileMappingChangedEvent());
						}
					});

				}

			}
		});

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
