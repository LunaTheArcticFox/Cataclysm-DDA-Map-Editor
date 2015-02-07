package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroup;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupInfoPanel;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;

public class CreatePlaceGroupTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void release(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		if (mouseButton == MouseButton.PRIMARY) {
			startX = x;
			startY = y;
			createPlaceGroupZone(x, y, map);
		}
	}

	@Override
	public void dragStart(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		if (mouseButton == MouseButton.PRIMARY) {
			startX = x;
			startY = y;
		}
	}

	@Override
	public void dragEnd(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		if (mouseButton == MouseButton.PRIMARY) {
			createPlaceGroupZone(x, y, map);
		}
	}

	private void createPlaceGroupZone(final int x, final int y, final CataclysmMap map) {

		PlaceGroupInfoPanel placeGroupInfoPanel = new PlaceGroupInfoPanel("Create New PlaceGroup");
		placeGroupInfoPanel.showAndWait().ifPresent(result -> {

			if (result == ButtonType.FINISH) {

				PlaceGroup placeGroup = new PlaceGroup();
				placeGroup.type = placeGroupInfoPanel.getType();
				placeGroup.group = placeGroupInfoPanel.getGroup();
				placeGroup.chance = placeGroupInfoPanel.getChance();

				int width = Math.abs(x - startX) + 1;
				int height = Math.abs(y - startY) + 1;

				if (x < startX) {
					startX = x;
				}

				if (y < startY) {
					startY = y;
				}

				map.addPlaceGroupZone(new PlaceGroupZone(startX, startY, width, height, placeGroup));

			}

		});

	}

}
