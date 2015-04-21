package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.ItemGroupMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ItemGroupMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(ItemGroupMappingController.class);

	@FXML
	private TextField item, chance;

	private ItemGroupMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof ItemGroupMapping) {

			this.mapping = (ItemGroupMapping) mapping;

			item.setText(this.mapping.item + "");
			chance.setText(this.mapping.chance + "");

			item.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.item = newValue;
			});

			chance.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.chance = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for chance: " + newValue, e);
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type ItemGroupMapping.");
		}
	}

}
