package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.input.MouseButton;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class BoxTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void dragStart(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		startX = x;
		startY = y;
	}

	@Override
	public void dragEnd(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

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
