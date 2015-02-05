package net.krazyweb.cataclysm.mapeditor.tools;

import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class PencilTool extends Tool {

	@Override
	public void click(final int x, final int y, final Tile tile, final CataclysmMap map) {
		map.setTile(x, y, tile);
	}

	@Override
	public void drag(final int x, final int y, final Tile tile, final CataclysmMap map) {
		click(x, y, tile, map);
	}

}
