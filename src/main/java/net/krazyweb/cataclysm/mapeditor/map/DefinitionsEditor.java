package net.krazyweb.cataclysm.mapeditor.map;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Modality;
import net.krazyweb.cataclysm.mapeditor.map.data.OverMapEntry;
import net.krazyweb.cataclysm.mapeditor.map.data.utils.PropertySheetItemCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PropertySheet;

import java.util.List;

public class DefinitionsEditor {

	private static final Logger log = LogManager.getLogger(DefinitionsEditor.class);

	private List<OverMapEntry> overMapEntries;

	private Dialog<Boolean> definitionsDialog;

	private TreeItem<String> itemGroups = new TreeItem<>("Item Groups");
	TreeItem<String> monsterGroups = new TreeItem<>("Monster Groups");
	TreeItem<String> overMaps = new TreeItem<>("Overmaps");

	public DefinitionsEditor(final List<OverMapEntry> overMapEntryList) {

		overMapEntries = overMapEntryList;

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

		overMaps.setExpanded(true);
		treeRoot.getChildren().add(overMaps);

		overMapEntries.forEach(overMapEntry -> {
			TreeItem<String> overMap = new TreeItem<>(overMapEntry.name);
			overMaps.getChildren().add(overMap);
		});

		TreeView<String> treeView = new TreeView<>(treeRoot);
		treeView.setShowRoot(false);
		treeView.setEditable(true);
		treeView.setCellFactory(factory -> new TreeCell());
		treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.getParent() == overMaps) {
				parent.getItems().remove(1);
				parent.getItems().add(new PropertySheet(PropertySheetItemCreator.getPropertySheetItems(overMapEntries.get(newValue.getParent().getChildren().indexOf(newValue)))));
			}
		});

		parent.getItems().add(treeView);
		parent.getItems().add(new PropertySheet());

		ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
		definitionsDialog.getDialogPane().getButtonTypes().setAll(closeButton);

		definitionsDialog.setOnCloseRequest(event -> definitionsDialog.close());

		definitionsDialog.getDialogPane().setContent(parent);

	}

	private class TreeCell extends TextFieldTreeCell {

		private ContextMenu contextMenu;
		private MenuItem testItem = new MenuItem("Add...");

		public TreeCell() {
			super();
			this.setEditable(false);
			contextMenu = new ContextMenu();
			contextMenu.getItems().add(testItem);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);
			if (getTreeItem() == null) {
				log.debug("Tree Item is Null");
			} else if (getTreeItem().equals(itemGroups)) {
				testItem.setOnAction(event -> log.debug("Item Groups Add Clicked"));
			} else if (getTreeItem().equals(monsterGroups)) {
				testItem.setOnAction(event -> log.debug("Monster Groups Add Clicked"));
			} else if (getTreeItem().equals(overMaps)) {
				testItem.setOnAction(event -> log.debug("Overmaps Add Clicked"));
			}
			setContextMenu(contextMenu);
		}

	}

	public void showAndWait() {
		definitionsDialog.showAndWait();
	}

}
