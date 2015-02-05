package net.krazyweb.cataclysm.mapeditor.tools;

import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class LineTool extends Tool {

	private int startX;
	private int startY;

	@Override
	public void dragStart(final int x, final int y, final Tile tile, final CataclysmMap map) {
		startX = x;
		startY = y;
	}

	@Override
	public void dragEnd(final int x, final int y, final Tile tile, final CataclysmMap map) {

		int delta = 0;

		int dirX = Math.abs(x - startX);
		int dirY = Math.abs(y - startY);

		int dirX2 = (dirX << 1);
		int dirY2 = (dirY << 1);

		int ix = startX < x ? 1 : -1;
		int iy = startY < y ? 1 : -1;

		if (dirY <= dirX) {
			while (true) {
				map.setTile(startX, startY, tile);
				if (startX == x) {
					break;
				}
				startX += ix;
				delta += dirY2;
				if (delta > dirX) {
					startY += iy;
					delta -= dirX2;
				}
			}
		} else {
			while (true) {
				map.setTile(startX, startY, tile);
				if (startY == y) {
					break;
				}
				startY += iy;
				delta += dirX2;
				if (delta > dirY) {
					startX += ix;
					delta -= dirY2;
				}
			}
		}

	}

}
