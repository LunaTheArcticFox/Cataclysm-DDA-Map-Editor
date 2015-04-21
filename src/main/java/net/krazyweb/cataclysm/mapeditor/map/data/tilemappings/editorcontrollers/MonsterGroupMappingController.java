package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.MonsterGroupMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MonsterGroupMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(MonsterGroupMappingController.class);

	@FXML
	private TextField monster, density, chance;

	private MonsterGroupMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof MonsterGroupMapping) {

			this.mapping = (MonsterGroupMapping) mapping;

			monster.setText(this.mapping.monster + "");
			density.setText(this.mapping.density + "");
			chance.setText(this.mapping.chance + "");

			monster.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.monster = newValue;
			});

			density.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.density = Double.parseDouble(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for density: " + newValue, e);
				}
			});

			chance.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.chance = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for chance: " + newValue, e);
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type MonsterGroupMapping.");
		}
	}

}
