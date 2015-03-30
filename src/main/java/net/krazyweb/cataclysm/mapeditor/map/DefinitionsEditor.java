package net.krazyweb.cataclysm.mapeditor.map;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Modality;
import net.krazyweb.cataclysm.mapeditor.map.data.OvermapEntry;
import net.krazyweb.cataclysm.mapeditor.map.data.utils.PropertySheetItemCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PropertySheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefinitionsEditor {

	private static final Logger log = LogManager.getLogger(DefinitionsEditor.class);

	private List<OvermapEntry> overmapEntries;

	private Dialog<List<OvermapEntry>> definitionsDialog;

	private TreeItem<String> itemGroups = new TreeItem<>("Item Groups");
	TreeItem<String> monsterGroups = new TreeItem<>("Monster Groups");
	TreeItem<String> overmaps = new TreeItem<>("Overmaps");

	public DefinitionsEditor(final List<OvermapEntry> overmapEntryList) {

		overmapEntries = new ArrayList<>();
		overmapEntryList.forEach(entry -> overmapEntries.add(new OvermapEntry(entry)));

		definitionsDialog = new Dialog<>();
		definitionsDialog.setTitle("Edit Definitions");
		definitionsDialog.initModality(Modality.APPLICATION_MODAL);
		definitionsDialog.setResizable(true);

		SplitPane parent = new SplitPane();
		parent.setDividerPosition(0, 0.3);
		parent.setPadding(new Insets(0));

		TreeItem<String> treeRoot = new TreeItem<>("");

		itemGroups.setExpanded(true);
		treeRoot.getChildren().add(itemGroups);

		monsterGroups.setExpanded(true);
		treeRoot.getChildren().add(monsterGroups);

		overmaps.setExpanded(true);
		treeRoot.getChildren().add(overmaps);

		overmapEntries.forEach(overmapEntry -> {
			TreeItem<String> overmap = new TreeItem<>(overmapEntry.name);
			overmaps.getChildren().add(overmap);
		});

		TreeView<String> treeView = new TreeView<>(treeRoot);
		treeView.setShowRoot(false);
		treeView.setEditable(true);
		treeView.setCellFactory(factory -> new TreeCell());
		treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.getParent() == overmaps) {
				PropertySheet propertySheet = new PropertySheet(PropertySheetItemCreator.getPropertySheetItems(overmapEntries.get(newValue.getParent().getChildren().indexOf(newValue))));
				propertySheet.setModeSwitcherVisible(false);
				propertySheet.setSearchBoxVisible(false);
				parent.getItems().remove(1);
				parent.getItems().add(propertySheet);
			}
		});

		parent.getItems().add(treeView);

		PropertySheet propertySheet = new PropertySheet();
		propertySheet.setModeSwitcherVisible(false);
		propertySheet.setSearchBoxVisible(false);
		parent.getItems().add(propertySheet);

		ButtonType saveButton = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		definitionsDialog.getDialogPane().getButtonTypes().setAll(saveButton, cancelButton);

		definitionsDialog.setOnCloseRequest(event -> definitionsDialog.close());

		definitionsDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButton) {
				return overmapEntries;
			}
			return null;
		});

		definitionsDialog.getDialogPane().setContent(parent);

	}

	private class TreeCell extends TextFieldTreeCell {

		private ContextMenu contextMenu;
		private MenuItem testItem = new MenuItem("Add...");

		public TreeCell() {
			super();
			contextMenu = new ContextMenu();
			contextMenu.getItems().add(testItem);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);
			setEditable(false);
			TreeItem<String> treeItem = getTreeItem();
			if (treeItem != null) {
				if (treeItem.equals(itemGroups)) {
					setContextMenu(contextMenu);
					testItem.setOnAction(event -> log.debug("Item Groups Add Clicked"));
				} else if (treeItem.equals(monsterGroups)) {
					setContextMenu(contextMenu);
					testItem.setOnAction(event -> log.debug("Monster Groups Add Clicked"));
				} else if (treeItem.equals(overmaps)) {
					setContextMenu(contextMenu);
					testItem.setOnAction(event -> log.debug("Overmaps Add Clicked"));
				} else {
					setEditable(true);
				}
			}
		}

	}

	public Optional<List<OvermapEntry>> showAndWait() {
		return definitionsDialog.showAndWait();
	}

}
