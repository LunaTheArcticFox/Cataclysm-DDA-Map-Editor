package net.krazyweb.cataclysm.mapeditor.tools;

import com.google.common.eventbus.EventBus;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

import java.util.ArrayList;
import java.util.List;

public abstract class Tool {

	protected static EventBus eventBus;

	public static void setEventBus(final EventBus eventBus) {
		Tool.eventBus = eventBus;
	}

	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void drag(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {}

	public List<Point> getHighlight(final int x, final int y, final Tile tile, final CataclysmMap map) {
		List<Point> highlight = new ArrayList<>();
		highlight.add(new Point(x, y));
		return highlight;
	}

	protected int convertCoord(final double eventPosition) {
		return (int) eventPosition / 32; //TODO Use tileset size
	}

}
