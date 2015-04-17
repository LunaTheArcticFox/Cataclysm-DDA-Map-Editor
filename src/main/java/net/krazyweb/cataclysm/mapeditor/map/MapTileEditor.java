package net.krazyweb.cataclysm.mapeditor.map;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.FurnitureMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TerrainMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers.MappingController;
import net.krazyweb.util.FXMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MapTileEditor {

	private static final Logger log = LogManager.getLogger(MapTileEditor.class);

	public enum CloseAction {
		SAVE, WITHOUT_SAVE
	}

	@FXML
	private VBox boxes;

	@FXML
	private Button addMappingButton;

	private Map<String, HBox> headers = new HashMap<>();

	private MapTile originalMapTile;
	private MapTile mapTile;

	private CloseAction closeAction = CloseAction.WITHOUT_SAVE;

	public void setMapTile(final MapTile mapTile) {

		originalMapTile = mapTile;
		this.mapTile = mapTile.copy();

		this.mapTile.tileMappings.forEach(tileMapping -> {

			String type = "";

			if (tileMapping instanceof TerrainMapping) {
				type = "Terrain";
			} else if (tileMapping instanceof FurnitureMapping) {
				type = "Furniture";
			}

			addEntry(type, tileMapping);

		});

	}

	private void addEntry(final String type, final TileMapping mapping) {

		if (!headers.containsKey(type)) {
			addHeader(type);
		}

		FXMLHelper.loadFXML("/fxml/mapTileEditor/mappingControllers/" + type.toLowerCase() + ".fxml").ifPresent(loader -> {
			loader.<MappingController>getController().setMapping(mapping);
			boxes.getChildren().addAll(loader.getRoot(), new Separator());
		});

	}

	private void addHeader(final String title) {
		HBox hBox = new HBox(new Label(title));
		hBox.setAlignment(Pos.CENTER_LEFT);
		headers.put(title, hBox);
		boxes.getChildren().add(hBox);
	}

	public CloseAction getCloseAction() {
		return closeAction;
	}

	@FXML
	private void showAddMappingMenu() {

		ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().add(new MenuItem("Hello!"));
		contextMenu.show(addMappingButton, Side.BOTTOM, 0, 0);

	}

	@FXML
	private void saveAndClose() {

		closeAction = CloseAction.SAVE;

		originalMapTile.clear();
		mapTile.tileMappings.forEach(originalMapTile::add);

		close();

	}

	@FXML
	private void close() {
		((Stage) boxes.getScene().getWindow()).close();
	}

}
