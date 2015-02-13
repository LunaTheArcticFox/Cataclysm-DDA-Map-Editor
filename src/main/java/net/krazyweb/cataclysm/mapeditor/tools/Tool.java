package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public abstract class Tool {

	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}

	protected int convertCoord(final double eventPosition) {
		return (int) eventPosition / 32; //TODO Use tileset size
	}

}
