package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.TileSet;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;

public class EraserTool extends Tool {

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		//TODO Pick tiles better
		if (event.getButton() == MouseButton.PRIMARY) {
			map.startEdit();
			map.setTile(convertCoord(event.getX()), convertCoord(event.getY()), Tile.tiles.get("t_grass"));
			map.setTile(convertCoord(event.getX()), convertCoord(event.getY()), Tile.tiles.get("f_null"));
		}
	}

	@Override
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		map.finishEdit("Eraser");
	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		map.finishEdit("Eraser");
	}

	@Override
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {
		click(event, tile, rootNode, map);
	}

	@Override
	public Image getHighlightTile(final Tile tile) {
		return TileSet.textures.get("t_grass");
	}

}
