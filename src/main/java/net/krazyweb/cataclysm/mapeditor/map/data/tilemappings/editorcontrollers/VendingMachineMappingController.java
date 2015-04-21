package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.VendingMachineMapping;


public class VendingMachineMappingController extends MappingController {

	@FXML
	private TextField itemGroup;

	private VendingMachineMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof VendingMachineMapping) {

			this.mapping = (VendingMachineMapping) mapping;
			itemGroup.setText(this.mapping.itemGroup);

			itemGroup.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.itemGroup = newValue;
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type VendingMachineMapping.");
		}
	}

}
