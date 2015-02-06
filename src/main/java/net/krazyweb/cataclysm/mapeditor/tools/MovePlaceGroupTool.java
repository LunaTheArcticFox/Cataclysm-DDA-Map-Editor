package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.input.MouseButton;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;

public class MovePlaceGroupTool extends Tool {

	private int lastX;
	private int lastY;

	private PlaceGroupZone placeGroupZone;

	@Override
	public void release(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		if (mouseButton != MouseButton.PRIMARY) {
			return;
		}
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
	}

	@Override
	public void dragStart(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

		if (mouseButton != MouseButton.PRIMARY) {
			return;
		}

		lastX = x;
		lastY = y;
		placeGroupZone = map.getPlaceGroupZoneAt(x, y);

	}

	@Override
	public void drag(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

		if (mouseButton != MouseButton.PRIMARY) {
			return;
		}

		if (placeGroupZone != null) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

			if (deltaX != 0 || deltaY != 0) {
				placeGroupZone.x += deltaX;
				placeGroupZone.y += deltaY;
				//Bring the zone to the front
				map.removePlaceGroupZone(placeGroupZone);
				map.addPlaceGroupZone(0, placeGroupZone);
			}

		}
		lastX = x;
		lastY = y;

	}

	@Override
	public void dragEnd(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

		if (mouseButton != MouseButton.PRIMARY) {
			return;
		}

		drag(x, y, tile, mouseButton, map);
		//TODO Crop zone to map boundaries (only on drag end)
	}

}
