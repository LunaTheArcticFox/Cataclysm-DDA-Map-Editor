package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class EraserTool extends Tool {

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		//TODO Pick tiles better
		if (event.getButton() == MouseButton.PRIMARY) {
			map.setTile(convertCoord(event.getX()), convertCoord(event.getY()), Tile.tiles.get("t_grass"));
		} else {
			map.setTile(convertCoord(event.getX()), convertCoord(event.getY()), Tile.tiles.get("f_null"));
		}
	}

	@Override
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		click(event, tile, rootNode, map);
	}

}
