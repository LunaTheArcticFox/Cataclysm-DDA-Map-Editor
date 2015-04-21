package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.FieldMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FieldMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(FieldMappingController.class);

	@FXML
	private TextField field, age, density;

	private FieldMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof FieldMapping) {

			this.mapping = (FieldMapping) mapping;

			field.setText(this.mapping.field + "");
			age.setText(this.mapping.age + "");
			density.setText(this.mapping.density + "");

			field.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.field = newValue;
			});

			age.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.age = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for age: " + newValue, e);
				}
			});

			density.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.density = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for density: " + newValue, e);
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type FieldMapping.");
		}
	}

}
