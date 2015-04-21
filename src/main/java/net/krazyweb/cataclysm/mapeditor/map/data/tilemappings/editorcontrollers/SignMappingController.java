package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.SignMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;


public class SignMappingController extends MappingController {

	@FXML
	private TextField signage;

	private SignMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof SignMapping) {

			this.mapping = (SignMapping) mapping;
			signage.setText(this.mapping.signage);

			signage.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.signage = newValue;
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type SignMapping.");
		}
	}

}
