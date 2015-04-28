package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.CataclysmDefinitions;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.VendingMachineMapping;
import net.krazyweb.util.AutoCompletePopup;

import java.util.Optional;


public class VendingMachineMappingController extends MappingController {

	@FXML
	private TextField itemGroup;

	private VendingMachineMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof VendingMachineMapping) {

			this.mapping = (VendingMachineMapping) mapping;
			this.mapping.itemGroup.ifPresent(itemGroup::setText);

			itemGroup.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.itemGroup = Optional.of(newValue);
			});

			AutoCompletePopup.bind(itemGroup, CataclysmDefinitions.itemGroups);

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type VendingMachineMapping.");
		}
	}

}
