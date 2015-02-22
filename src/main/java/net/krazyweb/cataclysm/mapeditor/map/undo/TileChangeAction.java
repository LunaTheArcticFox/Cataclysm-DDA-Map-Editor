package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileChangeAction implements Action {

	private Logger log = LogManager.getLogger(TileChangeAction.class);

	private CataclysmMap map;
	private CataclysmMap.Layer layer;
	private Point location;
	private String before, after;

	public TileChangeAction(final CataclysmMap map, final CataclysmMap.Layer layer, final Point location, final String before, final String after) {
		this.map = map;
		this.layer = layer;
		this.location = location;
		this.before = before;
		this.after = after;
	}

	public void execute() {
		log.trace("Redoing on map '" + map + "' - " + layer.name() + ", " + location + ", " + before + " -> " + after);
		map.setTile(location, layer, after);
	}

	public void undo() {
		log.trace("Undoing on map '" + map + "' - " + layer.name() + ", " + location + ", " + after + " -> " + before);
		map.setTile(location, layer, before);
	}

}
