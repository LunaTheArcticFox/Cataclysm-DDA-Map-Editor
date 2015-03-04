package net.krazyweb.cataclysm.mapeditor.map;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Modality;
import net.krazyweb.cataclysm.mapeditor.map.data.OverMapEntry;
import net.krazyweb.cataclysm.mapeditor.map.data.utils.PropertySheetItemCreator;
import org.controlsfx.control.PropertySheet;

import java.util.List;

public class DefinitionsEditor {

	private List<OverMapEntry> overMapEntries;

	private Dialog<Boolean> definitionsDialog;

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

		TreeItem<String> itemGroups = new TreeItem<>("Item Groups");
		itemGroups.setExpanded(true);
		treeRoot.getChildren().add(itemGroups);

		TreeItem<String> monsterGroups = new TreeItem<>("Monster Groups");
		monsterGroups.setExpanded(true);
		treeRoot.getChildren().add(monsterGroups);

		TreeItem<String> overMaps = new TreeItem<>("Overmaps");
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

		public TreeCell() {
			super();
			this.setEditable(false);
			contextMenu = new ContextMenu();
			MenuItem testItem = new MenuItem("Add...");
			contextMenu.getItems().add(testItem);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);
			setContextMenu(contextMenu);
		}

	}

	public void showAndWait() {
		definitionsDialog.showAndWait();
	}

}
