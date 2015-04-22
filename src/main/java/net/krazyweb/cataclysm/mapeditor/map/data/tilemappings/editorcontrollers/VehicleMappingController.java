package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.VehicleMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class VehicleMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(VehicleMappingController.class);

	@FXML
	private TextField vehicle, chance, fuel, status;

	private VehicleMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof VehicleMapping) {

			this.mapping = (VehicleMapping) mapping;

			vehicle.setText(this.mapping.vehicle);
			this.mapping.chance.ifPresent(value -> chance.setText(value.toString()));
			this.mapping.fuel.ifPresent(value -> fuel.setText(value.toString()));
			this.mapping.status.ifPresent(value -> status.setText(value.toString()));

			vehicle.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.vehicle = newValue;
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

			fuel.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.fuel = Optional.empty();
				} else {
					try {
						this.mapping.fuel = Optional.of(Integer.parseInt(newValue));
					} catch (NumberFormatException e) {
						log.error("Invalid number for fuel: " + newValue, e);
					}
				}
			});

			status.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.status = Optional.empty();
				} else {
					try {
						this.mapping.status = Optional.of(Integer.parseInt(newValue));
					} catch (NumberFormatException e) {
						log.error("Invalid number for status: " + newValue, e);
					}
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type VehicleMapping.");
		}
	}

}
