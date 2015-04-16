package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.FurnitureMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;


public class FurnitureMappingController extends MappingController {

	@FXML
	private TextField furnitureType;

	private FurnitureMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof FurnitureMapping) {

			this.mapping = (FurnitureMapping) mapping;
			furnitureType.setText(this.mapping.furniture);

			furnitureType.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.furniture = newValue;
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type FurnitureMapping.");
		}
	}

}
