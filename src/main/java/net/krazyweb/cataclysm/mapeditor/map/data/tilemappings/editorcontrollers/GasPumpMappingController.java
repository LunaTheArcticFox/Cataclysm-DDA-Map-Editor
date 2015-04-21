package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.GasPumpMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GasPumpMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(GasPumpMappingController.class);

	@FXML
	private TextField minAmount, maxAmount;

	private GasPumpMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof GasPumpMapping) {

			this.mapping = (GasPumpMapping) mapping;

			minAmount.setText(this.mapping.minAmount + "");
			maxAmount.setText(this.mapping.maxAmount + "");

			minAmount.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.minAmount = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for minAmount: " + newValue, e);
				}
			});

			maxAmount.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					this.mapping.maxAmount = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					log.error("Invalid number for maxAmount: " + newValue, e);
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type GasPumpMapping.");
		}
	}

}
