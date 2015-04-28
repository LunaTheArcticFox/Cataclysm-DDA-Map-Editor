package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.CataclysmDefinitions;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.ItemGroupMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.util.AutoCompletePopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class ItemGroupMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(ItemGroupMappingController.class);

	@FXML
	private TextField itemGroup, chance;

	private ItemGroupMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof ItemGroupMapping) {

			this.mapping = (ItemGroupMapping) mapping;

			itemGroup.setText(this.mapping.item + "");
			this.mapping.chance.ifPresent(value -> chance.setText(value.toString()));

			itemGroup.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.item = newValue;
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

			AutoCompletePopup.bind(itemGroup, CataclysmDefinitions.itemGroups);

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type ItemGroupMapping.");
		}
	}

}
