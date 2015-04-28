package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.CataclysmDefinitions;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.FieldMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.util.AutoCompletePopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class FieldMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(FieldMappingController.class);

	@FXML
	private TextField field, age, density;

	private FieldMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof FieldMapping) {

			this.mapping = (FieldMapping) mapping;

			field.setText(this.mapping.field);

			this.mapping.age.ifPresent(value -> age.setText(value.toString()));
			this.mapping.density.ifPresent(value -> density.setText(value.toString()));

			field.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.field = newValue;
			});

			age.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.age = Optional.empty();
				} else {
					try {
						this.mapping.age = Optional.of(Integer.parseInt(newValue));
					} catch (NumberFormatException e) {
						log.error("Invalid number for age: " + newValue, e);
					}
				}
			});

			density.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.density = Optional.empty();
				} else {
					try {
						this.mapping.density = Optional.of(Integer.parseInt(newValue));
					} catch (NumberFormatException e) {
						log.error("Invalid number for density: " + newValue, e);
					}
				}
			});

			AutoCompletePopup.bind(field, CataclysmDefinitions.fields);

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type FieldMapping.");
		}
	}

}
