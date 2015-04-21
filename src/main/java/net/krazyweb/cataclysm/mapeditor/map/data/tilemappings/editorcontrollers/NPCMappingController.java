package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.NPCMapping;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.TileMapping;


public class NPCMappingController extends MappingController {

	@FXML
	private TextField npcClass;

	private NPCMapping mapping;

	@Override
	public void setMapping(final TileMapping mapping) {
		if (mapping instanceof NPCMapping) {

			this.mapping = (NPCMapping) mapping;
			npcClass.setText(this.mapping.npcClass);

			npcClass.textProperty().addListener((observable, oldValue, newValue) -> {
				this.mapping.npcClass = newValue;
			});

		} else {
			throw new IllegalArgumentException("TileMapping '" + mapping + "' should be of type NPCMapping.");
		}
	}

}
