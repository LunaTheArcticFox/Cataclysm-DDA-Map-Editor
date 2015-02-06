package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroup;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;
import org.controlsfx.dialog.Wizard;

public class CreatePlaceGroupTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void dragStart(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		if (mouseButton == MouseButton.PRIMARY) {
			startX = x;
			startY = y;
		}
	}

	@Override
	public void dragEnd(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

		if (mouseButton != MouseButton.PRIMARY) {
			return;
		}

		//TODO Move to own class to call from anywhere, allow population of existing settings for editing PlaceGroups
		Wizard wizard = new Wizard();
		wizard.setTitle("Create New PlaceGroup");

		GridPane container = new GridPane();
		container.setVgap(5);

		TextField type = createTextField("type");
		TextField group = createTextField("group");
		TextField chance = createTextField("chance");

		container.add(new Label("Type"), 0, 0);
		container.add(type, 0, 1);
		container.add(new Label("Group"), 0, 2);
		container.add(group, 0, 3);
		container.add(new Label("Chance"), 0, 4);
		container.add(chance, 0, 5);

		Wizard.WizardPane infoPane = new Wizard.WizardPane();
		infoPane.setContent(container);

		wizard.setFlow(new Wizard.LinearFlow(infoPane));

		wizard.showAndWait().ifPresent(result -> {

			if (result == ButtonType.FINISH) {

				//Bug with the Wizard right now prevents using its automatic settings collection. Must manually get
				//values from text fields.

				PlaceGroup itemGroup = new PlaceGroup();
				itemGroup.type = type.getText();
				itemGroup.group = group.getText();
				itemGroup.chance = Integer.parseInt(chance.getText());

				int width = Math.abs(x - startX) + 1;
				int height = Math.abs(y - startY) + 1;

				if (x < startX) {
					startX = x;
				}

				if (y < startY) {
					startY = y;
				}

				map.addPlaceGroupZone(new PlaceGroupZone(startX, startY, width, height, itemGroup));

			}

		});

	}

	private TextField createTextField(final String id) {
		TextField textField = new TextField();
		textField.setId(id);
		return textField;
	}

}
