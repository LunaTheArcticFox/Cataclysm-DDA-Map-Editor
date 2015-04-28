package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.CataclysmDefinitions;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.MonsterGroupMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.util.AutoCompletePopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class MonsterGroupMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(MonsterGroupMappingController.class);

	@FXML
	private TextField monsterGroup, density, chance;

	private MonsterGroupMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof MonsterGroupMapping) {

			this.mapping = (MonsterGroupMapping) mapping;

			monsterGroup.setText(this.mapping.monster);

			this.mapping.density.ifPresent(value -> density.setText(value.toString()));
			this.mapping.chance.ifPresent(value -> chance.setText(value.toString()));

			monsterGroup.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.monster = newValue;
			});

			density.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.chance = Optional.empty();
				} else {
					try {
						this.mapping.density = Optional.of(Double.parseDouble(newValue));
					} catch (NumberFormatException e) {
						log.error("Invalid number for density: " + newValue, e);
					}
				}
			});

			chance.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.chance = Optional.empty();
				} else {
					try {
						this.mapping.chance = Optional.of(Integer.parseInt(newValue));
					} catch (NumberFormatException e) {
						log.error("Invalid number for chance: " + newValue, e);
					}
				}
			});

			AutoCompletePopup.bind(monsterGroup,  CataclysmDefinitions.monsterGroups);

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type MonsterGroupMapping.");
		}
	}

}
