package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroup;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupInfoPanel;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;

public class CreatePlaceGroupTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		if (event.getButton() == MouseButton.PRIMARY) {
			startX = convertCoord(event.getX());
			startY = convertCoord(event.getY());
			createPlaceGroupZone(convertCoord(event.getX()), convertCoord(event.getY()), map);
		}
	}

	@Override
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		if (event.getButton() == MouseButton.PRIMARY) {
			startX = convertCoord(event.getX());
			startY = convertCoord(event.getY());
		}
	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		if (event.getButton() == MouseButton.PRIMARY) {
			createPlaceGroupZone(convertCoord(event.getX()), convertCoord(event.getY()), map);
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
