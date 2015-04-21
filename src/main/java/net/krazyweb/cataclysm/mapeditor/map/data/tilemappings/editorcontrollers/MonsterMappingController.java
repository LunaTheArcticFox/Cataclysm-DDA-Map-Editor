package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.MonsterMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MonsterMappingController extends MappingController {

	private static final Logger log = LogManager.getLogger(MonsterMappingController.class);

	@FXML
	private TextField monster, name;

	@FXML
	private ChoiceBox<Boolean> friendly;

	private MonsterMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof MonsterMapping) {

			this.mapping = (MonsterMapping) mapping;

			monster.setText(this.mapping.monster + "");
			friendly.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));
			friendly.getSelectionModel().selectLast();
			name.setText(this.mapping.name + "");

			monster.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.monster = newValue;
			});

			friendly.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.friendly = newValue.getSelectedItem();
			});

			name.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.name = newValue;
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type MonsterMapping.");
		}
	}

}
