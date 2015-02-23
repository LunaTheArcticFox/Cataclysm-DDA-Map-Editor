package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;

public class PencilTool extends Tool {

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		if (event.getButton() == MouseButton.PRIMARY) {
			/*if (event.isAltDown()) {
				CataclysmMap.Layer layer = CataclysmMap.Layer.FURNITURE;
				if (map.getFurnitureAt(convertCoord(event.getX()), convertCoord(event.getY())).equals("f_null")) {
					layer = CataclysmMap.Layer.TERRAIN;
				}
				eventBus.post(new TilePickedEvent(Tile.tiles.get(map.getTileAt(convertCoord(event.getX()), convertCoord(event.getY()), layer))));
			} else {*/
			map.startEdit();
			map.setTile(convertCoord(event.getX()), convertCoord(event.getY()), tile);
			//}
		}
	}

	@Override
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		map.finishEdit("Pencil");
	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		map.finishEdit("Pencil");
	}

	@Override
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		click(event, tile, rootNode, map);
	}

}
