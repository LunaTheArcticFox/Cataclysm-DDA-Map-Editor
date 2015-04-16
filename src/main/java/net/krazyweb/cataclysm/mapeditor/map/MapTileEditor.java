package net.krazyweb.cataclysm.mapeditor.map;

import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers.TerrainMappingController;
import net.krazyweb.util.FXMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapTileEditor {

	private static final Logger log = LogManager.getLogger(MapTileEditor.class);

	@FXML
	private VBox boxes;

	private MapTile originalMapTile;
	private MapTile mapTile;

	public void setMapTile(final MapTile mapTile) {

		originalMapTile = mapTile;
		this.mapTile = mapTile.copy();

		this.mapTile.tileMappings.forEach(tileMapping -> {
			FXMLHelper.loadFXML("/fxml/mapTileEditor/mappingControllers/terrain.fxml").ifPresent(loader -> {
				loader.<TerrainMappingController>getController().setMapping(tileMapping);
				boxes.getChildren().addAll(loader.getRoot(), new Separator());
			});
		});

	}

	@FXML
	private void saveAndClose() {

		originalMapTile.clear();
		mapTile.tileMappings.forEach(originalMapTile::add);

		((Stage) boxes.getScene().getWindow()).close();

	}

}
