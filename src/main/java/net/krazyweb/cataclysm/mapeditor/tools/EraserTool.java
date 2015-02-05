package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.input.MouseButton;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class EraserTool extends Tool {

	@Override
	public void click(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		//TODO Pick tiles better
		if (mouseButton == MouseButton.PRIMARY) {
			map.setTile(x, y, Tile.tiles.get("t_grass"));
		} else {
			map.setTile(x, y, Tile.tiles.get("f_null"));
		}
	}

	@Override
	public void drag(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {
		click(x, y, tile, mouseButton, map);
	}

}
