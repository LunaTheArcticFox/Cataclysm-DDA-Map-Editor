package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class BoxTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		startX = convertCoord(event.getX());
		startY = convertCoord(event.getY());
	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

		int x = convertCoord(event.getX());
		int y = convertCoord(event.getY());

		int xDirection = x > startX ? -1 : 1;
		int yDirection = y > startY ? -1 : 1;

		int xAmount = Math.abs(x - startX);
		int yAmount = Math.abs(y - startY);

		for (int lineX = 0; lineX <= xAmount; lineX++) {
			map.setTile(x + xDirection * lineX, startY, tile);
			map.setTile(x + xDirection * lineX, y, tile);
		}

		for (int lineY = 0; lineY <= yAmount; lineY++) {
			map.setTile(startX, y + yDirection * lineY, tile);
			map.setTile(x, y + yDirection * lineY, tile);
		}

	}

}
