package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.MonsterMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


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

			monster.setText(this.mapping.monster);

			friendly.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE, null));
			friendly.getSelectionModel().select(this.mapping.friendly.orElse(null));

			this.mapping.name.ifPresent(name::setText);

			monster.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.monster = newValue;
			});

			friendly.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.friendly = Optional.ofNullable(newValue.getSelectedItem());
			});

			name.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.isEmpty()) {
					this.mapping.name = Optional.empty();
				} else {
					this.mapping.name = Optional.of(newValue);
				}
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type MonsterMapping.");
		}
	}

}
