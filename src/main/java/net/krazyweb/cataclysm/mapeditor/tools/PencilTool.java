package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class PencilTool extends Tool {

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		if (event.getButton() == MouseButton.PRIMARY) {
			map.setTile(convertCoord(event.getX()), convertCoord(event.getY()), tile);
		}
	}

	@Override
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		click(event, tile, rootNode, map);
	}

}
