package net.krazyweb.cataclysm.mapeditor.tools;

import com.google.common.eventbus.EventBus;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.TileSet;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;

import java.util.HashSet;
import java.util.Set;

public abstract class Tool {

	protected static EventBus eventBus;

	public static void setEventBus(final EventBus eventBus) {
		Tool.eventBus = eventBus;
	}

	public void click(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {}
	public void release(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {}
	public void drag(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {}
	public void dragStart(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {}
	public void dragEnd(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {}

	public Set<Point> getHighlight(final int x, final int y, final MapTile tile, final MapEditor map) {
		Set<Point> highlight = new HashSet<>();
		highlight.add(new Point(x, y));
		return highlight;
	}

	public Image getHighlightTile(final MapTile tile) {
		return TileSet.textures.get("t_grass"); //TODO Use maptile texture
	}

	protected int convertCoord(final double eventPosition) {
		return (int) eventPosition / 32; //TODO Use tileset size
	}

}
