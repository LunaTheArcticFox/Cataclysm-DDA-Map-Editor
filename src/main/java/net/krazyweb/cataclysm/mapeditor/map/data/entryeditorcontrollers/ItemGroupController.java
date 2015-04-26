package net.krazyweb.cataclysm.mapeditor.map.data.entryeditorcontrollers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import net.krazyweb.cataclysm.mapeditor.map.data.ItemGroupEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class ItemGroupController {

	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(ItemGroupController.class);

	@FXML
	private GridPane root;

	@FXML
	private TextField groupNameTextField;

	private ObservableList<ItemGroupEntry> itemGroupEntries;
	private ItemGroupEntry itemGroupEntry;

	private List<Node> addedRows = new ArrayList<>();

	public void setList(final ObservableList<ItemGroupEntry> itemGroupEntries) {
		this.itemGroupEntries = itemGroupEntries;
	}

	public void setItemGroup(final ItemGroupEntry itemGroup) {

		itemGroupEntry = itemGroup;
		groupNameTextField.setText(itemGroupEntry.id);
		groupNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			int i = itemGroupEntries.indexOf(itemGroupEntry);
			itemGroupEntry.id = newValue;
			itemGroupEntries.set(i, itemGroupEntry);
		});

		updateItemGroupRows();

	}

	private void updateItemGroupRows() {

		root.getChildren().removeAll(addedRows);
		int i = 3;
		for (ItemGroupEntry.ItemSpawn itemSpawn : itemGroupEntry.itemSpawns) {

			TextField groupField = new TextField(itemSpawn.name);
			groupField.setPrefWidth(200);
			groupField.textProperty().addListener((observable, oldValue, newValue) -> {
				itemSpawn.name = newValue;
				//TODO Input validation
			});

			TextField chanceField = new TextField(String.valueOf(itemSpawn.chance));
			chanceField.setPrefWidth(45);
			chanceField.textProperty().addListener((observable, oldValue, newValue) -> {
				itemSpawn.chance = Integer.parseInt(newValue);
				//TODO Input validation
			});

			Button deleteButton = new Button("X");
			deleteButton.setOnAction(event -> {
				itemGroupEntry.itemSpawns.remove(itemSpawn);
				updateItemGroupRows();
			});

			addedRows.add(groupField);
			addedRows.add(chanceField);
			addedRows.add(deleteButton);

			root.add(groupField, 0, i);
			root.add(chanceField, 1, i);
			root.add(deleteButton, 2, i++);

		}

		Button addButton = new Button("+ Add Item");
		addButton.setOnAction(event -> {
			itemGroupEntry.itemSpawns.add(new ItemGroupEntry.ItemSpawn("item_" + (int) (Math.random() * 1000), 50));
			updateItemGroupRows();
		});
		addedRows.add(addButton);

		root.add(addButton, 0, i + 2, 3, 1);

	}

}
