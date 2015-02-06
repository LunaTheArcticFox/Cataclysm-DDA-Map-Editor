package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.input.MouseButton;
import net.krazyweb.cataclysm.mapeditor.map.ItemGroup;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;

public class PlaceGroupTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void dragStart(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		startX = x;
		startY = y;
	}

	@Override
	public void dragEnd(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

		//TODO Pop up menu asking for placegroup

		int width = Math.abs(x - startX) + 1;
		int height = Math.abs(y - startY) + 1;

		if (x < startX) {
			startX = x;
		}

		if (y < startY) {
			startY = y;
		}

		map.addPlaceGroupZone(new PlaceGroupZone(startX, startY, width, height, new ItemGroup()));

	}

}
