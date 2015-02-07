package net.krazyweb.cataclysm.mapeditor.map;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.dialog.Wizard;

import java.util.Optional;

public class PlaceGroupInfoPanel {

	private Wizard wizard = new Wizard();

	//Bug with the Wizard right now prevents using its automatic settings collection. Must manually get
	//values from text fields.
	private TextField type = createTextField("type");
	private TextField group = createTextField("group");
	private TextField chance = createTextField("chance");

	public PlaceGroupInfoPanel(final String title) {

		wizard.setTitle(title);

		GridPane container = new GridPane();
		container.setVgap(5);

		container.add(new Label("Type"), 0, 0);
		container.add(type, 0, 1);
		container.add(new Label("Group"), 0, 2);
		container.add(group, 0, 3);
		container.add(new Label("Chance"), 0, 4);
		container.add(chance, 0, 5);

		Wizard.WizardPane infoPane = new Wizard.WizardPane();
		infoPane.setContent(container);

		wizard.setFlow(new Wizard.LinearFlow(infoPane));

	}

	public PlaceGroupInfoPanel(final String title, final PlaceGroup placeGroup) {
		this(title);
		type.setText(placeGroup.type);
		group.setText(placeGroup.group);
		chance.setText(String.valueOf(placeGroup.chance));
	}

	public Optional<ButtonType> showAndWait() {
		return wizard.showAndWait();
	}

	public String getType() {
		return type.getText();
	}

	public String getGroup() {
		return group.getText();
	}

	public int getChance() {
		return Integer.parseInt(chance.getText());
	}

	private TextField createTextField(final String id) {
		TextField textField = new TextField();
		textField.setId(id);
		return textField;
	}


}
