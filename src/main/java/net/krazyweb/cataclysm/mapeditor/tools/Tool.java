package net.krazyweb.cataclysm.mapeditor.tools;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.ApplicationSettings;
import net.krazyweb.cataclysm.mapeditor.TileSet;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.events.ZoomChangeEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public abstract class Tool {

	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(Tool.class);

	protected static EventBus eventBus;
	protected static TileSet tileSet;

	protected static double zoom = 1.0;

	public static void setEventBus(final EventBus eventBus) {
		Tool.eventBus = eventBus;
		//Cannot subscribe to events with static references, so this instance of an empty tool is registered to receive events
		eventBus.register(new Tool(){});
		tileSet = ApplicationSettings.currentTileset;
	}

	@Subscribe
	public void tileSetLoadedEventListener(final TilesetLoadedEvent event) {
		tileSet = event.getTileSet();
	}

	@Subscribe
	public void zoomChangeEventListener(final ZoomChangeEvent event) {
		zoom = event.getZoomLevel();
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
		if (tile != null) {
			return tile.getTexture(0, 0); //TODO Bitwise map texture
		} else {
			return SwingFXUtils.toFXImage(new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR), null);
		}
	}

	protected int convertCoord(final double eventPosition) {
		return (int) (eventPosition / (tileSet.tileSize * zoom));
	}

}
