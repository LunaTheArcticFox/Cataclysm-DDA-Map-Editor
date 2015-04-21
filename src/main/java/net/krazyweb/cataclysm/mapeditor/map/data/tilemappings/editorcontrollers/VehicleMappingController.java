package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.VehicleMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class VehicleMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(VehicleMappingController.class);

	@FXML
	private TextField vehicle, chance, fuel, status;

	private VehicleMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof VehicleMapping) {

			this.mapping = (VehicleMapping) mapping;

			vehicle.setText(this.mapping.vehicle + "");
			chance.setText(this.mapping.chance + "");
			fuel.setText(this.mapping.fuel + "");
			status.setText(this.mapping.status + "");

			vehicle.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.vehicle = newValue;
			});

			chance.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.chance = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for chance: " + newValue, e);
				}
			});

			fuel.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.fuel = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for fuel: " + newValue, e);
				}
			});

			status.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.status = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for status: " + newValue, e);
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type VehicleMapping.");
		}
	}

}
