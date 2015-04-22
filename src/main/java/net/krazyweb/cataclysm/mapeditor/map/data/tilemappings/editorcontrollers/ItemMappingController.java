package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.ItemMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class ItemMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(ItemMappingController.class);

	@FXML
	private TextField item, chance;

	private ItemMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof ItemMapping) {

			this.mapping = (ItemMapping) mapping;

			item.setText(this.mapping.item + "");
			chance.setText(this.mapping.chance + "");

			item.textProperty().addListener((observable, oldValue, newValue) -> {
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

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type ItemMapping.");
		}
	}

}
