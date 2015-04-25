package net.krazyweb.cataclysm.mapeditor.map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.krazyweb.cataclysm.mapeditor.map.data.ItemGroupEntry;
import net.krazyweb.cataclysm.mapeditor.map.data.MonsterGroupEntry;
import net.krazyweb.cataclysm.mapeditor.map.data.OvermapEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DefinitionsEditor {

	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(DefinitionsEditor.class);

	@FXML
	private VBox contextHelper;

	@FXML
	private TitledPane itemGroupPane, monsterGroupPane, overmapPane;

	@FXML
	private ListView<ItemGroupEntry> itemGroupListView;

	@FXML
	private ListView<MonsterGroupEntry> monsterGroupListView;

	@FXML
	private ListView<OvermapEntry> overmapListView;

	private ObservableList<ItemGroupEntry> itemGroupEntries = FXCollections.observableArrayList();
	private ObservableList<MonsterGroupEntry> monsterGroupEntries = FXCollections.observableArrayList();
	private ObservableList<OvermapEntry> overmapEntries = FXCollections.observableArrayList();

	@FXML
	private void initialize() {

		itemGroupListView.setItems(itemGroupEntries);
		itemGroupListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		itemGroupListView.setCellFactory(callback -> new ListCell<ItemGroupEntry>() {
			@Override
			protected void updateItem(final ItemGroupEntry itemGroupEntry, final boolean empty) {
				super.updateItem(itemGroupEntry, empty);
				if (itemGroupEntry != null) {
					setText(itemGroupEntry.id);
				}
			}
		});

		monsterGroupListView.setItems(monsterGroupEntries);
		monsterGroupListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		monsterGroupListView.setCellFactory(callback -> new ListCell<MonsterGroupEntry>() {
			@Override
			protected void updateItem(final MonsterGroupEntry monsterGroupEntry, final boolean empty) {
				super.updateItem(monsterGroupEntry, empty);
				if (monsterGroupEntry != null) {
					setText(monsterGroupEntry.name);
				}
			}
		});

		overmapListView.setItems(overmapEntries);
		overmapListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		overmapListView.setCellFactory(callback -> new ListCell<OvermapEntry>() {
			@Override
			protected void updateItem(final OvermapEntry overmapEntry, final boolean empty) {
				super.updateItem(overmapEntry, empty);
				if (overmapEntry != null) {
					setText(overmapEntry.name);
				}
			}
		});

	}

	public void setItemGroupEntries(final List<ItemGroupEntry> entries) {
		entries.forEach(entry -> itemGroupEntries.add(new ItemGroupEntry(entry)));
	}

	public void setMonsterGroupEntries(final List<MonsterGroupEntry> entries) {
		entries.forEach(entry -> monsterGroupEntries.add(new MonsterGroupEntry(entry)));
	}

	public void setOvermapEntries(final List<OvermapEntry> entries) {
		entries.forEach(entry -> overmapEntries.add(new OvermapEntry(entry)));
	}

	@FXML
	public void showItemGroupContextMenu(final MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {

			MenuItem addMenuItem = new MenuItem("Add New Item Group");
			addMenuItem.setOnAction(event1 -> itemGroupEntries.add(new ItemGroupEntry())); //TODO Ask for name, check if exists

			MenuItem cancelMenuItem = new MenuItem("Cancel");

			ContextMenu menu = new ContextMenu(addMenuItem, new SeparatorMenuItem(), cancelMenuItem);
			menu.setAutoHide(true);
			menu.setHideOnEscape(true);

			cancelMenuItem.setOnAction(event1 -> menu.hide());

			menu.show(contextHelper, event.getScreenX(), event.getScreenY());

		}
	}

	@FXML
	public void showMonsterGroupContextMenu(final MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {

			MenuItem addMenuItem = new MenuItem("Add New Monster Group");
			addMenuItem.setOnAction(event1 -> monsterGroupEntries.add(new MonsterGroupEntry())); //TODO Ask for name, check if exists

			MenuItem cancelMenuItem = new MenuItem("Cancel");

			ContextMenu menu = new ContextMenu(addMenuItem, new SeparatorMenuItem(), cancelMenuItem);
			menu.setAutoHide(true);
			menu.setHideOnEscape(true);

			cancelMenuItem.setOnAction(event1 -> menu.hide());

			menu.show(contextHelper, event.getScreenX(), event.getScreenY());

		}
	}

	@FXML
	public void showOvermapContextMenu(final MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {

			MenuItem addMenuItem = new MenuItem("Add New Overmap");
			addMenuItem.setOnAction(event1 -> overmapEntries.add(new OvermapEntry())); //TODO Ask for name, check if exists

			MenuItem cancelMenuItem = new MenuItem("Cancel");

			ContextMenu menu = new ContextMenu(addMenuItem, new SeparatorMenuItem(), cancelMenuItem);
			menu.setAutoHide(true);
			menu.setHideOnEscape(true);

			cancelMenuItem.setOnAction(event1 -> menu.hide());

			menu.show(contextHelper, event.getScreenX(), event.getScreenY());

		}
	}

}
