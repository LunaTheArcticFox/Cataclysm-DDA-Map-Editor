package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import net.krazyweb.cataclysm.mapeditor.ApplicationSettings;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupInfoPanel;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;
import net.krazyweb.util.Rectangle;

import java.util.HashSet;
import java.util.Set;

public class EditPlaceGroupTool extends Tool {

	private int lastX;
	private int lastY;

	private Rectangle originalBounds;

	private PlaceGroupZone placeGroupZone;

	@Override
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {

		if (!ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GROUPS).get()) {
			return;
		}

		int x = convertCoord(event.getX());
		int y = convertCoord(event.getY());

		if (event.getButton() == MouseButton.PRIMARY) {
			PlaceGroupZone zone = map.getPlaceGroupZoneAt(x, y);
			if (zone != null) {
				map.removePlaceGroupZone(zone);
				if (zone == placeGroupZone) {
					map.addPlaceGroupZone(zone);
					placeGroupZone = map.getPlaceGroupZoneAt(x, y);
				} else {
					map.addPlaceGroupZone(0, zone);
					placeGroupZone = zone;
				}
			}
		} else {

			if (map.getPlaceGroupZoneAt(x, y) != null) {

				MenuItem edit = new MenuItem("Edit");
				MenuItem delete = new MenuItem("Delete");
				MenuItem cancel = new MenuItem("Cancel");

				ContextMenu menu = new ContextMenu(edit, delete, new SeparatorMenuItem(), cancel);
				menu.setAutoHide(true);

				edit.setOnAction(action -> editPlaceGroupZone(map.getPlaceGroupZoneAt(x, y), map));
				delete.setOnAction(action -> {
					map.startEdit();
					map.removePlaceGroupZone(placeGroupZone);
					map.finishEdit("Delete PlaceGroup");
				});
				cancel.setOnAction(action -> menu.hide());

				menu.show(rootNode, event.getScreenX(), event.getScreenY());

			}

		}

	}

	@Override
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {

		lastX = convertCoord(event.getX());
		lastY = convertCoord(event.getY());

		if (event.getButton() != MouseButton.PRIMARY || map.getPlaceGroupZonesAt(lastX, lastY).isEmpty() ||
				!ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GROUPS).get()) {
			return;
		}

		placeGroupZone = map.getPlaceGroupZoneAt(lastX, lastY);
		originalBounds = new Rectangle(placeGroupZone.bounds);
		if (placeGroupZone != null) {
			map.startEdit();
		}

	}

	@Override
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {

		if (event.getButton() != MouseButton.PRIMARY ||
				!ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GROUPS).get()) {
			return;
		}

		if (placeGroupZone != null) {

			int deltaX = convertCoord(event.getX()) - lastX;
			int deltaY = convertCoord(event.getY()) - lastY;

			if (deltaX != 0 || deltaY != 0) {
				map.movePlaceGroupZone(placeGroupZone, deltaX, deltaY);
				//Bring the zone to the front
				map.removePlaceGroupZone(placeGroupZone);
				map.addPlaceGroupZone(0, placeGroupZone);
			}

		}
		lastX = convertCoord(event.getX());
		lastY = convertCoord(event.getY());

	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {

		if (event.getButton() != MouseButton.PRIMARY || placeGroupZone == null) {
			return;
		}

		drag(event, tile, rootNode, map);
		removeOutOfBounds(placeGroupZone, map);
		placeGroupZone = null;

		map.finishEdit("Move PlaceGroup");

	}

	@Override
	public Set<Point> getHighlight(final int x, final int y, final Tile tile, final MapEditor map) {

		Set<Point> area = new HashSet<>();

		if (!ApplicationSettings.getInstance().getBoolean(ApplicationSettings.Preference.SHOW_GROUPS).get()) {
			area.add(new Point(x, y));
			return area;
		}

		PlaceGroupZone zone = map.getPlaceGroupZoneAt(x, y);
		if (zone != null) {
			for (int ix = zone.bounds.x1; ix < zone.bounds.x2 + 1; ix++) {
				for (int iy = zone.bounds.y1; iy < zone.bounds.y2 + 1; iy++) {
					area.add(new Point(ix, iy));
				}
			}
		} else {
			area = super.getHighlight(x, y, tile, map);
		}

		return area;

	}

	@Override
	public Image getHighlightTile(final Tile tile) {
		return null;
	}

	private void removeOutOfBounds(final PlaceGroupZone zone, final MapEditor map) {

		Rectangle cropped = Rectangle.intersectionOf(new Rectangle(0, MapEditor.SIZE, 0, MapEditor.SIZE), zone.bounds);

		if (cropped.getArea() <= 0) {

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Remove PlaceGroup?");
			alert.setContentText("Are you sure you want to delete this PlaceGroup?");

			ButtonType yes = new ButtonType("Yes");
			ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(yes, no);

			alert.showAndWait().ifPresent(result -> {
				if (result == yes) {
					map.removePlaceGroupZone(zone);
				} else {
					map.movePlaceGroupZone(zone, originalBounds.x1 - placeGroupZone.bounds.x1, originalBounds.y1 - placeGroupZone.bounds.y1);
				}
			});

		}

	}

	private void editPlaceGroupZone(final PlaceGroupZone zone, final MapEditor map) {
		PlaceGroupInfoPanel infoPanel = new PlaceGroupInfoPanel("Edit PlaceGroup", zone.group);
		infoPanel.showAndWait().ifPresent(result -> {
			if (result == ButtonType.FINISH) {
				map.startEdit();
				map.modifyPlaceGroup(zone.group, infoPanel.getType(), infoPanel.getGroup(), infoPanel.getChance());
				map.finishEdit("Edit PlaceGroup");
			}
		});
	}

}
