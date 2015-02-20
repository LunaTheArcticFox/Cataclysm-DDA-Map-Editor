package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupInfoPanel;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;

import java.util.HashSet;
import java.util.Set;

public class EditPlaceGroupTool extends Tool {

	private int lastX;
	private int lastY;

	private PlaceGroupZone placeGroupZone;

	@Override
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

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
				MenuItem cancel = new MenuItem("Cancel");

				ContextMenu menu = new ContextMenu(edit, new SeparatorMenuItem(), cancel);
				menu.setAutoHide(true);

				edit.setOnAction(action -> editPlaceGroupZone(map.getPlaceGroupZoneAt(x, y), map));
				cancel.setOnAction(action -> menu.hide());

				menu.show(rootNode, event.getScreenX(), event.getScreenY());

			}

		}

	}

	@Override
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

		if (event.getButton() != MouseButton.PRIMARY) {
			return;
		}

		lastX = convertCoord(event.getX());
		lastY = convertCoord(event.getY());
		placeGroupZone = map.getPlaceGroupZoneAt(lastX, lastY);

	}

	@Override
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

		if (event.getButton() != MouseButton.PRIMARY) {
			return;
		}

		if (placeGroupZone != null) {

			int deltaX = convertCoord(event.getX()) - lastX;
			int deltaY = convertCoord(event.getY()) - lastY;

			if (deltaX != 0 || deltaY != 0) {
				placeGroupZone.x += deltaX;
				placeGroupZone.y += deltaY;
				//Bring the zone to the front
				map.removePlaceGroupZone(placeGroupZone);
				map.addPlaceGroupZone(0, placeGroupZone);
			}

		}
		lastX = convertCoord(event.getX());
		lastY = convertCoord(event.getY());

	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

		if (event.getButton() != MouseButton.PRIMARY) {
			return;
		}

		drag(event, tile, rootNode, map);
		//TODO Crop zone to map boundaries (only on drag end)
	}

	@Override
	public Set<Point> getHighlight(final int x, final int y, final Tile tile, final CataclysmMap map) {

		Set<Point> area = new HashSet<>();

		PlaceGroupZone zone = map.getPlaceGroupZoneAt(x, y);
		if (zone != null) {
			for (int ix = zone.x; ix < zone.x + zone.w; ix++) {
				for (int iy = zone.y; iy < zone.y + zone.h; iy++) {
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

	private void editPlaceGroupZone(final PlaceGroupZone zone, final CataclysmMap map) {
		PlaceGroupInfoPanel infoPanel = new PlaceGroupInfoPanel("Edit PlaceGroup", zone.group);
		infoPanel.showAndWait().ifPresent(result -> {
			if (result == ButtonType.FINISH) {
				zone.group.type = infoPanel.getType();
				zone.group.group = infoPanel.getGroup();
				zone.group.chance = infoPanel.getChance();
				map.finishEdit("Edit PlaceGroup");
			}
		});
	}

}
