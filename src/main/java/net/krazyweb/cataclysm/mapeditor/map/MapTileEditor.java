package net.krazyweb.cataclysm.mapeditor.map;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.*;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.editorcontrollers.MappingController;
import net.krazyweb.util.FXMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapTileEditor {

	private static final Logger log = LogManager.getLogger(MapTileEditor.class);

	public enum CloseAction {
		SAVE, WITHOUT_SAVE
	}

	@FXML
	private VBox boxes;

	@FXML
	private Button addMappingButton;

	private Map<String, HBox> headers = new HashMap<>();

	private MapTile originalMapTile;
	private MapTile mapTile;

	private CloseAction closeAction = CloseAction.WITHOUT_SAVE;

	public void setMapTile(final MapTile mapTile) {

		originalMapTile = mapTile;
		this.mapTile = mapTile.copy();

		this.mapTile.tileMappings.forEach(tileMapping -> addEntry(getType(tileMapping), tileMapping));

	}

	private void addEntry(final String type, final TileMapping mapping) {

		if (!headers.containsKey(type)) {
			addHeader(type);
		}

		FXMLHelper.loadFXML("/fxml/mapTileEditor/mappingControllers/" + type.toLowerCase() + ".fxml").ifPresent(loader -> {

			loader.<MappingController>getController().setMapping(mapping);

			Separator separator = new Separator();

			Button deleteButton = new Button("X");
			deleteButton.setOnAction(event -> {
				mapTile.tileMappings.remove(mapping);
				boxes.getChildren().removeAll(loader.<HBox>getRoot(), separator);
				removeUnusedHeaders();
			});

			loader.<HBox>getRoot().getChildren().add(deleteButton);

			int insertIndex = boxes.getChildren().indexOf(headers.get(type)) + 1;

			boxes.getChildren().add(insertIndex, loader.getRoot());
			boxes.getChildren().add(insertIndex + 1, separator);

		});

	}

	private void removeUnusedHeaders() {

		Set<String> toRemove = new HashSet<>(headers.keySet());

		mapTile.tileMappings.stream()
				.filter(tileMapping -> toRemove.contains(getType(tileMapping)))
				.forEach(tileMapping -> toRemove.remove(getType(tileMapping)));

		toRemove.forEach(header -> boxes.getChildren().remove(headers.remove(header)));

	}

	private String getType(final TileMapping tileMapping) {
		if (tileMapping instanceof TerrainMapping) {
			return "Terrain";
		} else if (tileMapping instanceof FurnitureMapping) {
			return "Furniture";
		} else if (tileMapping instanceof ToiletMapping) {
			return "Toilet";
		} else if (tileMapping instanceof GasPumpMapping) {
			return "GasPump";
		}
		return "";
	}

	private void addHeader(final String title) {
		HBox hBox = new HBox(new Label(title));
		hBox.setAlignment(Pos.CENTER_LEFT);
		headers.put(title, hBox);
		boxes.getChildren().add(hBox);
	}

	public CloseAction getCloseAction() {
		return closeAction;
	}

	@FXML
	private void showAddMappingMenu() {

		ContextMenu contextMenu = new ContextMenu();

		MenuItem addTerrainMenuItem = new MenuItem("Terrain");
		MenuItem addFurnitureMenuItem = new MenuItem("Furniture");
		MenuItem addToiletMenuItem = new MenuItem("Toilet");
		MenuItem addGasPumpMenuItem = new MenuItem("Gas Pump");

		addTerrainMenuItem.setOnAction(event -> {
			TerrainMapping mapping = new TerrainMapping("");
			mapTile.add(mapping);
			addEntry("Terrain", mapping);
		});

		addFurnitureMenuItem.setOnAction(event -> {
			FurnitureMapping mapping = new FurnitureMapping("");
			mapTile.add(mapping);
			addEntry("Furniture", mapping);
		});

		addToiletMenuItem.setOnAction(event -> {
			ToiletMapping mapping = new ToiletMapping();
			mapTile.add(mapping);
			addEntry("Toilet", mapping);
		});

		addGasPumpMenuItem.setOnAction(event -> {
			GasPumpMapping mapping = new GasPumpMapping();
			mapTile.add(mapping);
			addEntry("GasPump", mapping);
		});

		contextMenu.getItems().addAll(
				addTerrainMenuItem,
				addFurnitureMenuItem,
				addToiletMenuItem,
				addGasPumpMenuItem
		);

		contextMenu.show(addMappingButton, Side.BOTTOM, 0, 0);

	}

	@FXML
	private void saveAndClose() {

		closeAction = CloseAction.SAVE;

		originalMapTile.clear();
		mapTile.tileMappings.forEach(originalMapTile::add);

		close();

	}

	@FXML
	private void close() {
		((Stage) boxes.getScene().getWindow()).close();
	}

}
